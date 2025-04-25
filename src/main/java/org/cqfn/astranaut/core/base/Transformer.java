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
 * Transforms a tree to another tree using some strategy.
 * @since 2.0.0
 */
public interface Transformer {
    /**
     * Transforms the given syntax tree into another tree.
     *  The input tree remains unchanged. Implementations must ensure that the returned
     *  {@link Tree} is either a modified copy or the original if no transformation was needed.
     * @param tree The tree to be transformed, must not be {@code null}
     * @return A new {@link Tree} representing the result of the transformation,
     *  never {@code null}
     */
    default Tree transform(Tree tree) {
        return new Tree(this.transform(tree.getRoot()));
    }

    /**
     * Transforms a single node, potentially recursively modifying its children.
     *  This method defines the core logic of the transformation. It is typically
     *  called on the root node of the tree and is responsible for transforming
     *  the entire subtree rooted at that node.
     * @param node The root node of the subtree to transform, must not be {@code null}
     * @return The transformed {@link Node}, never {@code null}
     */
    Node transform(Node node);
}
