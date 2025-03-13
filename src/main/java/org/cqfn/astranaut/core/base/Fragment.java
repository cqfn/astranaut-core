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
 * Describes a fragment of source code.
 * @since 1.0
 */
public interface Fragment {
    /**
     * Returns the first position of the fragment.
     * @return The first position.
     */
    Position getBegin();

    /**
     * Returns the last position of the fragment.
     * @return The last position.
     */
    Position getEnd();

    /**
     * Returns a string representation of the fragment.
     * @return String representation of the fragment
     */
    default String getCode() {
        return this.getBegin().getSource().getFragmentAsString(this.getBegin(), this.getEnd());
    }

    /**
     * Returns a formatted string representing the position of the fragment.
     * @return A formatted string describing the position of the fragment
     */
    default String getPosition() {
        final String result;
        final Position begin = this.getBegin();
        final Position end = this.getEnd();
        final String path = this.getBegin().getSource().getFileName();
        if (path.isEmpty()) {
            result = String.format(
                "%d.%d - %d.%d",
                begin.getRow(),
                begin.getColumn(),
                end.getRow(),
                end.getColumn()
            );
        } else {
            result = String.format(
                "%s, %d.%d - %d.%d",
                path,
                begin.getRow(),
                begin.getColumn(),
                end.getRow(),
                end.getColumn()
            );
        }
        return result;
    }
}
