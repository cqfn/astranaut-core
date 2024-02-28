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
package org.cqfn.astranaut.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Action that replaces a child element.
 *
 * @since 1.1.0
 */
public final class Replace implements Action {
    /**
     * The type.
     */
    public static final Type TYPE = new ReplaceType();

    /**
     * Child element before changes.
     */
    private final Node before;

    /**
     * Child element after changes.
     */
    private final Node after;

    /**
     * Constructor.
     * @param before Child element that will be replaced
     * @param after Child element to be replaced by
     */
    public Replace(final Node before, final Node after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public Node getBefore() {
        return this.before;
    }

    @Override
    public Node getAfter() {
        return this.after;
    }

    @Override
    public Fragment getFragment() {
        return this.before.getFragment();
    }

    @Override
    public Type getType() {
        return Replace.TYPE;
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public int getChildCount() {
        return 2;
    }

    @Override
    public Node getChild(final int index) {
        final Node node;
        switch (index) {
            case 0:
                node = this.before;
                break;
            case 1:
                node = this.after;
                break;
            default:
                node = null;
                break;
        }
        return node;
    }

    /**
     * Type of 'Replace' action.
     *
     * @since 1.1.0
     */
    private static final class ReplaceType implements Type {
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
        private static final String REPLACE = "Replace";

        /**
         * The list of child descriptors.
         */
        private static final List<ChildDescriptor> CHILDREN =
            Arrays.asList(
                new ChildDescriptor(
                    ReplaceType.NODE,
                    false
                ),
                new ChildDescriptor(
                    ReplaceType.NODE,
                    false
                )
            );

        /**
         * Hierarchy.
         */
        private static final List<String> HIERARCHY =
            Collections.unmodifiableList(
                Arrays.asList(
                    ReplaceType.REPLACE,
                    ReplaceType.ACTION
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
            return ReplaceType.REPLACE;
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return ReplaceType.CHILDREN;
        }

        @Override
        public List<String> getHierarchy() {
            return ReplaceType.HIERARCHY;
        }

        @Override
        public String getProperty(final String name) {
            return ReplaceType.PROPERTIES.getOrDefault(name, "");
        }

        @Override
        public Builder createBuilder() {
            return new Constructor();
        }
    }

    /**
     * Class for 'Delete' action construction.
     *
     * @since 1.1.0
     */
    public static final class Constructor implements Builder {
        /**
         * Child node before changes.
         */
        private Node before;

        /**
         * Child node after changes.
         */
        private Node after;

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
            if (list.size() == 2) {
                final Iterator<Node> iterator = list.iterator();
                this.before = iterator.next();
                this.after = iterator.next();
                result = true;
            }
            return result;
        }

        @Override
        public boolean isValid() {
            return this.before != null && this.after != null;
        }

        @Override
        public Node createNode() {
            Node node = EmptyTree.INSTANCE;
            if (this.isValid()) {
                node = new Replace(this.before, this.after);
            }
            return node;
        }
    }
}
