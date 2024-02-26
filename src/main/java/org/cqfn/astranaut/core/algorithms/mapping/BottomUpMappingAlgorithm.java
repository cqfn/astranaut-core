/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.core.algorithms.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.Depth;
import org.cqfn.astranaut.core.algorithms.hash.AbsoluteHash;
import org.cqfn.astranaut.core.algorithms.hash.Hash;

/**
 * Bottom-up mapping algorithm.
 * Tries to match leaf nodes first, and then subtrees containing leaf nodes,
 * gradually increasing the size of the matched subtrees.
 *
 * @since 1.1.0
 */
class BottomUpMappingAlgorithm {
    /**
     * Set of node hashes.
     */
    private final Hash hashes;

    /**
     * Set of node depths.
     */
    private final Depth depth;

    /**
     * Relationship of the nodes to their parents.
     */
    private final Map<Node, Node> parents;

    /**
     * Sorted nodes from the 'left' tree.
     */
    private final List<Node> left;

    /**
     * Sorted nodes from the 'right' tree.
     */
    private final List<Node> right;

    /**
     * Left-to-right mapping.
     */
    private final Map<Node, Node> ltr;

    /**
     * Right-to-left mapping.
     */
    private final Map<Node, Node> rtl;

    /**
     * Map containing replaces nodes.
     */
    private final Map<Node, Node> replaced;

    /**
     * Set of deleted nodes.
     */
    private final Set<Node> deleted;

    /**
     * Constructor.
     * @param left Root node of the 'left' tree
     * @param right Root node of the 'right' tree
     */
    BottomUpMappingAlgorithm(final Node left, final Node right) {
        this.hashes = new AbsoluteHash();
        this.depth = new Depth();
        this.parents = new HashMap<>();
        this.left = this.createNodeList(left);
        this.right = this.createNodeList(right);
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
        this.replaced = new HashMap<>();
        this.deleted = new HashSet<>();
    }

    /**
     * Performs the mapping.
     */
    void execute() {
        final Map<Node, List<Node>> draft = this.performInitialMapping();
        this.absorbLargestSubtrees(draft);
        Node node = this.findPartiallyMappedLeftNode();
        while (node != null) {
            node = this.mapPartiallyMappedLeftNode(node);
            if (node == null) {
                node = this.findPartiallyMappedLeftNode();
            }
        }
    }

    /**
     * Returns result of mapping.
     * @return Result of mapping
     */
    Mapping getResult() {
        return new Mapping() {
            @Override
            public Node getRight(final Node node) {
                return BottomUpMappingAlgorithm.this.ltr.get(node);
            }

            @Override
            public Node getLeft(final Node node) {
                return BottomUpMappingAlgorithm.this.rtl.get(node);
            }

            @Override
            public Map<Node, Node> getReplaced() {
                return Collections.unmodifiableMap(BottomUpMappingAlgorithm.this.replaced);
            }

            @Override
            public Set<Node> getDeleted() {
                return Collections.unmodifiableSet(BottomUpMappingAlgorithm.this.deleted);
            }
        };
    }

    /**
     * Creates an initial list of nodes suitable for processing from the tree.
     * @param root The root of the tree
     * @return List of nodes where leaves are placed first.
     */
    private List<Node> createNodeList(final Node root) {
        final List<Node> list = new LinkedList<>();
        this.createNodeList(root, null, list);
        return list;
    }

    /**
     * Creates an initial set of nodes suitable for processing from the tree (recursive method).
     * @param node The current node
     * @param parent The current node parent
     * @param list The resulting list
     */
    private void createNodeList(final Node node, final Node parent, final List<Node> list) {
        this.parents.put(node, parent);
        node.forEachChild(child -> this.createNodeList(child, node, list));
        list.add(node);
    }

    /**
     * Performs hash calculation of nodes from the 'right' set.
     * @return The hash relation to the list of nodes that have such a hash
     */
    private Map<Integer, List<Node>> calculateRightHashes() {
        final Map<Integer, List<Node>> result = new TreeMap<>();
        for (final Node node : this.right) {
            final int hash = this.hashes.calculate(node);
            final List<Node> list =
                result.computeIfAbsent(hash, k -> new ArrayList<>(1));
            list.add(node);
        }
        return result;
    }

    /**
     * Performs initial (draft) node mapping.
     * @return Relationships of nodes to lists of nodes to which they can be mapped to
     */
    private Map<Node, List<Node>> performInitialMapping() {
        final Map<Node, List<Node>> result = new HashMap<>();
        final Map<Integer, List<Node>> relation = this.calculateRightHashes();
        for (final Node node : this.left) {
            final int hash = this.hashes.calculate(node);
            final List<Node> list = relation.get(hash);
            if (list != null) {
                result.put(node, list);
            }
        }
        return result;
    }

