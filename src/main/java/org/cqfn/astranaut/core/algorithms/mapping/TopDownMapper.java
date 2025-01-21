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
package org.cqfn.astranaut.core.algorithms.mapping;

import org.cqfn.astranaut.core.algorithms.ExtNodeCreator;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.base.Node;

/**
 * Top-down mapper.
 *  Compares root nodes first and then children in depth.
 * @since 1.1.0
 */
public final class TopDownMapper implements Mapper {
    /**
     * The instance.
     */
    public static final Mapper INSTANCE = new TopDownMapper();

    /**
     * Private constructor.
     */
    private TopDownMapper() {
    }

    @Override
    public Mapping map(final Node left, final Node right) {
        final TopDownAlgorithm algorithm = new TopDownAlgorithm();
        final ExtNodeCreator builder = new ExtNodeCreator();
        final ExtNode first = builder.create(left);
        final ExtNode second = builder.create(right);
        algorithm.execute(first, second);
        return new TopDownMapping(algorithm);
    }
}
