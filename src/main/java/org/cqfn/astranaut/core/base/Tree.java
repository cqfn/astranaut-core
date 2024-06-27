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
package org.cqfn.astranaut.core.base;

import java.util.Map;
import java.util.Set;

/**
 * A syntax tree, which represents the hierarchical structure of source code,
 *  depicting the syntactic structure and relationships between code elements.
 *
 * @since 2.0.0
 */
public class Tree {
    /**
     * Root node of the tree.
     */
    private final Node root;

    /**
     * Constructor.
     * @param root Root node the tree
     */
    public Tree(final Node root) {
        this.root = root;
    }

    /**
     * Returns the root node of the tree.
     * @return Root node of the tree
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * Returns the name of the programming language from which the source code,
     *  used to construct this tree, was written.
     * @return The programming language name or an empty string if no language is specified
     *  in any of the nodes.
     */
    public String getLanguage() {
        return Tree.getLanguage(this.root);
    }

    @Override
    public final String toString() {
        return this.root.toString();
    }

    /**
     * Performs a deep comparison of a tree with another tree,
     *  i.e., compares root nodes, as well as recursively all children of nodes one-to-one.
     * @param other Other node
     * @return Comparison result, {@code true} if the nodes are equal
     */
    public boolean deepCompare(final Tree other) {
        return this.root.deepCompare(other.root);
    }

    /**
     * Creates a tree from draft nodes based on description.
     *  Description format: A(B,C(...),...) where 'A' is the type name
     *  (it consists only of letters) followed by child nodes (in the same format) in parentheses
     *  separated by commas.
     * @param description Description
     * @return Tree created by description
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Tree createDraft(final String description) {
        return new Tree(DraftNode.create(description));
    }

    /**
     * Creates a tree from draft nodes based on description.
     *  Description format: A(B&lt;"data"&gt;,C(...),...) where 'A' is the type name
     *  (it consists only of letters) followed by child nodes (in the same format) in parentheses
     *  separated by commas.
     * @param description Description
     * @param nodes Collection in which to place the nodes to be created, sorted by type name
     * @return Tree created by description
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Tree createDraft(final String description, final Map<String, Set<Node>> nodes) {
        return new Tree(DraftNode.create(description, nodes));
    }

    /**
     * Analyzes the node and its children (recursively) to determine if a programming language
     *  name is specified.
     * @param node Node for analysis
     * @return The programming language name or an empty string if no language is specified
     *  in any of the child nodes.
     */
    protected static String getLanguage(final Node node) {
        String language = node.getProperties().getOrDefault("language", "");
        if (language.isEmpty()) {
            final int count = node.getChildCount();
            for (int index = 0; language.isEmpty() && index < count; index = index + 1) {
                language = Tree.getLanguage(node.getChild(index));
            }
        }
        return language;
    }
}
