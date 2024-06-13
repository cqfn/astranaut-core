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
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.PatternNode;

/**
 * Pattern builder.
 *
 * @since 1.1.5
 */
public final class PatternBuilder {
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
    private final PatternNode root;

    /**
     * Constructor.
     * @param diff Node of the differential tree from which the pattern is built
     */
    public PatternBuilder(final DifferenceNode diff) {
        this.root = new PatternNode(diff);
        this.info = PatternBuilder.buildNodeInfoMap(this.root);
    }

    /**
     * Returns root of resulting pattern.
     * @return Root node of pattern
     */
    public PatternNode getRoot() {
        return this.root;
    }

    /**
     * Turns a child node into a hole.
     * @param node Child node
     * @param number Hole number
     * @return Result of operation, @return {@code true} if node was transformer
     */
    public boolean makeHole(final Node node, final int number) {
        boolean result = false;
        final PatternNode parent =
            this.info.getOrDefault(node, PatternBuilder.DEFAULT_INFO).getParent();
        if (parent != null) {
            result = parent.makeHole(node, number);
        }
        return result;
    }

    /**
     * Builds the map containing relationship of the nodes to their parents.
     * @param root Root node
     * @return The map containing relationship of the nodes to their parents.
     */
    private static Map<Node, NodeInfo> buildNodeInfoMap(final PatternNode root) {
        final Map<Node, NodeInfo> map = new HashMap<>();
        map.put(root.getPrototype(), new NodeInfo(root, null));
        PatternBuilder.buildNodeInfoMap(map, root);
        return map;
    }

    /**
     * Builds the map containing relationship of the nodes to their parents (recursive method).
     * @param map Where to put the results
     * @param parent Parent node
     */
    private static void buildNodeInfoMap(final Map<Node, NodeInfo> map, final PatternNode parent) {
        parent.forEachChild(
            child -> {
                if (child instanceof PatternNode) {
                    final PatternNode node = (PatternNode) child;
                    map.put(node.getPrototype(), new NodeInfo(node, parent));
                    PatternBuilder.buildNodeInfoMap(map, node);
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
         * The corresponding pattern node.
         */
        private final PatternNode pnode;

        /**
         * The parent node.
         */
        private final PatternNode parent;

        /**
         * Constructor.
         * @param pnode The corresponding pattern node
         * @param parent The parent node
         */
        NodeInfo(final PatternNode pnode, final PatternNode parent) {
            this.pnode = pnode;
            this.parent = parent;
        }

        /**
         * Returns corresponding patter node.
         * @return Pattern node
         */
        public PatternNode getPatternNode() {
            return this.pnode;
        }

        /**
         * Returns parent node.
         * @return Pattern node containing this node
         */
        public PatternNode getParent() {
            return this.parent;
        }
    }
}
