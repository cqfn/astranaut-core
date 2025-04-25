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
package org.cqfn.astranaut.core.example.converters;

import java.util.List;
import java.util.Optional;
import org.cqfn.astranaut.core.algorithms.conversion.ConversionResult;
import org.cqfn.astranaut.core.algorithms.conversion.Converter;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;

/**
 * A converter that converts a sequence of nodes into an "Addition" node.
 * @since 2.0.0
 */
public final class AdditionConverter implements Converter {
    /**
     * The instance.
     */
    public static final Converter INSTANCE = new AdditionConverter();

    /**
     * The name of the type of node to be generated.
     */
    private static final String NODE_NAME = "Addition";

    /**
     * Private constructor.
     */
    private AdditionConverter() {
    }

    @Override
    public Optional<ConversionResult> convert(final List<Node> nodes, final int index,
        final Factory factory) {
        Optional<ConversionResult> result = Optional.empty();
        do {
            if (index + 3 > nodes.size()) {
                break;
            }
            final Extracted extracted = new Extracted();
            final boolean matched =
                ExpressionOneMatcher.INSTANCE.match(nodes.get(0 + index), extracted)
                && OperatorMatcher2.INSTANCE.match(nodes.get(1 + index), extracted)
                && ExpressionTwoMatcher.INSTANCE.match(nodes.get(2 + index), extracted);
            if (!matched) {
                break;
            }
            final Builder builder = factory.createBuilder(AdditionConverter.NODE_NAME);
            builder.setChildrenList(extracted.getNodes(1, 2));
            result = Optional.of(new ConversionResult(builder.createNode(), 3));
        } while (false);
        return result;
    }

    @Override
    public int getMinConsumed() {
        return 3;
    }
}
