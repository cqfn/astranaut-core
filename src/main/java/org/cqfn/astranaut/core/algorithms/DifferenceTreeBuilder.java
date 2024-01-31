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

/**
 * Builder of difference syntax tree, that is, one that stores changes between two trees.
 *
 * @since 1.1.0
 */
public final class DifferenceTreeBuilder {
    /**
     * The relationship of the nodes to their parents.
     * This information is necessary to implement algorithms for
     * adding, removing and replacing nodes.
     */
    public final Map<Node, DifferenceNode> parents;

    /**
     * Root node.
     */
    public final DifferenceNode root;

    /**
     * Constructor.
     * @param root Root node of an 'ordinary', non-difference original tree before the changes
     */
    public DifferenceTreeBuilder(final Node root) {
        this.root = new DifferenceNode(root);
        this.parents = DifferenceTreeBuilder.buildParentsMap(this.root);
    }

    /**
     * Returns root of resulting difference tree.
     * @return Root node of difference tree
     */
    public DifferenceNode getRoot() {
        return this.root;
    }

    /**
     * Adds an action to the difference tree that removes a node.
     * @param node The node to be removed
     * @return {@code true} if action was added
     */
    public boolean deleteNode(Node node) {
        boolean result = false;
        final DifferenceNode parent = this.parents.get(node);
        if (parent != null) {
            final int index = DifferenceTreeBuilder.findChildIndex(node, parent);
            if (index >= 0) {
                result = parent.deleteNode(index);
            }
        }
        return result;
    }

    /**
     * Builds the map containing relationship of the nodes to their parents.
     * @param root Root node
     * @return The map containing relationship of the nodes to their parents.
     */
    private static Map<Node, DifferenceNode> buildParentsMap(DifferenceNode root) {
        final Map<Node, DifferenceNode> map = new HashMap<>();
        DifferenceTreeBuilder.buildParentsMap(map, root);
        return map;
    }

    /**
     * Builds the map containing relationship of the nodes to their parents (recursive method).
     * @param map Where to put the results
     * @param node Current node
     */
    private static void buildParentsMap(
        final Map<Node, DifferenceNode> map,
        final DifferenceNode node) {
        node.forEachChild(child -> {
            if (child instanceof DifferenceNode) {
                DifferenceNode diff = (DifferenceNode) child;
                map.put(diff.getPrototype(), node);
                DifferenceTreeBuilder.buildParentsMap(map, diff);
            }
        });
    }

    /**
     * Searches the index of a child element by its prototype.
     * @param prototype Prototype of the node whose index is to be found
     * @param parent Parent node to search in
     * @return Index or -1 if there is no such node or it has already been deleted or replaced
     */
    private static int findChildIndex(Node prototype, DifferenceNode parent) {
        int result = -1;
        final int count = parent.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final Node child = parent.getChild(index);
            if (child instanceof DifferenceNode && prototype == ((DifferenceNode) child).getPrototype()) {
                result = index;
                break;
            }
        }
        return result;
    }
}
