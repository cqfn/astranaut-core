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
 * Auxiliary structure for mapping child nodes, storing information about which nodes
 *  have not yet been processed.
 * @since 2.0.0
 */
class Unprocessed {
    /**
     * Number of right nodes that have not yet been mapped.
     */
    private int inserted;

    /**
     * Number of left nodes that have not yet been mapped.
     */
    private int removed;

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
    Unprocessed(final ExtNode left, final ExtNode right) {
        this.inserted = right.getChildCount();
        this.removed = left.getChildCount();
        this.left = new boolean[this.removed];
        this.right = new boolean[this.inserted];
    }

    /**
     * Marks a pair of nodes as mapped.
     * @param first Index of the first (left) node
     * @param second Index of the second (right) node
     */
    void markAsMapped(int first, int second) {
        if (this.left[first] || this.right[second]) {
            throw new IllegalStateException();
        }
        this.inserted = this.inserted - 1;
        this.removed = this.removed - 1;
        this.left[first] = true;
        this.right[second] = true;
    }

    void markAsInserted(int index) {
        if (this.right[index]) {
            throw new IllegalStateException();
        }
        this.inserted = this.inserted - 1;
        this.right[index] = true;
    }

    /**
     * Returns a flag whether there is at least one unprocessed left node.
     * @return Flag, {@code true} if there is at least one unmapped left node
     */
    boolean hasAtLeastOneLeftNode() {
        return this.removed > 0;
    }

    /**
     * Returns a flag whether there is at least one unprocessed right node.
     * @return Flag, {@code true} if there is at least one unmapped right node
     */
    boolean hasAtLeastOneRightNode() {
        return this.inserted > 0;
    }

    /**
     * Returns a flag whether there is at least one unprocessed node.
     * @return Flag, {@code true} if there is at least one unmapped right node
     */
    boolean hasAtLeastOneNode() {
        return this.inserted > 0 || this.removed > 0;
    }

    /**
     * Returns the index of the first unprocessed right node.
     * @return Index or -1 if all nodes have been processed
     */
    int getFirstUnprocessedRightIndex() {
        int result = -1;
        for (int index = 0; index < this.right.length; index = index + 1) {
            if (!this.right[index]) {
                result = index;
                break;
            }
        }
        return result;
    }
}
