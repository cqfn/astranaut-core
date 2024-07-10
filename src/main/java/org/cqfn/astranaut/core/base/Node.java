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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * The {@code Node} interface represents a node in an abstract syntax tree (AST).
 * This is our foundational interface on which the entire platform is built.
 * The interface contains a minimal number of non-default methods, making it
 * easy to create custom nodes by requiring the implementation of only a few methods.
 * Additionally, the interface is designed to allow for automatic generation
 * of classes based on it.
 * The interface is designed to be immutable, which ensures thread safety and
 * consistency, as well as simplifying the debugging process.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.ExcessivePublicCount")
public interface Node {
    /**
     * Returns the fragment of source code that is associated with the node.
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
     * Retrieves the properties associated with the node.
     *  The presence of specific properties depends on the specific node implementation
     *  and may vary. By default, returns the properties of the node type.
     * @return Immutable map of properties where keys are property names
     *  and values are property values
     */
    default Map<String, String> getProperties() {
        return this.getType().getProperties();
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
        return new ChildrenList(this);
    }

    /**
     * Returns an iterator over the child nodes of this node.
     * @return An {@link Iterator} over the child nodes
     */
    default Iterator<Node> getIteratorOverChildren() {
        return new ChildrenIterator(this, 0);
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
     *  i.e., compares nodes, as well as recursively all children of nodes one-to-one.
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
                && this.getData().equals(other.getData())
                && this.getProperties().equals(other.getProperties());
            for (int index = 0; equals && index < count; index = index + 1) {
                equals = this.getChild(index).deepCompare(other.getChild(index));
            }
        }
        return equals;
    }

    /**
     * Creates a deep clone of the node, including all its children.
     * This method performs a deep cloning operation on the node. It first clones
     *  the node itself and then recursively clones all its children, ensuring that
     *  the entire subtree rooted at this node is cloned.
     * @return A deep clone of the node, including all its children
     */
    default Node deepClone() {
        final Builder builder = this.getType().createBuilder();
        builder.setFragment(this.getFragment());
        builder.setData(this.getData());
        final int count = this.getChildCount();
        final List<Node> list = new ArrayList<>(count);
        for (int index = 0; index < count; index = index + 1) {
            list.add(this.getChild(index).deepClone());
        }
        builder.setChildrenList(list);
        return builder.createNode();
    }

    /**
     * Converts the given {@link Node} to its string representation.
     * @param node Node to be converted to a string
     * @return String representation of the given node
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    static String toString(final Node node) {
        final StringBuilder builder = new StringBuilder();
        builder.append(node.getTypeName());
        final String data = node.getData();
        if (!data.isEmpty()) {
            builder.append("<\"").append(data).append("\">");
        }
        final int count = node.getChildCount();
        if (count > 0) {
            builder.append('(');
            for (int index = 0; index < count; index = index + 1) {
                if (index > 0) {
                    builder.append(", ");
                }
                builder.append(node.getChild(index).toString());
            }
            builder.append(')');
        }
        return builder.toString();
    }

    /**
     * Iterator that enumerates the children of a node.
     *
     * @since 1.1.5
     */
    class ChildrenIterator implements ListIterator<Node> {
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
         * @param index Starting index
         */
        private ChildrenIterator(final Node node, final int index) {
            this.node = node;
            this.index = index;
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

        @Override
        public boolean hasPrevious() {
            return this.index > 0;
        }

        @Override
        public Node previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.index = this.index - 1;
            return this.node.getChild(this.index);
        }

        @Override
        public int nextIndex() {
            return this.index;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(final Node other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(final Node other) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Class that wraps a node and provides a list of its children.
     * This custom implementation provides an efficient wrapper for managing
     *  the list of child nodes. By creating our own implementation, we ensure
     *  minimal overhead and optimal performance for operations involving child
     *  nodes. Using the List interface for child nodes is frequent in our codebase
     *  due to its convenience and flexibility in handling node collections.
     *
     * @since 2.0.0
     */
    @SuppressWarnings("PMD.TooManyMethods")
    class ChildrenList implements List<Node> {
        /**
         * Node that is wrapped by this class.
         */
        private final Node node;

        /**
         * Constructor.
         * @param node Node that will be wrapped
         */
        public ChildrenList(final Node node) {
            this.node = node;
        }

        @Override
        public int size() {
            return this.node.getChildCount();
        }

        @Override
        public boolean isEmpty() {
            return this.size() == 0;
        }

        @Override
        public boolean contains(final Object obj) {
            final int count = this.node.getChildCount();
            boolean found = false;
            for (int index = 0; index < count; index = index + 1) {
                if (this.node.getChild(index) == obj) {
                    found = true;
                    break;
                }
            }
            return found;
        }

        @Override
        public Iterator<Node> iterator() {
            return new ChildrenIterator(this.node, 0);
        }

        @Override
        public Object[] toArray() {
            final int count = this.node.getChildCount();
            final Object[] array = new Object[count];
            for (int index = 0; index < count; index = index + 1) {
                array[index] = this.node.getChild(index);
            }
            return array;
        }

        @Override
        public <T> T[] toArray(final T[] array) {
            int count = this.node.getChildCount();
            if (array.length < count) {
                count = array.length;
            }
            for (int index = 0; index < count; index = index + 1) {
                @SuppressWarnings("unchecked")
                final T element = (T) this.node.getChild(index);
                array[index] = element;
            }
            return array;
        }

        @Override
        public boolean add(final Node other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(final Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(final Collection<?> collection) {
            boolean result = true;
            for (final Object element : collection) {
                if (!this.contains(element)) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        @Override
        public boolean addAll(final Collection<? extends Node> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends Node> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(final Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(final Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Node get(final int index) {
            return this.node.getChild(index);
        }

        @Override
        public Node set(final int index, final Node element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(final int index, final Node element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Node remove(final int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(final Object obj) {
            int result = -1;
            if (obj instanceof Node) {
                final int count = this.node.getChildCount();
                for (int index = 0; index < count; index = index + 1) {
                    if (this.node.getChild(index) == obj) {
                        result = index;
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public int lastIndexOf(final Object obj) {
            int result = -1;
            if (obj instanceof Node) {
                for (int index = this.node.getChildCount() - 1; index >= 0; index = index - 1) {
                    if (this.node.getChild(index) == obj) {
                        result = index;
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public ListIterator<Node> listIterator() {
            return new ChildrenIterator(this.node, 0);
        }

        @Override
        public ListIterator<Node> listIterator(final int index) {
            if (index < 0 || index > this.node.getChildCount()) {
                throw new IndexOutOfBoundsException();
            }
            return new ChildrenIterator(this.node, index);
        }

        @Override
        public List<Node> subList(final int start, final int end) {
            if (start < 0 || end > this.node.getChildCount() || start >= end) {
                throw new IndexOutOfBoundsException();
            }
            return new AbstractList<Node>() {
                @Override
                public Node get(final int index) {
                    if (index < 0 || index >= ChildrenList.this.node.getChildCount()) {
                        throw new IndexOutOfBoundsException();
                    }
                    return ChildrenList.this.node.getChild(start + index);
                }

                @Override
                public int size() {
                    return end - start;
                }
            };
        }
    }
}
