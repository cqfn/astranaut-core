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

import java.util.List;

/**
 * Node representing one character of the source text.
 * @since 2.0.0
 */
public final class Char implements Node {
    /**
     * Name of the type.
     */
    public static final String NAME = "Char";

    /**
     * Type of the node.
     */
    public static final Type TYPE = new CharType();

    /**
     * Fragment of source code that is associated with the node.
     */
    private Fragment fragment;

    /**
     * Symbol itself.
     */
    private char symbol;

    /**
     * Private constructor.
     */
    private Char() {
    }

    @Override
    public Fragment getFragment() {
        return this.fragment;
    }

    @Override
    public Type getType() {
        return Char.TYPE;
    }

    /**
     * Returns the character stored in the node.
     * @return Non-zero character
     */
    public char getSymbol() {
        return this.symbol;
    }

    @Override
    public String getData() {
        return String.valueOf(this.symbol);
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Node getChild(final int index) {
        throw new IndexOutOfBoundsException();
    }

    /**
     * Type implementation describing character nodes.
     * @since 2.0.0
     */
    private static final class CharType implements Type {

        @Override
        public String getName() {
            return Char.NAME;
        }

        @Override
        public Builder createBuilder() {
            return new Char.Constructor();
        }
    }

    /**
     * Builder for character construction.
     * @since 2.0.0
     */
    public static final class Constructor implements Builder {
        /**
         * Fragment of source code that is associated with the node.
         */
        private Fragment fragment = EmptyFragment.INSTANCE;

        /**
         * Symbol itself.
         */
        private char symbol;

        @Override
        public void setFragment(final Fragment obj) {
            this.fragment = obj;
        }

        /**
         * Sets the symbol.
         * @param value Symbol value
         */
        public void setValue(final char value) {
            this.symbol = value;
        }

        @Override
        public boolean setData(final String str) {
            final boolean result;
            if (str.length() == 1 && str.charAt(0) != 0) {
                this.symbol = str.charAt(0);
                result = true;
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            return list.isEmpty();
        }

        @Override
        public boolean isValid() {
            return this.symbol != 0;
        }

        @Override
        public Node createNode() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final Char node = new Char();
            node.fragment = this.fragment;
            node.symbol = this.symbol;
            return node;
        }
    }
}
