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
import org.cqfn.astranaut.core.Node;

/**
 * Subtree.
 *
 * @since 1.1.4
 */
public class Subtree {
    /**
     * The root of the subtree.
     */
    private final SubtreeNode root;

    /**
     * The set of nodes for this subtree.
     */
    private Set<Node> nodes;

    /**
     * The mapping of the nodes' children for this subtree.
     */
    private Map<Node, List<Integer>> mapping;

    /**
     * Constructor. Create subtree with the given tree root.
     *
     * @param root The root of the subtree
     */
    public Subtree(final Node root) {
        this.root = new SubtreeNode(root, this);
    }

    /**
     * Create the subtree for the given set of nodes.
     *
     * @param subtree The set of nodes, which make up a subtree
     * @return The {@link SubtreeNode}, which is the root of the created subtree
     */
    public SubtreeNode create(final Set<Node> subtree) {
        this.nodes = subtree;
        this.mapping = new HashMap<>();
        this.preOrderTraversal(this.root.getOriginal());
        return this.root;
    }

    /**
     * Get subtree mapping.
     *
     * @return The mapping for this subtree
     */
    Map<Node, List<Integer>> getMapping() {
        return this.mapping;
    }

    /**
     * Pre-order traverse the tree from the given node.
     *
     * @param node The node to traverse from
     */
    private void preOrderTraversal(final Node node) {
        this.mapping.computeIfAbsent(node, s -> new ArrayList<>(0));
        for (int index = 0; index < node.getChildCount(); index += 1) {
            final Node child = node.getChild(index);
            if (this.nodes.contains(child)) {
                this.mapping.get(node).add(index);
                this.preOrderTraversal(child);
            }
        }
    }
}
