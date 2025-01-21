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

/**
 * {@code EmptyFragment} is an implementation of {@link Fragment} representing an empty fragment.
 *  It serves as a placeholder when a node does not have an associated fragment
 *  preventing the need to return a null pointer.
 * @since 1.0
 */
public final class EmptyFragment implements Fragment {
    /**
     * The instance.
     */
    public static final Fragment INSTANCE = new EmptyFragment();

    /**
     * The source.
     */
    private static final Source SOURCE = new Source() {
        @Override
        public String getFragmentAsString(final Position start, final Position end) {
            return "";
        }
    };

    /**
     * The position.
     */
    private static final Position POSITION = new Position() {
        @Override
        public Source getSource() {
            return EmptyFragment.SOURCE;
        }

        @Override
        public int getRow() {
            return 0;
        }

        @Override
        public int getColumn() {
            return 0;
        }
    };

    /**
     * Constructor.
     */
    private EmptyFragment() {
    }

    @Override
    public Position getBegin() {
        return EmptyFragment.POSITION;
    }

    @Override
    public Position getEnd() {
        return EmptyFragment.POSITION;
    }
}
