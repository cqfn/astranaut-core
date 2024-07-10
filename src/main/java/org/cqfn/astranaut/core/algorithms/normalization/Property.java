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
package org.cqfn.astranaut.core.algorithms.normalization;

import java.util.List;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.NodeAndType;

/**
 * Property extracted from the original node.
 * @since 2.0.0
 */
public final class Property extends NodeAndType {
    /**
     * Property name.
     */
    private final String name;

    /**
     * Property value.
     */
    private final String value;

    /**
     * Constructor.
     * @param name Property name
     * @param value Property value
     */
    public Property(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getData() {
        return this.value;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Node getChild(final int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return Node.toString(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Builder createBuilder() {
        return new PropertyBuilder(this.name);
    }

    /**
     * Builder of the property with the specified name.
     * @since 2.0.0
     */
    private static final class PropertyBuilder implements Builder {
        /**
         * Property name.
         */
        private final String name;

        /**
         * Property value.
         */
        private String value;

        /**
         * Constructor.
         * @param name Name
         */
        private PropertyBuilder(final String name) {
            this.name = name;
            this.value = "";
        }

        @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
        @Override
        public void setFragment(final Fragment fragment) {
        }

        @Override
        public boolean setData(final String str) {
            boolean result = false;
            if (!str.isEmpty()) {
                this.value = str;
                result = true;
            }
            return result;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            return list.isEmpty();
        }

        @Override
        public boolean isValid() {
            return !this.value.isEmpty();
        }

        @Override
        public Node createNode() {
            return new Property(this.name, this.value);
        }
    }
}
