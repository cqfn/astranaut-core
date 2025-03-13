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
import org.cqfn.astranaut.core.base.Char;
import org.cqfn.astranaut.core.base.Position;
import org.cqfn.astranaut.core.utils.FilesReader;

/**
 * A {@link CharParser} implementation that reads characters from a file.
 *  This class wraps a {@link StringSource}, which holds the file content as a sequence
 *  of characters. It allows iteration over characters and provides functionality
 *  for extracting fragments.
 * @since 2.0.0
 */
public final class FileSource implements CharParser {
    /**
     * The path to the file containing source code.
     */
    private final String path;

    /**
     * The underlying {@link StringSource} that stores the file content.
     */
    private final StringSource wrapped;

    /**
     * Constructs a {@code FileSource} by reading the content of the specified file.
     * @param path The path to the file to be read.
     */
    public FileSource(final String path) {
        this.path = path;
        this.wrapped = new StringSource(new FilesReader(path).readAsStringNoExcept());
    }

    @Override
    public Iterator<Char> iterator() {
        return this.wrapped.iterator();
    }

    @Override
    public String getFragmentAsString(final Position start, final Position end) {
        return this.wrapped.getFragmentAsString(start, end);
    }

    @Override
    public String getFileName() {
        return this.path;
    }
}
