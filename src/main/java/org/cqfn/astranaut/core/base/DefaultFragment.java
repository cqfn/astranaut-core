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

import org.cqfn.astranaut.core.utils.Pair;

/**
 * Default implementation of the {@link Fragment} interface that fits in most cases.
 * @since 2.0.0
 */
public final class DefaultFragment implements Fragment {
    /**
     * The first position of the fragment.
     */
    private final Position begin;

    /**
     * The last position of the fragment.
     */
    private final Position end;

    /**
     * Constructor.
     * @param begin The first position of the fragment
     * @param end The last position of the fragment
     */
    public DefaultFragment(final Position begin, final Position end) {
        this.begin = begin;
        this.end = end;
    }

    /**
     * Constructor.
     * @param pair Pair of positions (begin, end)
     */
    public DefaultFragment(final Pair<Position, Position> pair) {
        this(pair.getKey(), pair.getValue());
    }

    /**
     * Constructor.
     * @param positions Positions to calculate bounds from
     */
    public DefaultFragment(final Position... positions) {
        this(Position.bounds(positions));
    }

    @Override
    public Position getBegin() {
        return this.begin;
    }

    @Override
    public Position getEnd() {
        return this.end;
    }
}
