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
package org.cqfn.astranaut.core.algorithms.conversion;

import java.util.Optional;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;

/**
 * Interface for converters that check one rule described in DSL
 *  and convert a sequence of child nodes of a given parent node into a single target node.
 * @since 2.0.0
 */
public interface Converter {
    /**
     * Converts a sequence of child nodes starting from the given index.
     * If the conversion is successful, returns an {@code Optional} containing a
     *  {@code ConversionResult}, which includes the new node, the starting index of consumed nodes,
     *  and the number of nodes consumed. If the conversion fails, returns an empty
     *  {@code Optional}, and no nodes are consumed.
     * @param parent The parent node containing the child nodes to be converted
     * @param index The index of the first child node to be converted
     * @param factory The Factory for the creation of new nodes
     * @return An {@code Optional} containing a {@code ConversionResult} if conversion
     *  is successful, otherwise an empty {@code Optional}.
     */
    Optional<ConversionResult> convert(Node parent, int index, Factory factory);
}
