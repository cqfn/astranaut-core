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
package org.cqfn.astranaut.core.utils.deserializer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.core.DifferenceNode;
import org.cqfn.astranaut.core.Insertion;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.DifferenceTreeBuilder;

/**
 * List of actions to be added to the tree after deserialization to produce a difference tree.
 *
 * @since 1.1.0
 */
public class ActionList {
    /**
     * Collection of nodes to be inserted.
     */
    private final Set<Insertion> insert;

    /**
     * Collection of nodes to be replaced (node before changes -> node after changes).
     */
    private final Map<Node, Node> replace;

    /**
     * Set of nodes to be deleted.
     */
    private final Set<Node> delete;

    /**
     * Constructor.
     */
    public ActionList() {
        this.insert = new HashSet<>();
        this.replace = new HashMap<>();
        this.delete = new HashSet<>();
    }

    /**
     * Checks if an action is in any list.
     * @return Checking result
     */
    public boolean hasActions() {
        return  !this.insert.isEmpty() || !this.replace.isEmpty() || !this.delete.isEmpty();
    }

    /**
     * Adds the node to the list of nodes to be inserted.
     * @param node Node to be inserted
     * @param into Parent node into which the child node will be inserted
     * @param after Node after which to insert
     */
    public void insertNodeAfter(final Node node, final Node into, final Node after) {
        this.insert.add(new Insertion(node, into, after));
    }

    /**
     * Adds the node to the list of nodes to be replaced.
     * @param node Node to be replaced
     * @param replacement Node to be replaced by
     */
    public void replaceNode(final Node node, final Node replacement) {
        this.replace.put(node, replacement);
    }

    /**
     * Adds the node to the list of nodes to be deleted.
     * @param node The node
     */
    public void deleteNode(final Node node) {
        this.delete.add(node);
    }

    /**
     * Converts the tree to a difference tree using the list of actions.
     * @param root Root node of the tree
     * @return Root node of a difference tree
     */
    public DifferenceNode convertTreeToDifferenceTree(final Node root) {
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(root);
        for (final Insertion insertion : this.insert) {
            builder.insertNode(insertion);
        }
        for (final Map.Entry<Node, Node> pair : this.replace.entrySet()) {
            builder.replaceNode(pair.getKey(), pair.getValue());
        }
        for (final Node node : this.delete) {
            builder.deleteNode(node);
        }
        return builder.getRoot();
    }
}
