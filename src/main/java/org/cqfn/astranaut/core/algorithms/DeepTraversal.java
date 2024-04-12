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

import org.cqfn.astranaut.core.Node;

/**
 * Performs a deep traversal of the syntax tree.
 *
 * @since 1.1.5
 */
public class DeepTraversal {
    /**
     * The root node of the tree being traversed.
     */
    private final Node root;

    /**
     * Constructor.
     * @param root The root node of the tree being traversed
     */
    public DeepTraversal(final Node root) {
        this.root = root;
    }

    /**
     * Processes nodes starting from the root. Processes a node first.
     * If the stopping criterion is not reached, recursively processes all children of it,
     * starting from the first one. Once a node is found that satisfies the criterion,
     * stops traversal.
     * @param visitor Visitor that processes nodes
     * @return Found node or {@code null} if no node is found
     */
    public Node findFirstFromRoot(final Visitor visitor) {
        return DeepTraversal.findFirstFromRoot(this.root, visitor);
    }

    /**
     * Recursive method that implements the "Find the first starting from the root" algorithm.
     * @param node Current node to be processed
     * @param visitor Visitor that processes nodes
     * @return Found node or {@code null} if no node is found
     */
    private static Node findFirstFromRoot(final Node node, final Visitor visitor) {
        Node result = null;
        final boolean stop = visitor.process(node);
        if (stop) {
            result = node;
        } else {
            final int count = node.getChildCount();
            for (int index = 0; index < count && result == null; index = index + 1) {
                result = DeepTraversal.findFirstFromRoot(node.getChild(index), visitor);
            }
        }
        return result;
    }

    /**
     * Payload interface for the traversal algorithm.
     *
     * @since 1.1.5
     */
    public interface Visitor {
        /**
         * Processes a node.
         * @param node Node
         * @return Whether to stop traversal after node processing
         */
        boolean process(Node node);
    }
}
