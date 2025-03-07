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

import java.util.Optional;
import org.cqfn.astranaut.core.algorithms.conversion.ConversionResult;
import org.cqfn.astranaut.core.algorithms.conversion.Converter;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.example.green.Addition;
import org.cqfn.astranaut.core.utils.ListUtils;

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
     * Private constructor.
     */
    private AdditionConverter() {
    }

    @Override
    public Optional<ConversionResult> convert(final Node parent, final int index,
        final Factory factory) {
        final Optional<ConversionResult> result;
        final Extracted extracted = new Extracted();
        if (index < parent.getChildCount()
            && ExpressionOneMatcher.INSTANCE.match(parent.getChild(0), extracted)
            && OperatorPlusMatcher.INSTANCE.match(parent.getChild(1), extracted)
            && ExpressionTwoMatcher.INSTANCE.match(parent.getChild(2), extracted)) {
            final Addition.Constructor ctor = new Addition.Constructor();
            ctor.setChildrenList(
                new ListUtils<Node>()
                    .merge(extracted.getNodes(1))
                    .merge(extracted.getNodes(2))
                    .make()
            );
            result = Optional.of(new ConversionResult(ctor.createNode(), index, 3));
        } else {
            result = Optional.empty();
        }
        return result;
    }
}
