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
 * Auxiliary algorithm for top-down mapping.
 *  Finds pairs of matching nodes.
 *  Matching is determined by some criterion, usually hash equality.
 * @since 2.0.0
 */
final  class NodePairFinder {
    /**
     * Converter that takes the absolute hash of a node.
     */
    static final Converter ABSOLUTE_HASH = ExtNode::getAbsoluteHash;

    /**
     * Converter that takes the local hash of a node.
     */
    static final Converter LOCAL_HASH = ExtNode::getLocalHash;

    /**
     * Significant difference between node indices, that is, if the node indices
     *  differ by more than this number, we consider it to be a lot and look for more variants.
     */
    private static final int SIGNIFICANT_DIFF = 3;

    /**
     * Representing the left side of the unprocessed node section as an array of numbers.
     */
    private final int[] left;

    /**
     * Representing the left side of the unprocessed node section as an array of numbers.
     */
    private final int[] right;

    /**
     * Constructor.
     * @param section Section containing unprocessed nodes
     * @param converter Converter than converts nodes to numbers
     */
    NodePairFinder(final Section section, final Converter converter) {
        this.left = section.getLeft().stream().mapToInt(converter::convertNode).toArray();
        this.right = section.getRight().stream().mapToInt(converter::convertNode).toArray();
    }

    /**
     * Finds the best sequence of matching elements. The selection criterion is the sequence
     *  with the largest number of consecutive matched elements, such that the difference
     *  between the indices of the left and right matched elements is minimal.
     * @return Result containing indices of found elements
     */
    Result findMatchingSequence() {
        int size = Math.min(this.left.length, this.right.length);
        Result seq = null;
        while (size > 0 && seq == null) {
            seq = this.findMatchingSequence(size);
            size = size - 1;
        }
        while (size > 0 && seq.getOffsetDiff() > NodePairFinder.SIGNIFICANT_DIFF) {
            final Result other = this.findMatchingSequence(size);
            size = size - 1;
            if (seq.getOffsetDiff() - other.getOffsetDiff() > NodePairFinder.SIGNIFICANT_DIFF) {
                seq = other;
            }
        }
        if (seq == null) {
            seq = new Result();
            seq.left = -1;
            seq.right = -1;
        }
        return seq;
    }

    /**
     * Find a matching sequence of a specific size.
     * @param size Size of sequence
     * @return Result containing indices of found elements or {@code null} if no sequence
     *  of that size is found
     */
    private Result findMatchingSequence(final int size) {
        Result seq = null;
        int min = Integer.MAX_VALUE;
        for (int loffset = 0; min > 0 && loffset <= this.left.length - size;
            loffset = loffset + 1) {
            for (int roffset = 0; min > 0 && roffset <= this.right.length - size;
                roffset = roffset + 1) {
                final boolean matches = this.compareSegments(loffset, roffset, size);
                if (!matches) {
                    continue;
                }
                final int diff = Math.abs(loffset - roffset);
                if (seq == null) {
                    seq = new Result();
                    seq.left = loffset;
                    seq.right = roffset;
                    seq.count = size;
                    min = diff;
                } else if (min > diff) {
                    seq.left = loffset;
                    seq.right = roffset;
                    min = diff;
                }
            }
        }
        return seq;
    }

    /**
     * Compares segments of arrays.
     * @param loffset Index of the first compared element of the left array
     * @param roffset Index of the first compared element of the right array
     * @param count Number of elements to be compared
     * @return Comparison result, {@code true} if all segment elements are equal
     */
    private boolean compareSegments(final int loffset, final int roffset, final int count) {
        boolean equals = true;
        for (int index = 0; index < count; index = index + 1) {
            if (this.left[index + loffset] != this.right[index + roffset]) {
                equals = false;
                break;
            }
        }
        return equals;
    }

    /**
     * Converts a node to a number.
     *  This number will be used when comparing nodes and finding matches.
     * @since 2.0.0
     */
    interface Converter {
        /**
         * Converts node to a number.
         * @param node Node
         * @return A number
         */
        int convertNode(ExtNode node);
    }

    /**
     * Matching result.
     * @since 2.0.0
     */
    static final class Result {
        /**
         * Index of the first matched element from the left array.
         */
        private int left;

        /**
         * Index of the first matched element from the right array.
         */
        private int right;

        /**
         * Number of matched pairs arranged sequentially.
         */
        private int count;

        /**
         * Returns the index of the first matched element from the left array.
         * @return Index
         */
        int getLeftOffset() {
            return this.left;
        }

        /**
         * Returns the index of the first matched element from the right array.
         * @return Index
         */
        int getRightOffset() {
            return this.right;
        }

        /**
         * Returns the number of matched pairs arranged sequentially.
         * @return Number of matched pairs
         */
        int getCount() {
            return this.count;
        }

        /**
         * Returns the difference between the indexes of the first elements.
         * @return Difference between the indexes
         */
        private int getOffsetDiff() {
            return Math.abs(this.left - this.right);
        }
    }
}
