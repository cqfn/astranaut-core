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
package org.cqfn.astranaut.core.utils.visualizer;

import java.util.Objects;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.Node;

/**
 * Renders a DOT file from a tree.
 *
 * @since 1.0.2
 */
public class DotRender {
    /**
     * Node name start text.
     */
    private static final String NODE = "  node_";

    /**
     * Stores the generated DOT text.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder builder;

    /**
     * The tree to be converted.
     */
    private final Node tree;

    /**
     * Last index used for a node.
     */
    private int index;

    /**
     * Constructor.
     *
     * @param tree The tree to be converted
     */
    public DotRender(final Node tree) {
        this.builder = new StringBuilder();
        this.tree = Objects.requireNonNull(tree);
        this.index = 0;
    }

    /**
     * Renders data to a DOT format.
     *
     * @return A rendered DOT text as string
     */
    public String render() {
        this.appendStart();
        this.processNode(this.tree);
        this.appendEnd();
        return this.builder.toString();
    }

    /**
     * Processes a node with all its children.
     *
     * @param node A node.
     */
    private void processNode(final Node node) {
        final boolean empty = node == null || node == EmptyTree.INSTANCE;
        if (empty) {
            this.appendNullNode();
        } else {
            this.appendNode(
                node.getTypeName(),
                node.getData(),
                node.getProperties().getOrDefault("color", "")
            );
            final int parent = this.index;
            for (int idx = 0; idx < node.getChildCount(); idx += 1) {
                this.index += 1;
                final int child = this.index;
                this.processNode(node.getChild(idx));
                this.appendEdge(parent, child, idx);
            }
        }
    }

    /**
     * Appends tree start text.
     */
    private void appendStart() {
        this.builder
            .append("digraph Tree {\n")
            .append("  node [shape=box style=rounded];\n");
    }

    /**
     * Appends tree end text.
     */
    private void appendEnd() {
        this.builder.append("}\n");
    }

    /**
     * Appends tree node text.
     *
     * @param type A node type
     * @param data A node data
     * @param color A node color
     */
    private void appendNode(final String type, final String data, final String color) {
        this.builder.append(DotRender.NODE).append(this.index).append(" [");
        this.builder.append("label=<").append(type);
        if (!data.isEmpty()) {
            this.builder.append("<br/><font color=\"blue\">");
            this.builder.append(encodeHtml(data));
            this.builder.append("</font>");
        }
        this.builder.append('>');
        if (!color.isEmpty()) {
            this.builder.append(" color=").append(color);
        }
        this.builder.append("];\n");
    }

    /**
     * Appends null node text.
     */
    private void appendNullNode() {
        this.builder
            .append(DotRender.NODE)
            .append(this.index)
            .append(" [label=<NULL>];\n");
    }

    /**
     * Appends tree edge text.
     *
     * @param parent A parent node index
     * @param child A child node index
     * @param label An edge label
     */
    private void appendEdge(final int parent, final int child, final Integer label) {
        this.builder
            .append(DotRender.NODE)
            .append(parent)
            .append(" -> ")
            .append("node_")
            .append(child);
        if (label != null) {
            this.builder
                .append(" [label=\" ")
                .append(label)
                .append("\"]");
        }
        this.builder.append(";\n");
    }

    /**
     * Encodes text into an HTML-compatible format replacing characters,
     * which are not accepted in HTML, with corresponding HTML escape symbols.
     *
     * @param str Text to be encoded in HTML
     * @return An encoded text
     */
    private static String encodeHtml(final String str) {
        final StringBuilder result = new StringBuilder();
        final int len = str.length();
        for (int idx = 0; idx < len; idx += 1) {
            final char symbol = str.charAt(idx);
            switch (symbol) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '\'':
                    result.append("&apos;");
                    break;
                case '\"':
                    result.append("&quot;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                default:
                    result.append(symbol);
                    break;
            }
        }
        return result.toString();
    }
}
