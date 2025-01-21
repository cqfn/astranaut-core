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

package org.cqfn.astranaut.core.example.green;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cqfn.astranaut.core.algorithms.NodeAllocator;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.ChildDescriptor;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;
import org.cqfn.astranaut.core.utils.ListUtils;

/**
 * Node that describes the 'SimpleAssignment' type.
 * @since 1.1.0
 */
public final class SimpleAssignment implements Assignment {
    /**
     * The type.
     */
    public static final Type TYPE = new TypeImpl();

    /**
     * The number of children.
     */
    private static final int CHILD_COUNT = 2;

    /**
     * The fragment associated with the node.
     */
    private Fragment fragment;

    /**
     * List of child nodes.
     */
    private List<Node> children;

    /**
     * Child with the 'left' tag.
     */
    private AssignableExpression left;

    /**
     * Child with the 'right' tag.
     */
    private Expression right;

    /**
     * Constructor.
     */
    private SimpleAssignment() {
    }

    @Override
    public Type getType() {
        return SimpleAssignment.TYPE;
    }

    @Override
    public Fragment getFragment() {
        return this.fragment;
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public int getChildCount() {
        return SimpleAssignment.CHILD_COUNT;
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

    @Override
    public AssignableExpression getLeft() {
        return this.left;
    }

    @Override
    public Expression getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", this.left.toString(), this.right.toString());
    }

    /**
     * Type descriptor of the 'SimpleAssignment' node.
     * @since 1.1.0
     */
    private static class TypeImpl implements Type {
        /**
         * The 'SimpleAssignment' string.
         */
        private static final String SIMPLE_ASSIGNMEN = "SimpleAssignment";

        /**
         * The 'AssignableExpression' string.
         */
        private static final String ASSIGNABLE_EXPRE = "AssignableExpression";

        /**
         * The 'Expression' string.
         */
        private static final String EXPRESSION = "Expression";

        /**
         * The list of child types.
         */
        private static final List<ChildDescriptor> CHILDREN =
            Collections.unmodifiableList(
                Arrays.asList(
                    new ChildDescriptor(
                        TypeImpl.ASSIGNABLE_EXPRE,
                        false
                    ),
                    new ChildDescriptor(
                        TypeImpl.EXPRESSION,
                        false
                    )
                )
            );

        /**
         * The 'Assignment' string.
         */
        private static final String ASSIGNMENT = "Assignment";

        /**
         * Hierarchy.
         */
        private static final List<String> HIERARCHY =
            Collections.unmodifiableList(
                Arrays.asList(
                    TypeImpl.SIMPLE_ASSIGNMEN,
                    TypeImpl.ASSIGNMENT,
                    TypeImpl.EXPRESSION
                )
            );

        /**
         * Properties.
         */
        private static final Map<String, String> PROPERTIES = Stream.of(
            new String[][] {
                {"color", "green"},
                {"language", "common"},
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        @Override
        public String getName() {
            return TypeImpl.SIMPLE_ASSIGNMEN;
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return TypeImpl.CHILDREN;
        }

        @Override
        public List<String> getHierarchy() {
            return TypeImpl.HIERARCHY;
        }

        @Override
        public Map<String, String> getProperties() {
            return TypeImpl.PROPERTIES;
        }

        @Override
        public Builder createBuilder() {
            return new Constructor();
        }
    }

    /**
     * Class for 'SimpleAssignment' node construction.
     * @since 1.1.0
     */
    public static final class Constructor implements Builder {
        /**
         * The maximum number of nodes.
         */
        private static final int MAX_NODE_COUNT = 2;

        /**
         * The position of the 'left' field.
         */
        private static final int LEFT_POS = 0;

        /**
         * The position of the 'right' field.
         */
        private static final int RIGHT_POS = 1;

        /**
         * The fragment associated with the node.
         */
        private Fragment fragment = EmptyFragment.INSTANCE;

        /**
         * Node with the 'left' tag.
         */
        private AssignableExpression left;

        /**
         * Node with the 'right' tag.
         */
        private Expression right;

        @Override
        public void setFragment(final Fragment obj) {
            this.fragment = obj;
        }

        @Override
        public boolean setData(final String str) {
            return str.isEmpty();
        }

        /**
         * Sets the node with the 'left' tag.
         * @param node The node
         */
        public void setLeft(final AssignableExpression node) {
            this.left = node;
        }

        /**
         * Sets the node with the 'right' tag.
         * @param node The node
         */
        public void setRight(final Expression node) {
            this.right = node;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            final Node[] mapping = new Node[Constructor.MAX_NODE_COUNT];
            final NodeAllocator allocator =
                new NodeAllocator(SimpleAssignment.TYPE.getChildTypes());
            final boolean result = allocator.allocate(mapping, list);
            if (result) {
                this.left = (AssignableExpression) mapping[Constructor.LEFT_POS];
                this.right = (Expression) mapping[Constructor.RIGHT_POS];
            }
            return result;
        }

        @Override
        public boolean isValid() {
            return this.left != null
                && this.right != null;
        }

        @Override
        public SimpleAssignment createNode() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final SimpleAssignment node = new SimpleAssignment();
            node.fragment = this.fragment;
            node.children = new ListUtils<Node>()
                .add(
                    this.left,
                    this.right
                )
                .make();
            node.left = this.left;
            node.right = this.right;
            return node;
        }
    }
}
