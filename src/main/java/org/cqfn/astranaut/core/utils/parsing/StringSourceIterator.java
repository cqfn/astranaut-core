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

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.cqfn.astranaut.core.base.Char;
import org.cqfn.astranaut.core.base.DefaultPosition;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Position;

/**
 * An iterator that splits the source code into individual {@link Node} elements,
 *  where each node represents a single character.
 *  This allows the entire source code to be transformed into a degenerate syntax tree,
 *  where a single root node has all characters as its children.
 *  This structure provides a foundation for further processing, such as node reduction
 *  based on specific rules.
 * @since 2.0.0
 */
final class StringSourceIterator implements Iterator<Char> {
    /**
     * The source of characters to iterate over.
     */
    private final StringSource source;

    /**
     * The current row position (1-based index).
     */
    private int row;

    /**
     * The current column position (1-based index).
     */
    private int column;

    /**
     * The current character being processed.
     */
    private char symbol;

    /**
     * Constructs an iterator for the given {@link StringSource}.
     *  The iterator starts from the first character at position (1,1).
     * @param source The source containing characters to iterate over.
     */
    StringSourceIterator(final StringSource source) {
        this.source = source;
        this.row = 1;
        this.column = 1;
        this.symbol = source.getSymbol(1, 1);
    }

    @Override
    public boolean hasNext() {
        return this.symbol != 0;
    }

    @Override
    public Char next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final Position position = new DefaultPosition(this.source, this.row, this.column);
        final Fragment fragment = new CharFragment(position);
        final Char.Constructor builder = new Char.Constructor();
        builder.setFragment(fragment);
        builder.setValue(this.symbol);
        final Char node = (Char) builder.createNode();
        if (this.symbol == '\n') {
            this.row = this.row + 1;
            this.column = 1;
        } else {
            this.column = this.column + 1;
        }
        this.symbol = this.source.getSymbol(this.row, this.column);
        return node;
    }
}
