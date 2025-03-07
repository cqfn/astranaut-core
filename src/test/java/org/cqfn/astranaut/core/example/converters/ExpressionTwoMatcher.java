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

import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.algorithms.conversion.Matcher;
import org.cqfn.astranaut.core.base.Node;

/**
 * Matcher that checks for a match with an expression.
 *  If matched, saves the result to hole numbered 2.
 * @since 2.0.0
 */
public final class ExpressionTwoMatcher implements Matcher {
    /**
     * The instance.
     */
    public static final Matcher INSTANCE = new ExpressionTwoMatcher();

    /**
     * Expected type name.
     */
    private static final String TYPE_NAME = "Expression";

    /**
     * Hole number where to save.
     */
    private static final int HOLE_NUMBER = 2;

    /**
     * Private constructor.
     */
    private ExpressionTwoMatcher() {
    }

    @Override
    public boolean match(final Node node, final Extracted extracted) {
        final boolean result = node.belongsToGroup(ExpressionTwoMatcher.TYPE_NAME);
        if (result) {
            extracted.addNode(ExpressionTwoMatcher.HOLE_NUMBER, node);
        }
        return result;
    }
}
