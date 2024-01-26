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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Action that deletes a child node.
 *
 * @since 1.0.7
 */
public final class Delete implements Action {
    /**
     * Node being deleted.
     */
    private final Node child;

    /**
     * Type that emulates the type of node being deleted.
     */
    private final Type type;

    /**
     * Constructor.
     * @param child Node being deleted
     */
    public Delete(final Node child) {
        this.child = child;
        this.type = new TypeImpl(child.getType());
    }

    @Override
    public Fragment getFragment() {
        return this.child.getFragment();
    }

    @Override
    public Type getType() {
        return this.type;
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
        return this.child;
    }

    @Override
    public Node getBefore() {
        return this.child;
    }

    @Override
    public Node getAfter() {
        return null;
    }

    /**
     * Type that emulates the type of node being deleted.
     *
     * @since 1.0.7
     */
    private static final class TypeImpl implements Type {
        /**
         * Properties.
         */
        private static final Map<String, String> PROPERTIES = Stream.of(
            new String[][] {
                {"color", "blue"},
                {"action", "delete"},
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        /**
         * Base type.
         */
        private final Type base;

        /**
         * Constructor.
         * @param base Base type
         */
        private TypeImpl(final Type base) {
            this.base = base;
        }

        @Override
        public String getName() {
            return this.base.getName();
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getHierarchy() {
            return this.base.getHierarchy();
        }

        @Override
        public String getProperty(final String name) {
            return TypeImpl.PROPERTIES.getOrDefault(name, "");
        }

        @Override
        public Builder createBuilder() {
            return new Delete.Constructor();
        }
    }

    /**
     * Action constructor.
     *
     * @since 1.0.7
     */
    public static final class Constructor implements Builder {
        /**
         * Node being deleted.
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
            return new Delete(this.child);
        }
    }
}
