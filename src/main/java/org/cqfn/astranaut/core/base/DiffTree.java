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
 * Difference tree represents the difference between two syntax trees.
 * This tree structure contains both "regular" nodes and actions. An action is
 *  a special type of node that specifies the operations required to transform
 *  a subtree from the original syntax tree to the modified one.
 * Thus, a DifferentialTree encapsulates both the original tree and the modified
 *  tree, detailing the differences and the changes needed to transition from one
 *  to the other.
 *
 * @since 2.0.0
 */
public class DiffTree {
    /**
     * Root node of the difference tree.
     */
    private final DiffNode root;

    /**
     * Constructor.
     * @param root Root node the difference tree
     */
    public DiffTree(final DiffNode root) {
        this.root = root;
    }

    /**
     * Returns the root node of the tree.
     * @return Root node of the tree
     */
    public DiffNode getRoot() {
        return this.root;
    }

    /**
     * Returns the syntax tree before the changes were applied.
     * @return The syntax tree before the changes
     */
    public Tree getBefore() {
        return new Tree(this.root.getBefore());
    }

    /**
     * Returns the syntax tree after the changes were applied.
     * @return The syntax tree after the changes
     */
    public Tree getAfter() {
        return new Tree(this.root.getAfter());
    }

    @Override
    public final String toString() {
        return this.root.toString();
    }
}
