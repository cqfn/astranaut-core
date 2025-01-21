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
package org.cqfn.astranaut.core.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.PrototypeBasedNode;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.base.Type;
import org.cqfn.astranaut.core.utils.Pair;

/**
 * This algorithm labels the selected nodes of the tree with some property.
 * @since 2.0.0
 */
public class LabeledTreeBuilder {
    /**
     * The root node of the original tree.
     */
    private final Node root;

    /**
     * Constructor.
     * @param root Root node of the original not labeled tree
     */
    public LabeledTreeBuilder(final Node root) {
        this.root = root;
    }

    /**
     * Constructor.
     * @param tree Original not labeled tree
     */
    public LabeledTreeBuilder(final Tree tree) {
        this(tree.getRoot());
    }

    /**
     * Constructs a new tree in which the specified nodes have one added property.
     * @param selected Set of selected nodes in which the property will be added
     * @param name New property name
     * @param value New property value
     * @return Labeled tree
     */
    public Tree build(final Set<Node> selected, final String name, final String value) {
        return new Tree(
            new LabeledNode(this.root, selected, new Pair<>(name, value))
        );
    }

    /**
     * A node created from the original node, however, with one modified property.
     * @since 2.0.0
     */
    private static final class LabeledNode implements PrototypeBasedNode {
        /**
         * Original node.
         */
        private final Node original;

        /**
         * Child labeled nodes.
         */
        private final List<LabeledNode> children;

        /**
         * Collection of properties that contains a specific label.
         */
        private final Map<String, String> properties;

        /**
         * Constructor.
         * @param original Original node.
         * @param selected Set of selected nodes in which the property will be added
         * @param property Property to be added
         */
        private LabeledNode(final Node original,
            final Set<Node> selected, final Pair<String, String> property) {
            this.original = original;
            this.children = LabeledNode.initChildrenList(original, selected, property);
            this.properties = LabeledNode.initProperties(original, selected, property);
        }

        @Override
        public Type getType() {
            return this.original.getType();
        }

        @Override
        public String getData() {
            return this.original.getData();
        }

        @Override
        public Map<String, String> getProperties() {
            return this.properties;
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
        public String toString() {
            return this.original.toString();
        }

        @Override
        public Node getPrototype() {
            return this.original;
        }

        /**
         * Initiates a list of labeled child nodes.
         * @param original Original node.
         * @param selected Set of selected nodes in which the property will be added
         * @param property Property to be added
         * @return List of labeled child nodes
         */
        private static List<LabeledNode> initChildrenList(final Node original,
            final Set<Node> selected, final Pair<String, String> property) {
            final int count = original.getChildCount();
            final List<LabeledNode> list = new ArrayList<>(count);
            for (int index = 0; index < count; index = index + 1) {
                list.add(new LabeledNode(original.getChild(index), selected, property));
            }
            return Collections.unmodifiableList(list);
        }

        /**
         * Initiates a property collection with the added property (if necessary).
         * @param original Original node.
         * @param selected Set of selected nodes in which the property will be added
         * @param property Property to be added
         * @return Property collection with the added property
         */
        private static Map<String, String> initProperties(final Node original,
            final Set<Node> selected, final Pair<String, String> property) {
            final Map<String, String> result;
            if (selected.contains(original)) {
                result = new TreeMap<>(original.getProperties());
                result.put(property.getKey(), property.getValue());
            } else {
                result = original.getProperties();
            }
            return Collections.unmodifiableMap(result);
        }
    }
}
