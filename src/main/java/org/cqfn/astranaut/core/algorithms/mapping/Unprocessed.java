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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.utils.Pair;

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
    private int deleted;

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
     * Sections of nodes that have not yet been processed.
     */
    private final List<Section> sections;

    /**
     * Constructor.
     * @param left Root node of the left subtree.
     * @param right Root node of the right subtree.
     */
    Unprocessed(final ExtNode left, final ExtNode right) {
        this.inserted = right.getChildCount();
        this.deleted = left.getChildCount();
        this.left = new boolean[this.deleted];
        this.right = new boolean[this.inserted];
        this.sections = new LinkedList<>(
            Collections.singleton(
                new Section(
                    new Sequence(0, left.getChildCount() - 1),
                    new Sequence(0, right.getChildCount() - 1)
                )
            )
        );
    }

    /**
     * Marks node as inserted.
     * @param index Index of the node
     */
    void markAsInserted(final int index) {
        if (this.right[index]) {
            throw new IllegalStateException();
        }
        this.inserted = this.inserted - 1;
        this.right[index] = true;
    }

    /**
     * Marks a pair of nodes as mapped.
     * @param leftidx Index of the first (left) node
     * @param rightidx Index of the second (right) node
     */
    void markAsMapped(final int leftidx, final int rightidx) {
        if (this.left[leftidx] || this.right[rightidx]) {
            throw new IllegalStateException();
        }
        this.inserted = this.inserted - 1;
        this.deleted = this.deleted - 1;
        this.left[leftidx] = true;
        this.right[rightidx] = true;
        final ListIterator<Section> iterator = this.sections.listIterator();
        Section section = null;
        while (iterator.hasNext()) {
            section = iterator.next();
            if (section.hasLeftIndex(leftidx) || section.hasRightIndex(rightidx)) {
                break;
            }
        }
        iterator.remove();
        Pair<Section, Section> pair = section.split(leftidx, rightidx);
        if (pair.getKey() != null) {
            iterator.add(pair.getKey());
        }
        if (pair.getValue() != null) {
            iterator.add(pair.getValue());
        }
    }

    /**
     * Marks node as deleted.
     * @param index Index of the node
     */
    void markAsDeleted(final int index) {
        if (this.left[index]) {
            throw new IllegalStateException();
        }
        this.deleted = this.deleted - 1;
        this.left[index] = true;
    }

    /**
     * Returns a flag whether there is at least one unprocessed left node.
     * @return Flag, {@code true} if there is at least one unmapped left node
     */
    boolean hasAtLeastOneLeftNode() {
        return this.deleted > 0;
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
        return this.inserted > 0 || this.deleted > 0;
    }

    /**
     * Returns the index of the first unprocessed right node.
     * @return Index or -1 if all nodes have been processed
     */
    int getFirstUnprocessedLeftIndex() {
        int result = -1;
        for (int index = 0; index < this.left.length; index = index + 1) {
            if (!this.left[index]) {
                result = index;
                break;
            }
        }
        return result;
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

    /**
     * Returns the first unprocessed section of nodes.
     * @return Object containing unprocessed node indexes or {@code null} if no more sections
     */
    Section getFirstSection() {
        final Section section;
        if (this.sections.isEmpty()) {
            section = null;
        } else {
            section = this.sections.get(0);
        }
        return section;
    }

    /**
     * Defines the beginning and end of a continuous sequence of child nodes
     *  that have yet to be processed.
     * @since 2.0.0
     */
    static final class Sequence {
        /**
         * Index of the first unprocessed child node.
         */
        private final int begin;

        /**
         * Index of the last unprocessed child node.
         */
        private final int end;

        /**
         * Constructor.
         * @param begin Index of the first unprocessed child node
         * @param end Index of the last unprocessed child node
         */
        Sequence(final int begin, final int end) {
            this.begin = begin;
            this.end = end;
        }

        /**
         * Returns the index of the first unprocessed child node.
         * @return Index
         */
        int getBegin() {
            return this.begin;
        }

        /**
         * Returns the index of the last unprocessed child node.
         * @return Index
         */
        int getEnd() {
            return this.end;
        }
    }

    /**
     * Defines a sequence of unprocessed nodes from the left subtree
     *  with a related set of unprocessed nodes from the right subtree.
     * @since 2.0.0
     */
    static final class Section {
        /**
         * Sequence of nodes from the left subtree.
         */
        private final Sequence left;

        /**
         * Sequence of nodes from the right subtree.
         */
        private final Sequence right;

        /**
         * Constructor.
         * @param left Sequence of nodes from the left subtree
         * @param right Sequence of nodes from the right subtree
         */
        private Section(final Sequence left, final Sequence right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Returns sequence of nodes from the left subtree.
         * @return Beginning and end of a continuous sequence of child nodes
         */
        Sequence getLeft() {
            return this.left;
        }

        /**
         * Returns sequence of nodes from the right subtree.
         * @return Beginning and end of a continuous sequence of child nodes
         */
        Sequence getRight() {
            return this.right;
        }

        /**
         * Checks if the section has an index from the left subtree.
         * @param index Index
         * @return Checking result
         */
        private boolean hasLeftIndex(final int index) {
            return this.left != null && index >= this.left.begin && index <= this.left.end;
        }

        /**
         * Checks if the section has an index from the right subtree.
         * @param index Index
         * @return Checking result
         */
        private boolean hasRightIndex(final int index) {
            return this.right != null && index >= this.right.begin && index <= this.right.end;
        }

        /**
         * Splits the section into two.
         * @param leftidx Index of the node from the left subtree by which the split
         *  will be performed
         * @param rightidx Index of the node from the right subtree
         * @return Two, one, or no sections resulting from a split
         */
        private Pair<Section, Section> split(final int leftidx, final int rightidx) {
            if (this.left.begin > leftidx || this.left.end < leftidx
                || this.right.begin > rightidx || this.right.end < rightidx) {
                throw new IllegalStateException();
            }
            final Sequence alpha;
            if (leftidx > this.left.begin) {
                alpha = new Sequence(this.left.begin, leftidx - 1);
            } else {
                alpha = null;
            }
            final Sequence beta;
            if (rightidx > this.right.begin) {
                beta = new Sequence(this.right.begin, rightidx - 1);
            } else {
                beta = null;
            }
            final Section first;
            if (alpha != null || beta != null) {
                first = new Section(alpha, beta);
            } else {
                first = null;
            }
            final Sequence gamma;
            if (leftidx < this.left.end) {
                gamma = new Sequence(leftidx + 1, this.left.end);
            } else {
                gamma = null;
            }
            final Sequence delta;
            if (rightidx < this.right.end) {
                delta = new Sequence(rightidx + 1, this.right.end);
            } else {
                delta = null;
            }
            final Section second;
            if (gamma != null || delta != null) {
                second = new Section(gamma, delta);
            } else {
                second = null;
            }
            return new Pair<>(first, second);
        }
    }
}
