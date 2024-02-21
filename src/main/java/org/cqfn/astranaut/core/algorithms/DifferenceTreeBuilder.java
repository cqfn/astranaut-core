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
import org.cqfn.astranaut.core.algorithms.mapping.Mapper;
import org.cqfn.astranaut.core.algorithms.mapping.Mapping;

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
    private final Map<Node, DifferenceNode> parents;

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
        this.parents = DifferenceTreeBuilder.buildParentsMap(this.root);
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
     * Adds an action to the difference tree that replaces a node.
     * @param node Child element that will be replaced
     * @param replacement Child element to be replaced by
     * @return Result of operation, {@code true} if action was added
     */
    public boolean replaceNode(final Node node, final Node replacement) {
        boolean result = false;
        final DifferenceNode parent = this.parents.get(node);
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
        final DifferenceNode parent = this.parents.get(node);
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
    private static Map<Node, DifferenceNode> buildParentsMap(final DifferenceNode root) {
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
        node.forEachChild(
            child -> {
                if (child instanceof DifferenceNode) {
                    final DifferenceNode diff = (DifferenceNode) child;
                    map.put(diff.getPrototype(), node);
                    DifferenceTreeBuilder.buildParentsMap(map, diff);
                }
            }
        );
    }
}
