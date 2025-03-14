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

import org.cqfn.astranaut.core.base.Node;

/**
 * Represents the result of a conversion operation, containing the converted node, the index
 *  of the first consumed node, the number of nodes consumed during the conversion.
 * @since 2.0.0
 */
public final class ConversionResult {
    /**
     * The newly created node as a result of the conversion.
     */
    private final Node node;

    /**
     * The number of nodes that were consumed during the conversion.
     */
    private final int consumed;

    /**
     * Creates a new conversion result.
     * @param node The newly created node
     * @param consumed The number of nodes that were consumed
     */
    public ConversionResult(final Node node, final int consumed) {
        this.node = node;
        this.consumed = consumed;
    }

    /**
     * Returns the converted node.
     * @return The node created as a result of the conversion
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * Returns the number of nodes that were consumed during conversion.
     * @return The count of nodes consumed
     */
    public int getConsumed() {
        return this.consumed;
    }
}
