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
 * Factory interface for creating and managing types and builders.
 * @since 1.0
 */
public interface Factory {
    /**
     * Retrieves the type associated with the given name.
     * @param name The name of the type to retrieve
     * @return The corresponding Type object, or {@code null} if not found
     */
    Type getType(String name);

    /**
     * Creates a builder for the given type name.
     * @param name The name of the type to create a builder for
     * @return A Builder instance corresponding to the type, or a default {@link DraftNode} builder
     *  if the type is not found
     */
    default Builder createBuilder(final String name) {
        final Builder builder;
        final Type type = this.getType(name);
        if (type == null) {
            final DraftNode.Constructor draft = new DraftNode.Constructor();
            draft.setName(name);
            builder = draft;
        } else {
            builder = type.createBuilder();
        }
        return builder;
    }
}
