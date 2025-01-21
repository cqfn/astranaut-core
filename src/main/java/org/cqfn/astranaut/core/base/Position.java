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
package org.cqfn.astranaut.core.base;

/**
 * Represents a position in source code.
 *  It provides methods to retrieve the source, row, and column of the position.
 *  Positions are comparable based on their source, row, and column. If positions
 *  have different sources, they are considered incomparable.
 * @since 1.0
 */
public interface Position extends Comparable<Position> {

    /**
     * Returns an object representing the source code that has this position.
     * @return Object representing the source code
     */
    Source getSource();

    /**
     * Returns the row (line) number of this position.
     * @return The row number
     */
    int getRow();

    /**
     * Returns the column number of this position.
     * @return The column number
     */
    int getColumn();

    @Override
    default int compareTo(final Position other) {
        if (!this.getSource().equals(other.getSource())) {
            throw new IllegalArgumentException();
        }
        int result = Integer.compare(this.getRow(), other.getRow());
        if (result == 0) {
            result = Integer.compare(this.getColumn(), other.getColumn());
        }
        return result;
    }
}
