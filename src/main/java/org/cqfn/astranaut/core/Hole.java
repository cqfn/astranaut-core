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

import java.util.List;

/**
 * A special pattern node that can substitute for any node of a suitable type.
 *
 * @since 1.1.5
 */
public final class Hole implements PatternItem {
    /**
     * The type of the hole.
     */
    private final Type type;

    /**
     * The number of the hole.
     */
    private final int number;

    /**
     * Constructor.
     * @param type The type of the hole
     * @param number The number of the hole
     */
    public Hole(final Type type, final int number) {
        this.type = new HoleType(type);
        this.number = number;
    }

    @Override
    public Fragment getFragment() {
        return EmptyFragment.INSTANCE;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public String getData() {
        return String.format("#%d", this.number);
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Node getChild(final int index) {
        return null;
    }

    /**
     * Return number of the hole.
     * @return The number
     */
    public int getNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return this.getData();
    }

    /**
     * Type implementation for a hole based on a prototype.
     *
     * @since 1.1.5
     */
    private static final class HoleType implements Type {
        /**
         * Prototype.
         */
        private final Type prototype;

        /**
         * Constructor.
         * @param prototype Prototype
         */
        private HoleType(final Type prototype) {
            this.prototype = prototype;
        }

        @Override
        public String getName() {
            return this.prototype.getName();
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return this.prototype.getChildTypes();
        }

        @Override
        public List<String> getHierarchy() {
            return this.prototype.getHierarchy();
        }

        @Override
        public String getProperty(final String name) {
            final String property;
            if ("color".equals(name)) {
                property = "purple";
            } else {
                property = this.prototype.getProperty(name);
            }
            return property;
        }

        @Override
        public Builder createBuilder() {
            return this.prototype.createBuilder();
        }
    }
}
