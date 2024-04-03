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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Fragment;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.Type;

/**
 * Algorithm that produces a subtree from the original tree.
 *
 * @since 1.1.4
 */
public class Subtree {
    /**
     * Algorithm that composes a subtree only from the nodes that are specified in the set.
     */
    public static final Algorithm INCLUDE = (node, set) -> set.contains(node);

    /**
     * Algorithm that composes a subtree from all nodes in the original tree,
     *  but excludes nodes specified in the set.
     */
    public static final Algorithm EXCLUDE = (node, set) -> !set.contains(node);

    /**
     * The root node of the original tree.
     */
    private final Node root;

    /**
     * Algorithm that selects nodes based on some criteria.
     */
    private final Algorithm algorithm;

    /**
     * Constructor.
     *
     * @param root The root node of the original tree
     * @param algorithm Algorithm that selects nodes based on some criteria
     */
    public Subtree(final Node root, final Algorithm algorithm) {
        this.root = root;
        this.algorithm = algorithm;
    }

    /**
     * Creates a subtree from the original tree, with the result depends on the nodes that are
     *  specified in the set.
     * @param nodes The set of nodes
     * @return Root node of created subtree.
     */
    public Node create(final Set<Node> nodes) {
        final Map<Node, List<Integer>> indexes = new HashMap<>();
        this.build(this.root, indexes, nodes);
        final Node result;
        if (indexes.get(this.root).isEmpty()) {
            result = EmptyTree.INSTANCE;
        } else {
            result = new SubNode(this.root, indexes);
        }
        return result;
    }

    /**
     * Constructs an index map containing the indexes of the nodes that will be included
     *  in the resulting tree.
     * @param node Current node
     * @param indexes Index map
     * @param set The set of nodes
     */
    private void build(final Node node, final Map<Node, List<Integer>> indexes,
        final Set<Node> set) {
        final List<Integer> list = indexes.computeIfAbsent(node, s -> new ArrayList<>(0));
        final int count = node.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final Node child = node.getChild(index);
            if (this.algorithm.isApplicable(child, set)) {
                list.add(index);
                this.build(child, indexes, set);
            }
        }
    }

    /**
     * An algorithm that selects nodes based on some criteria.
     *
     * @since 1.1.4
     */
    public interface Algorithm {
        /**
         * Checks if the node is applicable to the set.
         * @param node Node to be checked
         * @param set Set of nodes
         * @return Checking result.
         */
        boolean isApplicable(Node node, Set<Node> set);
    }

    /**
     * A node created from the original node, but which has only children from the specified set.
     *
     * @since 1.1.4
     */
    private static final class SubNode implements Node {
        /**
         * Original node.
         */
        private final Node original;

        /**
         * Index map containing the indexes of the nodes that should be included
         *  to the resulting tree.
         */
        private final Map<Node, List<Integer>> indexes;

        /**
         * Array of children (also truncated nodes).
         */
        private final SubNode[] children;

        /**
         * Constructor.
         * @param original Original node
         * @param indexes Index map containing the indexes of the nodes that should be included
         *  to the resulting tree
         */
        private SubNode(final Node original, final Map<Node, List<Integer>> indexes) {
            this.original = original;
            this.indexes = indexes;
            this.children = new SubNode[indexes.get(original).size()];
        }

        @Override
        public Fragment getFragment() {
            return this.original.getFragment();
        }

        @Override
        public Type getType() {
            return this.original.getType();
        }

        @Override
        public String getData() {
            return this.original.getData();
        }

        @Override
        public int getChildCount() {
            return this.children.length;
        }

        @Override
        public Node getChild(final int index) {
            if (this.children[index] == null) {
                this.children[index] = new SubNode(
                    this.original.getChild(this.indexes.get(this.original).get(index)),
                    this.indexes
                );
            }
            return this.children[index];
        }
    }
}
