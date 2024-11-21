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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.utils.Pair;

/**
 * Top-down mapping algorithm.
 *  Compares root nodes first and then children in depth.
 * @since 1.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
final class TopDownAlgorithmNew {
    /**
     * Left-to-right mapping.
     */
    private final Map<ExtNode, ExtNode> ltr;

    /**
     * Right-to-left mapping.
     */
    private final Map<ExtNode, ExtNode> rtl;

    /**
     * Set containing inserted nodes.
     */
    private final List<ExtInsertion> inserted;

    /**
     * Map containing replaces nodes.
     */
    private final Map<ExtNode, ExtNode> replaced;

    /**
     * Set of deleted nodes.
     */
    private final Set<ExtNode> deleted;

    /**
     * Constructor.
     */
    TopDownAlgorithmNew() {
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
        this.inserted = new ArrayList<>(0);
        this.replaced = new HashMap<>();
        this.deleted = new HashSet<>();
    }

    /**
     * Performs the mapping.
     * @param left Root node of the 'left' tree
     * @param right Root node of the 'right' tree
     */
    void execute(final ExtNode left, final ExtNode right) {
        final boolean result = this.mapSubtrees(left, right);
        if (!result) {
            this.replaced.put(left, right);
            this.skipLeftSubtree(left);
            this.skipRightSubtree(right);
        }
    }

    /**
     * Returns left-to-right mapping.
     * @return Collection containing nodes from the left tree and corresponding nodes
     *  from the right tree
     */
    Map<ExtNode, ExtNode> getLeftToRight() {
        return this.ltr;
    }

    /**
     * Returns right-to-left mapping.
     * @return Collection containing nodes from the right tree and corresponding nodes
     *  from the left tree
     */
    Map<ExtNode, ExtNode> getRightToLeft() {
        return this.rtl;
    }

    /**
     * Returns inserted nodes.
     * @return Ordered list of insertions
     */
    List<ExtInsertion> getInserted() {
        return this.inserted;
    }

    /**
     * Returns replaces nodes.
     * @return Collection containing replaced nodes (key - node before replacement,
     *  value - after replacement)
     */
    Map<ExtNode, ExtNode> getReplaced() {
        return this.replaced;
    }

    /**
     * Returns deleted nodes.
     * @return Set of deleted nodes
     */
    Set<ExtNode> getDeleted() {
        return this.deleted;
    }

    /**
     * Performs a mapping of two subtrees.
     * @param left Root node of the left subtree
     * @param right Root node of the right subtree
     * @return Mapping result {@code true} if mapping was performed, {@code false} if subtrees
     *  can't be mapped
     */
    private boolean mapSubtrees(final ExtNode left, final ExtNode right) {
        final boolean result;
        if (left.getAbsoluteHash() == right.getAbsoluteHash()) {
            this.mapSubtreesWithTheSameAbsoluteHash(left, right);
            result = true;
        } else if (left.getLocalHash() == right.getLocalHash()) {
            this.mapSubtreesWithTheSameLocalHash(left, right);
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Skips the left subtree, considering that all its nodes cannot be matched.
     * @param node Root node of the subtree
     */
    private void skipLeftSubtree(final ExtNode node) {
        this.ltr.put(node, null);
        for (int index = 0; index < node.getChildCount(); index = index + 1) {
            this.skipLeftSubtree(node.getExtChild(index));
        }
    }

    /**
     * Skips the right subtree, considering that all its nodes cannot be matched.
     * @param node Root node of the subtree
     */
    private void skipRightSubtree(final ExtNode node) {
        this.rtl.put(node, null);
        for (int index = 0; index < node.getChildCount(); index = index + 1) {
            this.skipRightSubtree(node.getExtChild(index));
        }
    }

    /**
     * Maps subtrees with the same absolute hash, adding the corresponding nodes to the resulting
     *  collections.
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     */
    private void mapSubtreesWithTheSameAbsoluteHash(final ExtNode left, final ExtNode right) {
        this.ltr.put(left, right);
        this.rtl.put(right, left);
        for (int index = 0; index < left.getChildCount(); index = index + 1) {
            this.mapSubtreesWithTheSameAbsoluteHash(
                left.getExtChild(index),
                right.getExtChild(index)
            );
        }
    }

    /**
     * Maps subtrees with the same local hash, adding the corresponding nodes to the resulting
     *  collections.
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     */
    private void mapSubtreesWithTheSameLocalHash(final ExtNode left, final ExtNode right) {
        this.ltr.put(left, right);
        this.rtl.put(right, left);
        do {
            if (left.getChildCount() == 0) {
                this.insertAllNodes(left, right);
                break;
            }
            if (right.getChildCount() == 0) {
                this.deleteAllNodes(left);
                break;
            }
            final Unprocessed unprocessed = new Unprocessed(left, right);
            final NodePairFinder finder = new NodePairFinder();
            finder.fill(left, right);
            boolean flag = this.mapAllIdenticalChildren(finder, unprocessed);
            this.mapAllUnmappedNodes(left, right, unprocessed);
        } while(false);
    }

    /**
     * Inserts all nodes from the right subtree into the left subtree
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     */
    private void insertAllNodes(final ExtNode left, final ExtNode right) {
        ExtNode after = null;
        for (int index = 0; index < right.getChildCount(); index = index + 1) {
            final ExtNode child = right.getExtChild(index);
            this.inserted.add(new ExtInsertion(child, left, after));
            this.rtl.put(child, null);
            after = child;
        }
    }

    /**
     * Marks all child nodes as deleted.
     * @param node A node whose child nodes are deleted
     */
    private void deleteAllNodes(final ExtNode node) {
        for (int index = 0; index < node.getChildCount(); index = index + 1) {
            final ExtNode child = node.getExtChild(index);
            this.deleted.add(child);
            this.ltr.put(child, null);
        }
    }

    /**
     * Mapped all child nodes with the same absolute hash.
     * @param finder Algorithm that finds the most matching pairs of nodes
     * @param unprocessed Unprocessed nodes
     * @return Operation result, {@code true} if at least one such pair of nodes was mapped
     */
    private boolean mapAllIdenticalChildren(final NodePairFinder finder,
        final Unprocessed unprocessed) {
        final  boolean result;
        Pair<ExtNode, ExtNode> identical = finder.getBestPairOfIdenticalNodes();
        if (identical != null) {
            while (identical != null) {
                unprocessed.markAsMapped(
                    identical.getKey().getIndex(),
                    identical.getValue().getIndex()
                );
                this.mapSubtreesWithTheSameAbsoluteHash(identical.getKey(), identical.getValue());
                identical = finder.getRightPairOfIdenticalNodes(identical);
            }
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Maps all nodes that have not yet been mapped.
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     * @param unprocessed Unprocessed nodes
     */
    private void mapAllUnmappedNodes(final ExtNode left, final ExtNode right,
        final Unprocessed unprocessed) {
        while (unprocessed.hasAtLeastOneNode()) {
            if (!unprocessed.hasAtLeastOneLeftNode() && unprocessed.hasAtLeastOneRightNode()) {
                this.insertAllUnmappedNodes(left, right, unprocessed);
                break;
            }
            if (!unprocessed.hasAtLeastOneRightNode() && unprocessed.hasAtLeastOneLeftNode()) {
                this.deleteAllUnmappedNodes(left, unprocessed);
                break;
            }

            this.replaceTwoFirstUnmappedNodes(left, right, unprocessed);
        }
    }

    /**
     * Inserts all nodes that have not yet been processed from the right subtree
     *  into the left subtree.
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     * @param unprocessed Unprocessed nodes
     */
    private void insertAllUnmappedNodes(final ExtNode left, final ExtNode right,
        final Unprocessed unprocessed) {
        int index = unprocessed.getFirstUnprocessedRightIndex();
        int previous = index - 1;
        ExtNode after = null;
        while (index >= 0) {
            final ExtNode child = right.getExtChild(index);
            if (index > previous + 1 || after == null) {
                after = child.getLeft();
            }
            this.inserted.add(new ExtInsertion(child, left, after));
            this.rtl.put(child, null);
            unprocessed.markAsInserted(index);
            previous = index;
            after = child;
            index = unprocessed.getFirstUnprocessedRightIndex();
        }
    }

    /**
     * Marks all child nodes that have not yet been processed as deleted nodes.
     * @param node A node whose child nodes are deleted
     * @param unprocessed Unprocessed nodes
     */
    private void deleteAllUnmappedNodes(final ExtNode node, final Unprocessed unprocessed) {
        int index = unprocessed.getFirstUnprocessedLeftIndex();
        while (index >= 0) {
            final ExtNode child = node.getExtChild(index);
            this.deleted.add(child);
            this.ltr.put(child, null);
            unprocessed.markAsDeleted(index);
            index = unprocessed.getFirstUnprocessedLeftIndex();
        }
    }

    /**
     * Replaces the first two unprocessed nodes. This is a “dangerous” operation, as such
     *  replacement may not be optimal. More optimal algorithms should have to work out
     *  before calling this method. However, we still use this method as it will allow the
     *  mapping algorithm to terminate (not loop) in any case.
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     * @param unprocessed Unprocessed nodes
     */
    private void replaceTwoFirstUnmappedNodes(final ExtNode left, final ExtNode right,
        final Unprocessed unprocessed) {
        final int leftidx = unprocessed.getFirstUnprocessedLeftIndex();
        final ExtNode first = left.getExtChild(leftidx);
        final int rightidx = unprocessed.getFirstUnprocessedRightIndex();
        final ExtNode second = right.getExtChild(rightidx);
        this.replaced.put(first, second);
        this.ltr.put(first, second);
        this.rtl.put(second, first);
        unprocessed.markAsMapped(leftidx, rightidx);
    }
}
