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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The DraftNode class implements the {@link Node} interface. This class serves as a "draft node",
 *  where no data consistency checks are performed. The constructor of this class allows
 *  the creation of any tree structure without validation, making it suitable for testing
 *  purposes and for displaying trees obtained from third-party parsers in the ASTranaut
 *  format.<br/>
 * DraftNode is designed to be immutable.
 * @since 1.0
 */
public final class DraftNode extends NodeAndType {
    /**
     * The fragment associated with the node.
     */
    private Fragment fragment;

    /**
     * The node name.
     */
    private String name;

    /**
     * The node data.
     */
    private String data;

    /**
     * The list of children nodes.
     */
    private List<Node> children;

    /**
     * Private constructor.
     */
    private DraftNode() {
    }

    @Override
    public Fragment getFragment() {
        return this.fragment;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

    @Override
    public Builder createBuilder() {
        final Constructor ctor = new Constructor();
        ctor.setName(this.name);
        return ctor;
    }

    @Override
    public String toString() {
        return Node.toString(this);
    }

    /**
     * Creates a tree from draft nodes based on description.
     *  Description format: A(B&lt;"data"&gt;,C(...),...) where 'A' is the type name
     *  (it consists only of letters) followed by child nodes (in the same format) in parentheses
     *  separated by commas.
     * @param description Description
     * @return Root node of the tree created by description
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Node create(final String description) {
        return DraftNode.create(description, null);
    }

    /**
     * Creates a tree from draft nodes based on description.
     *  Description format: A(B&lt;"data"&gt;,C(...),...) where 'A' is the type name
     *  (it consists only of letters) followed by child nodes (in the same format) in parentheses
     *  separated by commas.
     * @param description Description
     * @param nodes Collection in which to place the nodes to be created, sorted by type name
     * @return Root node of the tree created by description
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Node create(final String description, final Map<String, Set<Node>> nodes) {
        final CharacterIterator iterator = new StringCharacterIterator(description);
        return DraftNode.create(iterator, nodes);
    }

    /**
     * Creates a tree based on its description (recursive method).
     * @param iterator Iterator by description characters
     * @param nodes Collection in which to place the nodes to be created, sorted by type name
     * @return Node of the tree with its children, created by description
     */
    private static Node create(
        final CharacterIterator iterator,
        final Map<String, Set<Node>> nodes) {
        char symbol = iterator.current();
        final Node result;
        final StringBuilder name = new StringBuilder();
        while (symbol >= 'A' && symbol <= 'Z' || symbol >= 'a' && symbol <= 'z') {
            name.append(symbol);
            symbol = iterator.next();
        }
        if (name.length() > 0) {
            final DraftNode.Constructor builder = new DraftNode.Constructor();
            builder.setName(name.toString());
            if (symbol == '<') {
                builder.setData(DraftNode.parseData(iterator));
                symbol = iterator.current();
            }
            if (symbol == '(') {
                builder.setChildrenList(DraftNode.parseChildrenList(iterator, nodes));
            }
            result = builder.createNode();
            if (nodes != null) {
                final Set<Node> set =
                    nodes.computeIfAbsent(result.getTypeName(), k -> new HashSet<>());
                set.add(result);
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Parses data from description.
     * @param iterator Iterator by description characters
     * @return Node data, extracted from description
     */
    private static String parseData(final CharacterIterator iterator) {
        final StringBuilder data = new StringBuilder();
        char symbol = iterator.next();
        if (symbol == '\"') {
            symbol = iterator.next();
            while (symbol != '\"') {
                data.append(symbol);
                symbol = iterator.next();
            }
            symbol = iterator.next();
            if (symbol == '>') {
                iterator.next();
            }
        } else {
            do {
                symbol = iterator.next();
            } while (symbol != '>' && symbol != CharacterIterator.DONE);
            iterator.next();
        }
        return data.toString();
    }

    /**
     * Parses children list from description.
     * @param iterator Iterator by description characters
     * @param nodes Collection in which to place the nodes to be created, sorted by type name
     * @return Children list, created by description
     */
    private static List<Node> parseChildrenList(
        final CharacterIterator iterator,
        final Map<String, Set<Node>> nodes) {
        final List<Node> children = new LinkedList<>();
        char next;
        do {
            iterator.next();
            final Node child = DraftNode.create(iterator, nodes);
            if (child != null) {
                children.add(child);
            }
            next = iterator.current();
            if (next != ')' && next != ',' && next != ' ') {
                do {
                    next = iterator.next();
                } while (next != ')' && next != CharacterIterator.DONE);
            }
        } while (next != ')');
        iterator.next();
        return children;
    }

    /**
     * The constructor class for draft node.
     * @since 1.0
     */
    public static final class Constructor implements Builder {
        /**
         * The fragment associated with the node.
         */
        private Fragment fragment;

        /**
         * The type name.
         */
        private String name;

        /**
         * The node data.
         */
        private String data;

        /**
         * The list of children nodes.
         */
        private final List<Node> children;

        /**
         * Constructor.
         */
        public Constructor() {
            this.fragment = EmptyFragment.INSTANCE;
            this.name = "";
            this.data = "";
            this.children = new LinkedList<>();
        }

        @Override
        public void setFragment(final Fragment obj) {
            this.fragment = obj;
        }

        /**
         * Sets the type name.
         * @param str Type mane
         */
        public void setName(final String str) {
            this.name = str;
        }

        @Override
        public boolean setData(final String str) {
            this.data = Objects.requireNonNull(str);
            return true;
        }

        /**
         * Adds a child node.
         * @param node Node
         */
        public void addChild(final Node node) {
            this.children.add(Objects.requireNonNull(node));
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            this.children.clear();
            this.children.addAll(list);
            return true;
        }

        @Override
        public boolean isValid() {
            return !this.name.isEmpty();
        }

        @Override
        public DraftNode createNode() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final DraftNode node = new DraftNode();
            node.fragment = this.fragment;
            node.name = this.name;
            node.data = this.data;
            node.children = new ArrayList<>(this.children);
            return node;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.name);
            if (!this.data.isEmpty()) {
                builder.append("<\"").append(this.data).append("\">");
            }
            if (!this.children.isEmpty()) {
                boolean flag = false;
                builder.append('(');
                for (final Node child : this.children) {
                    if (flag) {
                        builder.append(", ");
                    }
                    flag = true;
                    builder.append(child.toString());
                }
                builder.append(')');
            }
            return builder.toString();
        }
    }
}
