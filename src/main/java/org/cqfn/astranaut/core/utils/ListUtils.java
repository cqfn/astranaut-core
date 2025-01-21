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
package org.cqfn.astranaut.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for building lists.
 * @param <T> Item type
 * @since 1.0
 */
public final class ListUtils<T> {
    /**
     * List being built.
     */
    private final List<T> list;

    /**
     * Constructor.
     */
    public ListUtils() {
        this.list = new ArrayList<>(0);
    }

    /**
     * Adds non-null items to the list. Null items are skipped.
     * @param items Items
     * @return Itself
     */
    @SafeVarargs
    public final ListUtils<T> add(final T... items) {
        for (final T item : items) {
            if (item != null) {
                this.list.add(item);
            }
        }
        return this;
    }

    /**
     * Merges another list with the result. Null items are skipped.
     * @param other Another list
     */
    public void merge(final List<T> other) {
        if (other != null) {
            for (final T item : other) {
                if (item != null) {
                    this.list.add(item);
                }
            }
        }
    }

    /**
     * Builds an unmodifiable list.
     * @return A list
     */
    public List<T> make() {
        return Collections.unmodifiableList(this.list);
    }
}