    /**
     * Selects the largest size subtrees from the initial node relation and maps them.
     * @param draft Initial node relation
     */
    private void absorbLargestSubtrees(final Map<Node, List<Node>> draft) {
        final List<Node> sorted = new ArrayList<>(draft.keySet());
        sorted.sort(
            (first, second) -> -Integer.compare(
                this.depth.calculate(first),
                this.depth.calculate(second)
            )
        );
        for (final Node node : sorted) {
            final List<Node> related = draft.get(node);
            if (related != null && related.size() == 1 && !this.ltr.containsKey(node)) {
                this.mapSubtreesWithTheSameHash(node, related.get(0), draft);
            }
            if (draft.isEmpty()) {
                break;
            }
        }
    }

    /**
     * Maps subtrees with the same hash, adding the corresponding nodes to the resulting
     * collections and removing them from the initial mapping.
     * @param node Left node
     * @param related Related node to the left node
     * @param draft Initial node relation
     */
    private void mapSubtreesWithTheSameHash(
        final Node node,
        final Node related,
        final Map<Node, List<Node>> draft) {
        assert this.hashes.calculate(node) == this.hashes.calculate(related);
        draft.remove(node);
        this.left.remove(node);
        this.right.remove(related);
        this.ltr.put(node, related);
        this.rtl.put(related, node);
        final int count = node.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final Node first = node.getChild(index);
            final Node second = related.getChild(index);
            this.mapSubtreesWithTheSameHash(first, second, draft);
        }
    }

    /**
     * Finds a partially mapped node from the 'left' set, that is, one that has some children
     * mapped and some not.
     * @return A node or {@code null} if such node not found
     */
    private Node findPartiallyMappedLeftNode() {
        Node result = null;
        final Iterator<Node> iterator = this.left.iterator();
        while (result == null && iterator.hasNext()) {
            final Node node = iterator.next();
            final int count = node.getChildCount();
            for (int index = 0; index < count; index = index + 1) {
                final Node child = node.getChild(index);
                if (this.ltr.containsKey(child)) {
                    result = node;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Tries to map a partially mapped 'left' node to another node.
     * @param node A node to be mapped
     * @return Next partially mapped node to be processed
     */
    private Node mapPartiallyMappedLeftNode(final Node node) {
        final Set<Node> ancestors = new HashSet<>();
        Node next = null;
        node.forEachChild(
            child -> {
                final Node mapped = this.ltr.get(child);
                if (mapped != null) {
                    ancestors.add(this.parents.get(mapped));
                }
            }
        );
        do {
            if (ancestors.size() != 1) {
                break;
            }
            final Node related = ancestors.iterator().next();
            if (!node.getTypeName().equals(related.getTypeName())
                || !node.getData().equals(related.getData())) {
                break;
            }
            this.right.remove(related);
            this.ltr.put(node, related);
            this.rtl.put(related, node);
            this.mapChildren(node, related);
            next = this.parents.get(node);
        } while (false);
        this.left.remove(node);
        return next;
    }

    /**
     * Maps the child nodes of partially mapped nodes.
     * @param before Node before changes
     * @param after Node after changes
     */
    private void mapChildren(final Node before, final Node after) {
        final int sign = Integer.compare(before.getChildCount(), after.getChildCount());
        if (sign > 0) {
            this.mapChildrenIfDeleted(before);
        } else if (sign == 0) {
            this.mapChildrenIfReplaced(before, after);
        }
    }

    /**
     * Maps the child nodes of partially mapped nodes if the node before changes
     * has the same number of child nodes as the node after changes, i.e., when it is obvious
     * that some nodes have been replaced.
     * @param before Node before changes
     * @param after Node after changes
     */
    private void mapChildrenIfReplaced(final Node before, final Node after) {
        final int count = before.getChildCount();
        assert count == after.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final Node first = before.getChild(index);
            if (!this.ltr.containsKey(first)) {
                final Node second = after.getChild(index);
                this.replaced.put(first, second);
                this.left.remove(first);
                this.right.remove(second);
            }
        }
    }

    /**
     * Maps the child nodes of partially mapped nodes if the node before changes
     * has more child nodes than the node after changes, i.e., when it is obvious
     * that some nodes have been deleted.
     * @param before Node before changes
     */
    private void mapChildrenIfDeleted(final Node before) {
        final int count = before.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final Node child = before.getChild(index);
            if (!this.ltr.containsKey(child)) {
                this.deleted.add(child);
                this.left.remove(child);
            }
        }
    }
}
