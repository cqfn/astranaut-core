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
package org.cqfn.astranaut.core.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Action that deletes a child element.
 * @since 1.1.0
 */
public final class Delete implements Action {
    /**
     * The type.
     */
    public static final Type TYPE = new DeleteType();

    /**
     * Child element.
     */
    private final Node child;

    /**
     * Constructor.
     * @param child A child element that will be removed.
     */
    public Delete(final Node child) {
        this.child = child;
    }

    @Override
    public Node getBefore() {
        return this.child;
    }

    @Override
    public Node getAfter() {
        return null;
    }

    @Override
    public Fragment getFragment() {
        return this.child.getFragment();
    }

    @Override
    public Type getType() {
        return Delete.TYPE;
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public int getChildCount() {
        return 1;
    }

    @Override
    public Node getChild(final int index) {
        final Node node;
        if (index == 0) {
            node = this.child;
        } else {
            node = null;
        }
        return node;
    }

    @Override
    public String toString() {
        return Node.toString(this);
    }

    /**
     * Type of 'Delete' action.
     * @since 1.1.0
     */
    private static final class DeleteType implements Type {
        /**
         * The 'Node' string.
         */
        private static final String NODE = "Node";

        /**
         * The 'ACTION' string.
         */
        private static final String ACTION = "Action";

        /**
         * The 'DELETE' string.
         */
        private static final String DELETE = "Delete";

        /**
         * The list of child descriptors.
         */
        private static final List<ChildDescriptor> CHILDREN =
            Collections.singletonList(
                new ChildDescriptor(
                    DeleteType.NODE,
                    false
                )
            );

        /**
         * Hierarchy.
         */
        private static final List<String> HIERARCHY =
            Collections.unmodifiableList(
                Arrays.asList(
                    DeleteType.DELETE,
                    DeleteType.ACTION
                )
            );

        /**
         * Properties.
         */
        private static final Map<String, String> PROPERTIES = Stream.of(
            new String[][] {
                {"color", "blue"},
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        @Override
        public String getName() {
            return DeleteType.DELETE;
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return DeleteType.CHILDREN;
        }

        @Override
        public List<String> getHierarchy() {
            return DeleteType.HIERARCHY;
        }

        @Override
        public Map<String, String> getProperties() {
            return DeleteType.PROPERTIES;
        }

        @Override
        public Builder createBuilder() {
            return new Constructor();
        }
    }

    /**
     * Class for 'Delete' action construction.
     * @since 1.1.0
     */
    public static final class Constructor implements Builder {
        /**
         * Child node.
         */
        private Node child;

        @Override
        public void setFragment(final Fragment fragment) {
            // do nothing
        }

        @Override
        public boolean setData(final String str) {
            return str.isEmpty();
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            boolean result = false;
            if (list.size() == 1) {
                this.child = list.get(0);
                result = true;
            }
            return result;
        }

        @Override
        public boolean isValid() {
            return this.child != null;
        }

        @Override
        public Node createNode() {
            Node node = DummyNode.INSTANCE;
            if (this.isValid()) {
                node = new Delete(this.child);
            }
            return node;
        }
    }
}
