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
package org.cqfn.astranaut.core.base;

/**
 * Represents a pattern which is a syntax tree containing actions, similar to a difference tree,
 *  but with some nodes that can be replaced by "holes".
 * A pattern is a somewhat generalized difference tree. The presence of holes allows the
 *  pattern's tree to be overlaid on subtrees of another syntax tree (a process called matching)
 *  and then replace (add, delete) nodes in the processed syntax tree according to the actions
 *  specified in the pattern (a process called patching or applying the pattern).
 * Using the pattern mechanism enables transferring changes made in one syntax tree
 *  (i.e., the source code from which this syntax tree was derived) to another, unrelated
 *  syntax tree. One possible application of such a mechanism is the detection and automatic
 *  correction of errors in source code.
 *
 * @since 2.0.0
 */
public final class Pattern extends Tree {
    /**
     * Constructor.
     * @param root Root node the difference tree
     */
    public Pattern(final PatternNode root) {
        super(root);
    }

    @Override
    public PatternNode getRoot() {
        return (PatternNode) super.getRoot();
    }
}
