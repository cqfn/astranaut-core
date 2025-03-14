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
package org.cqfn.astranaut.core.utils.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cqfn.astranaut.core.base.Char;
import org.cqfn.astranaut.core.base.Position;

/**
 * Implementation of the Source interface that stores source code as a set of strings.
 * @since 2.0.0
 */
public class StringSource implements CharParser {
    /**
     * Source code divided into lines.
     */
    private final String[] lines;

    /**
     * Constructs a {@code StringSource} instance by splitting the given source code into lines.
     * @param code Source code as one big line
     */
    public StringSource(final String code) {
        this.lines = code.split("\n");
    }

    @Override
    public final String getFragmentAsString(final Position start, final Position end) {
        final String result;
        do {
            if (start.compareTo(end) > 0) {
                result = this.getFragmentAsString(end, start);
                break;
            }
            final int row = start.getRow();
            final int count = end.getRow() - row + 1;
            if (count == 1) {
                result = this.getLineByRow(row, start.getColumn(), end.getColumn());
                break;
            }
            final List<String> list = new ArrayList<>(count);
            list.add(this.getLineByRow(row, start.getColumn(), Integer.MAX_VALUE));
            for (int offset = 1; offset < count - 1; offset = offset + 1) {
                list.add(this.getLineByRow(row + offset));
            }
            list.add(this.getLineByRow(end.getRow(), 1, end.getColumn()));
            result = String.join("\n", list);
        } while (false);
        return result;
    }

    @Override
    public final Iterator<Char> iterator() {
        return new StringSourceIterator(this);
    }

    /**
     * Retrieves a character from the specified row and column in the source code.
     * @param row The 1-based row index.
     * @param column The 1-based column index.
     * @return The character at the given position, {@code '\n'} if the column is out of bounds,
     *  or {@code 0} if the row is invalid.
     */
    char getSymbol(final int row, final int column) {
        char symbol = 0;
        do {
            if (row < 1 || column < 1 || row > this.lines.length) {
                break;
            }
            final String line = this.lines[row - 1];
            final boolean exceeds = column > line.length();
            if (exceeds && row == this.lines.length) {
                break;
            }
            if (exceeds) {
                symbol = '\n';
                break;
            }
            symbol = line.charAt(column - 1);
        } while (false);
        return symbol;
    }

    /**
     * Retrieves a specific line from the stored source code.
     *  If the requested row index is out of bounds, an empty string is returned.
     * @param row The 1-based index of the line to retrieve.
     * @return The requested line, or an empty string if the index is out of range.
     */
    private String getLineByRow(final int row) {
        final int index = row - 1;
        final String line;
        if (index < 0 || index >= this.lines.length) {
            line = "";
        } else {
            line = this.lines[index];
        }
        return line;
    }

    /**
     * Retrieves a substring of a specific line from the stored source code.
     *  The method extracts a portion of the line based on the given column range.
     *  If the specified range extends beyond the line boundaries, it is adjusted
     *  to fit within the available content.
     * @param row The 1-based index of the line to retrieve.
     * @param first The 1-based starting column (inclusive).
     * @param last The 1-based ending column (inclusive).
     * @return The substring of the specified line, or an empty string if out of bounds.
     */
    private String getLineByRow(final int row, final int first, final int last) {
        final String line = this.getLineByRow(row);
        int begin = first - 1;
        if (begin < 0) {
            begin = 0;
        }
        int end = last;
        final int length = line.length();
        if (end > length) {
            end = length;
        }
        return line.substring(begin, end);
    }

}
