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

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.cqfn.astranaut.core.base.Node;

/**
 * An algorithm that selects nodes from a tree based on some criteria.
 * @since 1.1.4
 */
public final class NodeSelector {
    /**
     * The root node of the tree from which the nodes will be selected.
     */
    private final Node root;

    /**
     * Constructor.
     * @param root The root node of the tree from which the nodes will be selected
     */
    public NodeSelector(final Node root) {
        this.root = root;
    }

    /**
     * Selects nodes from the tree based on some criterion.
     * @param criteria Node selection criteria
     * @return Set containing selected nodes
     */
    public Set<Node> select(final Criteria criteria) {
        final Walker walker = new Walker(criteria);
        walker.walk(this.root);
        return walker.set;
    }

    /**
     * Node selection criteria.
     *
     * @since 1.1.4
     */
    public interface Criteria {
        /**
         * Checks if the node satisfies the criterion.
         * @param node Node
         * @param parents Parents of the node, starting from the immediate one (in case
         *  the criterion analysis requires information about the parents)
         * @return Checking result
         */
        boolean isApplicable(Node node, Iterable<Node> parents);
    }

    /**
     * Walker that traverses the syntax tree and selects nodes.
     * @since 1.1.4
     */
    private static final class Walker {
        /**
         * Node selection criteria.
         */
        private final Criteria criteria;

        /**
         * Set containing selected nodes.
         */
        private final Set<Node> set;

        /**
         * Constructor.
         * @param criteria Node selection criteria
         */
        private Walker(final Criteria criteria) {
            this.criteria = criteria;
            this.set = new HashSet<>();
        }

        /**
         * Starts the tree traversal.
         * @param root Root node of the tree
         */
        void walk(final Node root) {
            this.check(root, new LinkedList<>());
        }

        /**
         * Checks the node and recursively all children of the node against the criterion.
         * @param node The node
         * @param parents Stack containing the parents of the node
         */
        void check(final Node node, final Deque<Node> parents) {
            if (this.criteria.isApplicable(node, parents)) {
                this.set.add(node);
            }
            final int count = node.getChildCount();
            if (count > 0) {
                parents.addFirst(node);
                for (int index = 0; index < count; index = index + 1) {
                    this.check(node.getChild(index), parents);
                }
                parents.removeFirst();
            }
        }
    }
}
