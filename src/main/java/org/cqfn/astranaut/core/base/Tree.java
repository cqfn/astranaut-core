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
     * Analyzes the node and its children (recursively) to determine if a programming language
     *  name is specified.
     * @param node Node for analysis
     * @return The programming language name or an empty string if no language is specified
     *  in any of the child nodes.
     */
    protected static String getLanguage(final Node node) {
        final Type type = node.getType();
        String language = type.getProperty("language");
        if (language.isEmpty()) {
            final int count = node.getChildCount();
            for (int index = 0; language.isEmpty() && index < count; index = index + 1) {
                language = Tree.getLanguage(node.getChild(index));
            }
        }
        return language;
    }
}
