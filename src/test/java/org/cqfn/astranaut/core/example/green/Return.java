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
 * Node that describes the 'Return' type.
 * @since 1.1.0
 */
public final class Return implements Statement {
    /**
     * The type.
     */
    public static final Type TYPE = new TypeImpl();

    /**
     * The fragment associated with the node.
     */
    private Fragment fragment;

    /**
     * List of child nodes.
     */
    private List<Node> children;

    /**
     * Child with the 'expression' tag.
     */
    private Expression expression;

    /**
     * Constructor.
     */
    private Return() {
    }

    @Override
    public Type getType() {
        return Return.TYPE;
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
        return this.children.size();
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

    @Override
    public String toString() {
        return "return;";
    }

    /**
     * Returns the child with the 'expression' tag.
     * @return The node
     */
    public Expression getExpression() {
        return this.expression;
    }

    /**
     * Type descriptor of the 'Return' node.
     * @since 1.1.0
     */
    private static class TypeImpl implements Type {
        /**
         * The 'Return' string.
         */
        private static final String RETURN = "Return";

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
                        TypeImpl.EXPRESSION,
                        true
                    )
                )
            );

        /**
         * The 'Statement' string.
         */
        private static final String STATEMENT = "Statement";

        /**
         * The 'ProgramItem' string.
         */
        private static final String PROGRAM_ITEM = "ProgramItem";

        /**
         * Hierarchy.
         */
        private static final List<String> HIERARCHY =
            Collections.unmodifiableList(
                Arrays.asList(
                    TypeImpl.RETURN,
                    TypeImpl.STATEMENT,
                    TypeImpl.PROGRAM_ITEM
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
            return TypeImpl.RETURN;
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
     * Class for 'Return' node construction.
     * @since 1.1.0
     */
    public static final class Constructor implements Builder {
        /**
         * The maximum number of nodes.
         */
        private static final int MAX_NODE_COUNT = 1;

        /**
         * The position of the 'expression' field.
         */
        private static final int EXPRESSION_POS = 0;

        /**
         * The fragment associated with the node.
         */
        private Fragment fragment = EmptyFragment.INSTANCE;

        /**
         * Node with the 'expression' tag.
         */
        private Expression expression;

        @Override
        public void setFragment(final Fragment obj) {
            this.fragment = obj;
        }

        @Override
        public boolean setData(final String str) {
            return str.isEmpty();
        }

        /**
         * Sets the node with the 'expression' tag.
         * @param node The node
         */
        public void setExpression(final Expression node) {
            this.expression = node;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            final Node[] mapping = new Node[Constructor.MAX_NODE_COUNT];
            final NodeAllocator allocator =
                new NodeAllocator(Return.TYPE.getChildTypes());
            final boolean result = allocator.allocate(mapping, list);
            if (result) {
                this.expression = (Expression) mapping[Constructor.EXPRESSION_POS];
            }
            return result;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Return createNode() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final Return node = new Return();
            node.fragment = this.fragment;
            node.children = new ListUtils<Node>()
                .add(
                    this.expression
                )
                .make();
            node.expression = this.expression;
            return node;
        }
    }
}
