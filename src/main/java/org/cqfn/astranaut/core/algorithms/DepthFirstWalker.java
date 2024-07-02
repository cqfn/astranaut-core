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
import java.util.List;
import java.util.Optional;
import org.cqfn.astranaut.core.base.Node;

/**
 * Performs a depth-first traversal of the syntax tree.
 *
 * @since 1.1.5
 */
public class DepthFirstWalker {
    /**
     * The root node of the tree being traversed.
     */
    private final Node root;

    /**
     * Constructor.
     * @param root The root node of the tree being traversed
     */
    public DepthFirstWalker(final Node root) {
        this.root = root;
    }

    /**
     * Processes nodes starting from the root. Processes a node first.
     * If the stopping criterion is not reached, recursively processes all children of it,
     * starting from the first one. Once a node is found that satisfies the criterion,
     * stops traversal.<br/>
     * And yes, you can use this algorithm not only to find nodes, but also just to traverse
     * the tree in the specific order.
     * @param visitor Visitor that processes nodes
     * @return Found node (optional)
     */
    public Optional<Node> findFirst(final Visitor visitor) {
        return Optional.ofNullable(DepthFirstWalker.findFirst(this.root, visitor));
    }

    /**
     * Processes nodes starting from the root.
     * If a node matches the criterion, adds it to the set and does not check
     * the children of this node, otherwise it does.
     * @param visitor Visitor that processes nodes
     * @return List of found nodes (can be empty, but not {@code null})
     */
    public List<Node> findAll(final Visitor visitor) {
        final List<Node> list = new ArrayList<>(0);
        DepthFirstWalker.findAll(this.root, visitor, list);
        return list;
    }

    /**
     * Recursive method that implements the "Find first starting from the root" algorithm.
     * @param node Current node to be processed
     * @param visitor Visitor that processes nodes
     * @return Found node or {@code null} if no node is found
     */
    private static Node findFirst(final Node node, final Visitor visitor) {
        Node result = null;
        final boolean stop = visitor.process(node);
        if (stop) {
            result = node;
        } else {
            final int count = node.getChildCount();
            for (int index = 0; index < count && result == null; index = index + 1) {
                result = DepthFirstWalker.findFirst(node.getChild(index), visitor);
            }
        }
        return result;
    }

    /**
     * Recursive method that implements the "Find all starting from the root" algorithm.
     * @param node Current node to be processed
     * @param visitor Visitor that processes nodes
     * @param list List of found nodes
     */
    private static void findAll(final Node node, final Visitor visitor, final List<Node> list) {
        final boolean found = visitor.process(node);
        if (found) {
            list.add(node);
        } else {
            final int count = node.getChildCount();
            for (int index = 0; index < count; index = index + 1) {
                DepthFirstWalker.findAll(node.getChild(index), visitor, list);
            }
        }
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
