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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class describes child node within type descriptor.
 * @since 1.0
 */
public final class ChildDescriptor {
    /**
     * The name of the child type.
     */
    private final String type;

    /**
     * Flag that states that the child is optional, that is, one that can be in the list
     *  of child nodes but is not required.
     */
    private final boolean optional;

    /**
     * Constructor.
     * @param type The name of the child type
     * @param optional Flag that states that the child is optional
     */
    public ChildDescriptor(final String type, final  boolean optional) {
        this.type = type;
        this.optional = optional;
    }

    /**
     * Additional constructor.
     * @param type The name of the child type
     */
    public ChildDescriptor(final String type) {
        this(type, false);
    }

    /**
     * Returns the name of the child type.
     * @return The name
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the flag that states that the child is optional.
     * @return The optional flag
     */
    public boolean isOptional() {
        return this.optional;
    }

    /**
     * Creates constructor that helps build lists of descriptors.
     * @return Constructor that helps build lists of descriptors
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Constructor create() {
        return new Constructor();
    }

    @Override
    public String toString() {
        final String result;
        if (this.optional) {
            result = new StringBuilder()
                .append('[')
                .append(this.type)
                .append(']')
                .toString();
        } else {
            result = this.type;
        }
        return result;
    }

    /**
     * Constructor that helps build lists of descriptors.
     * @since 2.0.0
     */
    public static final class Constructor {
        /**
         * Internal list.
         */
        private final List<ChildDescriptor> list;

        /**
         * Constructor.
         */
        private Constructor() {
            this.list = new ArrayList<>(2);
        }

        /**
         * Adds a descriptor for a node that must be in the list of child nodes.
         * @param type Type name of child node
         * @return The constructor itself for the continuation of the chain
         */
        public Constructor required(final String type) {
            this.list.add(new ChildDescriptor(type, false));
            return this;
        }

        /**
         * Adds a descriptor for an optional node, that is, one that can be in the list
         *  of child nodes but is not required.
         * @param type Type name of child node
         * @return The constructor itself for the continuation of the chain
         */
        public Constructor optional(final String type) {
            this.list.add(new ChildDescriptor(type, true));
            return this;
        }

        /**
         * Constructs a list of descriptors.
         * @return Unmodifiable list of descriptors.
         */
        public List<ChildDescriptor> build() {
            return Collections.unmodifiableList(this.list);
        }
    }
}
