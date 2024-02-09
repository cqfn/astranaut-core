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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    private final Hash hash;

    /**
     * Set of node depths.
     */
    private final Depth depth;

    /**
     * Not yet processed nodes from the 'left' tree.
     */
    private final Set<ExtNode> left;

    /**
     * Not yet processed nodes from the 'right' tree.
     */
    private final Set<ExtNode> right;

    /**
     * Left-to-right mapping.
     */
    private final Map<Node, Node> ltr;

    /**
     * Right-to-left mapping.
     */
    private final Map<Node, Node> rtl;


    /**
     * Constructor.
     * @param left Root node of the 'left' tree
     * @param right Root node of the 'right' tree
     */
    BottomUpMappingAlgorithm(final Node left, final Node right) {
        this.hash = new AbsoluteHash();
        this.depth = new Depth();
        this.left = this.createNodeSet(left);
        this.right = this.createNodeSet(right);
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
    }

    /**
     * Performs the mapping.
     */
    void execute() {

    }

    /**
     * Returns result of mapping.
     * @return Result of mapping
     */
    Mapping getResult() {
        return new Mapping() {
            @Override
            public Node getRight(final Node left) {
                return BottomUpMappingAlgorithm.this.ltr.get(left);
            }

            @Override
            public Node getLeft(Node right) {
                return BottomUpMappingAlgorithm.this.rtl.get(right);
            }
        };
    }

    /**
     * Creates an initial set of nodes suitable for processing from the tree.
     * @param root The root of the tree
     * @return Set of extended nodes
     */
    private Set<ExtNode> createNodeSet(final Node root) {
        final Set<ExtNode> set = new HashSet<>();
        this.createNodeSet(root, null, set);
        return set;
    }

    /**
     * Creates an initial set of nodes suitable for processing from the tree (recursive method).
     * @param node The current node
     * @param parent The current node parent
     * @param set The resulting set
     */
    private void createNodeSet(final Node node, final Node parent, final Set<ExtNode> set) {
        set.add(new ExtNode(node, parent));
        node.forEachChild(child -> BottomUpMappingAlgorithm.this.createNodeSet(child, node, set));
    }

    /**
     * Extended node containing information required for mapping.
     *
     * @since 1.1.0
     */
    private class ExtNode {
        /**
         * The node itself.
         */
        private final Node node;

        /**
         * The parent of the node.
         */
        private final Node parent;

        /**
         * Constructor.
         * @param node The node itself
         * @param parent The parent of the node
         */
        ExtNode(final Node node, final Node parent) {
            this.node = node;
            this.parent = parent;
        }

        Node getNode() {
            return this.node;
        }

        Node getParent() {
            return this.parent;
        }

        /**
         * Calculates the hash of the node.
         * @return Node hash
         */
        int getHash() {
            return BottomUpMappingAlgorithm.this.hash.calculate(this.node);
        }

        /**
         * Calculates the depth of the node.
         * @return Node depth
         */
        int getDepth() {
            return BottomUpMappingAlgorithm.this.depth.calculate(this.node);
        }
    }
}
