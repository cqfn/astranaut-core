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
package org.cqfn.astranaut.core.algorithms;

import java.util.HashMap;
import java.util.Map;
import org.cqfn.astranaut.core.DifferenceNode;
import org.cqfn.astranaut.core.Insertion;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.mapping.Mapper;
import org.cqfn.astranaut.core.algorithms.mapping.Mapping;

/**
 * Builder of difference syntax tree, that is, one that stores changes between two trees.
 *
 * @since 1.1.0
 */
public final class DifferenceTreeBuilder {
    /**
     * Default node info (to avoid null checks).
     */
    private static final NodeInfo DEFAULT_INFO = new NodeInfo(null, null);

    /**
     * The relationship of the nodes to their parents and corresponding difference nodes.
     * This information is necessary to implement algorithms for inserting, removing
     * and replacing nodes.
     */
    private final Map<Node, NodeInfo> info;

    /**
     * Root node.
     */
    private final DifferenceNode root;

    /**
     * Constructor.
     * @param before Root node of an 'ordinary', non-difference original tree before the changes
     */
    public DifferenceTreeBuilder(final Node before) {
        this.root = new DifferenceNode(before);
        this.info = DifferenceTreeBuilder.buildNodeInfoMap(this.root);
    }

    /**
     * Builds a difference tree based on the original tree and the tree after changes.
     * @param after Root node of tree before the changes
     * @param mapper A mapper used for node mappings
     * @return Result of operation, {@code true} if difference tree was built
     */
    public boolean build(final Node after, final Mapper mapper) {
        final Mapping mapping = mapper.map(this.root.getPrototype(), after);
        boolean result = true;
        for (final Insertion insertion : mapping.getInserted()) {
            result = result & this.insertNode(insertion);
        }
        for (final Map.Entry<Node, Node> replaced : mapping.getReplaced().entrySet()) {
            result = result & this.replaceNode(replaced.getKey(), replaced.getValue());
        }
        for (final Node deleted : mapping.getDeleted()) {
            result = result & this.deleteNode(deleted);
        }
        return result;
    }

    /**
     * Returns root of resulting difference tree.
     * @return Root node of difference tree
     */
    public DifferenceNode getRoot() {
        return this.root;
    }

    /**
     * Adds an action to the difference tree that inserts a node after another node.
     * If no other node is specified, inserts at the beginning of the children's list.
     * @param insertion Full information about the node being inserted
     * @return Result of operation, {@code true} if action was added
     */
    public boolean insertNode(final Insertion insertion) {
        boolean result = false;
        DifferenceNode parent = this.info.getOrDefault(
            insertion.getInto(),
            DifferenceTreeBuilder.DEFAULT_INFO
        ).getDiff();
        if (parent == null) {
            parent = this.info.getOrDefault(
                insertion.getAfter(),
                DifferenceTreeBuilder.DEFAULT_INFO
            ).getParent();
        }
        if (parent != null) {
            result = parent.insertNodeAfter(insertion.getNode(), insertion.getAfter());
        }
        return result;
    }

    /**
     * Adds an action to the difference tree that replaces a node.
     * @param node Child element that will be replaced
     * @param replacement Child element to be replaced by
     * @return Result of operation, {@code true} if action was added
     */
    public boolean replaceNode(final Node node, final Node replacement) {
        boolean result = false;
        final DifferenceNode parent =
            this.info.getOrDefault(node, DifferenceTreeBuilder.DEFAULT_INFO).getParent();
        if (parent != null) {
            result = parent.replaceNode(node, replacement);
        }
        return result;
    }

    /**
     * Adds an action to the difference tree that removes a node.
     * @param node The node to be removed
     * @return Result of operation, {@code true} if action was added
     */
    public boolean deleteNode(final Node node) {
        boolean result = false;
        final DifferenceNode parent =
            this.info.getOrDefault(node, DifferenceTreeBuilder.DEFAULT_INFO).getParent();
        if (parent != null) {
            result = parent.deleteNode(node);
        }
        return result;
    }

    /**
     * Builds the map containing relationship of the nodes to their parents.
     * @param root Root node
     * @return The map containing relationship of the nodes to their parents.
     */
    private static Map<Node, NodeInfo> buildNodeInfoMap(final DifferenceNode root) {
        final Map<Node, NodeInfo> map = new HashMap<>();
        map.put(root.getPrototype(), new NodeInfo(root, null));
        DifferenceTreeBuilder.buildNodeInfoMap(map, root);
        return map;
    }

    /**
     * Builds the map containing relationship of the nodes to their parents (recursive method).
     * @param map Where to put the results
     * @param parent Parent node
     */
    private static void buildNodeInfoMap(
        final Map<Node, NodeInfo> map,
        final DifferenceNode parent) {
        parent.forEachChild(
            child -> {
                if (child instanceof DifferenceNode) {
                    final DifferenceNode node = (DifferenceNode) child;
                    map.put(node.getPrototype(), new NodeInfo(node, parent));
                    DifferenceTreeBuilder.buildNodeInfoMap(map, node);
                }
            }
        );
    }

    /**
     * Some additional information about each node needed to insert, replace, or delete nodes.
     *
     * @since 1.1.0
     */
    private static final class NodeInfo {
        /**
         * The corresponding difference node.
         */
        private final DifferenceNode diff;

        /**
         * The parent node.
         */
        private final DifferenceNode parent;

        /**
         * Constructor.
         * @param diff The corresponding difference node
         * @param parent The parent node
         */
        NodeInfo(final DifferenceNode diff, final DifferenceNode parent) {
            this.diff = diff;
            this.parent = parent;
        }

        /**
         * Returns corresponding difference node.
         * @return Difference node
         */
        public DifferenceNode getDiff() {
            return this.diff;
        }

        /**
         * Returns parent node.
         * @return Difference node containing this node
         */
        public DifferenceNode getParent() {
            return this.parent;
        }
    }
}
