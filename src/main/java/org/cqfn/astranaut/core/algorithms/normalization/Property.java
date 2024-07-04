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

import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;

/**
 * Property extracted from the original node.
 * @since 2.0.0
 */
public final class Property implements Node {
    /**
     * The type of the property.
     */
    private final Type type;

    /**
     * The value of the property.
     */
    private final String value;

    /**
     * Constructor.
     * @param name Property name
     * @param value Property value
     */
    public Property(final String name, final String value) {
        this.type = new PropertyType(name);
        this.value = value;
    }

    @Override
    public Type getType() {
        return this.type;
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

    /**
     * Type of property extracted from the original node.
     * @since 2.0.0
     */
    private static final class PropertyType implements Type {
        /**
         * Property name.
         */
        private final String name;

        /**
         * Constructor.
         * @param name Property name
         */
        private PropertyType(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Builder createBuilder() {
            throw new UnsupportedOperationException();
        }
    }
}
