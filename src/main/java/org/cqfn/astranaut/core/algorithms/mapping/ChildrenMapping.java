/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import org.cqfn.astranaut.core.base.ExtNode;

/**
 * Auxiliary structure used for mapping child nodes.
 * @since 2.0.0
 */
class ChildrenMapping {
    /**
     * Array of node flags of the left subtree, {@code true} means the node is processed
     *  (mapped, added, deleted).
     */
    private final boolean[] left;

    /**
     * Array of node flags of the right subtree, {@code true} means the node is processed
     *  (mapped, added, deleted).
     */
    private final boolean[] right;

    /**
     * Constructor.
     * @param left Root node of the left subtree.
     * @param right Root node of the right subtree.
     */
    ChildrenMapping(final ExtNode left, final ExtNode right) {
        this.left = new boolean[left.getChildCount()];
        this.right = new boolean[right.getChildCount()];
    }
}
