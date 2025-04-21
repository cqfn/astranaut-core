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

import java.util.Objects;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Position;

/**
 * Represents a fragment that consists of a single character in the source code.
 *  Since this fragment corresponds to only one character, both the start and end positions
 *  are the same.
 *
 * @since 2.0.0
 */
class CharFragment implements Fragment {
    /**
     * The position of the single character in the source code.
     */
    private final Position position;

    /**
     * Constructs a {@code CharFragment} with the given position.
     * @param position The position of the character in the source.
     */
    CharFragment(final Position position) {
        this.position = position;
    }

    @Override
    public Position getBegin() {
        return this.position;
    }

    @Override
    public Position getEnd() {
        return this.position;
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj instanceof Fragment) {
            final Fragment other = (Fragment) obj;
            result = this.position.equals(other.getBegin())
                && this.position.equals(other.getEnd());
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.position, this.position);
    }

    @Override
    public String toString() {
        final String result;
        final String path = this.position.getSource().getFileName();
        if (path.isEmpty()) {
            result = String.format(
                "%d.%d",
                this.position.getRow(),
                this.position.getColumn()
            );
        } else {
            result = String.format(
                "%s, %d.%d",
                path,
                this.position.getRow(),
                this.position.getColumn()
            );
        }
        return result;
    }
}
