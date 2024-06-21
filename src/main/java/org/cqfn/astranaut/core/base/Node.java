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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * The {@code Node} interface represents a node in an abstract syntax tree (AST).
 * This is our foundational interface on which the entire platform is built.
 * The interface contains a minimal number of non-default methods, making it
 * easy to create custom nodes by requiring the implementation of only a few methods.
 * Additionally, the interface is designed to allow for automatic generation
 * of classes based on it.
 *
 * @since 1.0
 */
public interface Node extends Iterable<Node> {
    /**
     * Returns the fragment associated with the node.
     * @return The fragment
     */
    default Fragment getFragment() {
        return EmptyFragment.INSTANCE;
    }

    /**
     * Returns the type of the node.
     * @return The type
     */
    Type getType();

    /**
     * Returns data associated with the node (in a textual format).
     * @return Node data or empty string
     */
    String getData();

    /**
     * Returns the value of some custom property (depends on implementation).
     * @param name The name of property
     * @return Property value (if the property is not defined, returns an empty string)
     */
    default String getProperty(final String name) {
        return this.getType().getProperty(name);
    }

    /**
     * Returns the number of children.
     * @return Child node count
     */
    int getChildCount();

    /**
     * Returns a child by its index.
     * @param index Child index
     * @return A node
     */
    Node getChild(int index);

    /**
     * Returns the name of the type.
     * @return The name
     */
    default String getTypeName() {
        return this.getType().getName();
    }

    /**
     * Checks whether the node type belongs to group.
     * @param type The type name
     * @return Checking result, {@code true} if the type belongs to the group
     */
    default boolean belongsToGroup(final String type) {
        return this.getType().belongsToGroup(type);
    }

    /**
     * Returns the list of child nodes.
     * @return The node list
     */
    default List<Node> getChildrenList() {
        final int count = this.getChildCount();
        final Node[] result = new Node[count];
        for (int index = 0; index < count; index = index + 1) {
            result[index] = this.getChild(index);
        }
        return Arrays.asList(result);
    }

    /**
     * Performs some action for each child node.
     * @param action An action
     */
    default void forEachChild(Consumer<Node> action) {
        final int count = this.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            action.accept(this.getChild(index));
        }
    }

    /**
     * Performs a deep comparison of a node with another node,
     * i.e., compares nodes, as well as recursively all children of nodes one-to-one.
     * @param other Other node
     * @return Comparison result, {@code true} if the nodes are equal
     */
    default boolean deepCompare(Node other) {
        boolean equals;
        if (this == other) {
            equals = true;
        } else {
            final int count = this.getChildCount();
            equals = count == other.getChildCount()
                && this.getTypeName().equals(other.getTypeName())
                && this.getData().equals(other.getData());
            for (int index = 0; equals && index < count; index = index + 1) {
                equals = this.getChild(index).deepCompare(other.getChild(index));
            }
        }
        return equals;
    }

    @Override
    default Iterator<Node> iterator() {
        return new NodeIterator(this);
    }

    /**
     * Iterator that enumerates the children of a node.
     *
     * @since 1.1.5
     */
    class NodeIterator implements Iterator<Node> {
        /**
         * Node.
         */
        private final Node node;

        /**
         * Current index.
         */
        private int index;

        /**
         * Constructor.
         * @param node Node whose children should be enumerated.
         */
        private NodeIterator(final Node node) {
            this.node = node;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return this.index < this.node.getChildCount();
        }

        @Override
        public Node next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final Node child = this.node.getChild(this.index);
            this.index = this.index + 1;
            return child;
        }
    }
}
