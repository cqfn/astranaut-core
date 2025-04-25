/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.Map;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;

/**
 * Generates graph description in DOT format from a tree.
 * @since 1.0.2
 */
public class DotGenerator {
    /**
     * Node name start text.
     */
    private static final String NODE = "  node_";

    /**
     * The 'color' property.
     */
    private static final String PROP_COLOR = "color";

    /**
     * The 'bgcolor' property.
     */
    private static final String PROP_BGCOLOR = "bgcolor";

    /**
     * Maximum length of the displayed data.
     */
    private static final int MAX_DATA_LENGTH = 64;

    /**
     * Stores the generated DOT text.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder builder;

    /**
     * The tree to be converted.
     */
    private final Tree tree;

    /**
     * Last index used for a node.
     */
    private int index;

    /**
     * Constructor.
     * @param tree The tree to be converted
     */
    public DotGenerator(final Tree tree) {
        this.builder = new StringBuilder();
        this.tree = tree;
        this.index = 0;
    }

    /**
     * Renders data to a DOT format.
     * @return A rendered DOT text as string
     */
    public String generate() {
        this.appendStart();
        this.processNode(this.tree.getRoot());
        this.appendEnd();
        return this.builder.toString();
    }

    /**
     * Processes a node with all its children.
     * @param node A node.
     */
    private void processNode(final Node node) {
        if (node == DummyNode.INSTANCE) {
            this.appendNullNode();
        } else {
            this.appendNode(
                node.getTypeName(),
                node.getData(),
                node.getProperties()
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
     * @param type Nde type
     * @param data Node data
     * @param properties Node properties
     */
    private void appendNode(final String type, final String data,
        final Map<String, String> properties) {
        this.builder.append(DotGenerator.NODE).append(this.index).append(" [");
        this.builder.append("label=<").append(type);
        if (!data.isEmpty()) {
            this.builder.append("<br/><font color=\"blue\">");
            this.builder.append(DotGenerator.encodeHtml(DotGenerator.truncate(data)));
            this.builder.append("</font>");
        }
        this.builder.append('>');
        if (properties.containsKey(DotGenerator.PROP_COLOR)) {
            this.builder.append(" color=").append(properties.get(DotGenerator.PROP_COLOR));
        }
        if (properties.containsKey(DotGenerator.PROP_BGCOLOR)) {
            this.builder.append(" style=\"filled,rounded\" fillcolor=")
                .append(properties.get(DotGenerator.PROP_BGCOLOR));
        }
        this.builder.append("];\n");
    }

    /**
     * Appends null node text.
     */
    private void appendNullNode() {
        this.builder
            .append(DotGenerator.NODE)
            .append(this.index)
            .append(" [label=<NULL>];\n");
    }

    /**
     * Appends tree edge text.
     * @param parent A parent node index
     * @param child A child node index
     * @param label An edge label
     */
    private void appendEdge(final int parent, final int child, final int label) {
        this.builder
            .append(DotGenerator.NODE)
            .append(parent)
            .append(" -> ")
            .append("node_")
            .append(child)
            .append(" [label=\" ")
            .append(label)
            .append("\"]")
            .append(";\n");
    }

    /**
     * Truncates the given text to a maximum of {@code MAX_DATA_LENGTH} characters.
     *  If the text exceeds {@code MAX_DATA_LENGTH}  characters, it is cut at the last space
     *  character within the first {@code MAX_DATA_LENGTH}  characters. If no space is found,
     *  it is cut strictly at the {@code MAX_DATA_LENGTH}  character. An ellipsis ("...")
     *  is appended to indicate truncation.
     * @param text The input string to truncate, must not be {@code null}
     * @return The truncated string with an ellipsis if needed,
     *  or the original string if it's {@code MAX_DATA_LENGTH}  characters or less
     */
    private static String truncate(final String text) {
        String result = text;
        do {
            if (text.length() <= DotGenerator.MAX_DATA_LENGTH) {
                break;
            }
            final int space = text.lastIndexOf(' ', DotGenerator.MAX_DATA_LENGTH);
            int cut = space;
            if (space < 0) {
                cut = DotGenerator.MAX_DATA_LENGTH;
            }
            result = text.substring(0, cut).concat("...");
        } while (false);
        return result;
    }

    /**
     * Encodes text into an HTML-compatible format replacing characters,
     *  which are not accepted in HTML, with corresponding HTML escape symbols.
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
