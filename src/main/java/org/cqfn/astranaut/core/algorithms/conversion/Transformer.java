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

import java.util.List;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;

/**
 * Transforms a tree to another tree using a list of converters.
 * @since 2.0.0
 */
public final class Transformer {
    /**
     * List of converters that are used in the conversion.
     */
    private final List<Converter> converters;

    /**
     * Factory that is used to create the nodes of the resulting trees.
     */
    private final Factory factory;

    /**
     * Constructor.
     * @param converters List of converters that are used in the conversion
     * @param factory Factory that is used to create the nodes of the resulting trees
     */
    public Transformer(final List<Converter> converters, final Factory factory) {
        this.converters = converters;
        this.factory = factory;
    }

    /**
     * Transforms a syntax tree into another tree.
     * @param tree The tree to be transformed
     * @return A new tree, the result of a transformation
     */
    public Tree transform(final Tree tree) {
        return this.transform(tree.getRoot());
    }

    /**
     * Transforms a syntax tree into another tree.
     * @param root The root node of the tree to be transformed
     * @return A new tree, the result of a transformation
     */
    public Tree transform(final Node root) {
        return null;
    }
}
