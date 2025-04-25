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
 * Provider interface for obtaining {@link Factory} and {@link Transformer} instances
 *  based on the target programming language.
 *  Language names are case-insensitive (e.g., "Java", "java", "JAVA" are treated the same).
 *  If no specific implementation is found, default fallbacks are returned.
 * @since 2.0.0
 */
public interface Provider {
    /**
     * Retrieves a {@link Factory} instance associated with the specified language.
     *  Language names are case-insensitive. If no matching factory is found,
     *  a default factory is returned that produces only {@link DraftNode}-based structures.
     * @param language The name of the language for which a {@link Factory} is requested,
     *  must not be {@code null} or empty
     * @return A {@link Factory} instance, never {@code null}
     */
    Factory getFactory(String language);

    /**
     * Retrieves a {@link Transformer} that transforms an abstract syntax tree
     *  into a tree suitable for the specified target language.
     *  Language names are case-insensitive. If no matching transformer is found,
     *  a no-op transformer is returned that returns the input node as-is.
     * @param language The name of the target language, must not be {@code null} or empty
     * @return A {@link Transformer} instance, never {@code null}
     */
    Transformer getTransformer(String language);
}
