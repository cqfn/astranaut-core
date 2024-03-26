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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.core.Insertion;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.hash.AbsoluteHash;
import org.cqfn.astranaut.core.algorithms.hash.Hash;

/**
 * Top-down mapping algorithm.
 * Compares root nodes first and then children in depth.
 *
 * @since 1.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
final class TopDownAlgorithm {
    /**
     * Set of node hashes.
     */
    private final Hash hashes;

    /**
     * Left-to-right mapping.
     */
    private final Map<Node, Node> ltr;

    /**
     * Right-to-left mapping.
     */
    private final Map<Node, Node> rtl;

    /**
     * Set containing inserted nodes.
     */
    private final Set<Insertion> inserted;

    /**
     * Map containing replaces nodes.
     */
    private final Map<Node, Node> replaced;

    /**
     * Set of deleted nodes.
     */
    private final Set<Node> deleted;

    /**
     * Constructor.
     */
    TopDownAlgorithm() {
        this.hashes = new AbsoluteHash();
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
        this.inserted = new HashSet<>();
        this.replaced = new HashMap<>();
        this.deleted = new HashSet<>();
    }

    /**
     * Performs the mapping.
     * @param left Root node of the 'left' tree
     * @param right Root node of the 'right' tree
     * @return Mapping result ({@code true} if two nodes and their children have been mapped)
     */
    boolean execute(final Node left, final Node right) {
        final boolean result;
        if (this.hashes.calculate(left) == this.hashes.calculate(right)) {
            this.mapSubtreesWithTheSameHash(left, right);
            result = true;
        } else {
            result = left.getTypeName().equals(right.getTypeName())
                && left.getData().equals(right.getData());
            if (result) {
                this.ltr.put(left, right);
                this.rtl.put(right, left);
                TopDownAlgorithm.mapSubtreesWithDifferentHashes(left, right);
            }
        }
        return result;
    }

    /**
     * Returns result of mapping.
     * @return Result of mapping
     */
    Mapping getResult() {
        return new Result(this);
    }

    /**
     * Maps subtrees with the same hash, adding the corresponding nodes to the resulting
     * collections.
     * @param left Left node
     * @param right Related node to the left node
     */
    private void mapSubtreesWithTheSameHash(final Node left, final Node right) {
        assert this.hashes.calculate(left) == this.hashes.calculate(right);
        this.ltr.put(left, right);
        this.rtl.put(right, left);
        final int count = left.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final Node first = left.getChild(index);
            final Node second = left.getChild(index);
            this.mapSubtreesWithTheSameHash(first, second);
        }
    }

    /**
     * Maps subtrees with different hashes.
     * @param left Left node
     * @param right Related node to the left node
     */
    private static void mapSubtreesWithDifferentHashes(final Node left, final Node right) {
        final Unprocessed counter = new Unprocessed(left, right);
        assert counter.hasUnprocessedNodes();
    }

    /**
     * Mapping result.
     *
     * @since 1.1.0
     */
    private static final class Result implements Mapping {
        /**
         * Structure from which the mapping results can be taken.
         */
        private final TopDownAlgorithm data;

        /**
         * Constructor.
         * @param data Structure from which the mapping results can be taken
         */
        private Result(final TopDownAlgorithm data) {
            this.data = data;
        }

        @Override
        public Node getRight(final Node node) {
            return this.data.ltr.get(node);
        }

        @Override
        public Node getLeft(final Node node) {
            return this.data.rtl.get(node);
        }

        @Override
        public Set<Insertion> getInserted() {
            return Collections.unmodifiableSet(this.data.inserted);
        }

        @Override
        public Map<Node, Node> getReplaced() {
            return Collections.unmodifiableMap(this.data.replaced);
        }

        @Override
        public Set<Node> getDeleted() {
            return Collections.unmodifiableSet(this.data.deleted);
        }
    }

    /**
     * Number of unprocessed child nodes.
     *
     * @since 1.1.0
     */
    private static class Unprocessed {
        /**
         * Number of unprocessed children of left node.
         */
        private int left;

        /**
         * Number of unprocessed children of right node.
         */
        private int right;

        /**
         * Number of nodes to be added.
         */
        private final int add;

        /**
         * Number of nodes to be deleted.
         */
        private final int delete;

        /**
         * Constructor.
         * @param left Left node whose children will be analyzed
         * @param right Right node
         */
        Unprocessed(final Node left, final Node right) {
            this.left = left.getChildCount();
            this.right = right.getChildCount();
            this.add = Math.max(this.right - this.left, 0);
            this.delete = Math.max(this.left - this.right, 0);
        }

        /**
         * Checks are there still unprocessed nodes.
         * @return Checking result ({@code true} if yes)
         */
        boolean hasUnprocessedNodes() {
            return this.left > 0 && this.right > 0;
        }

        /**
         * Analyzes a case where the only actions that are allowed are additions.
         * @return Checking result, {@code true} if we can only add nodes
         */
        boolean onlyActionIsToAddNodes() {
            return this.left == 0 && this.add == this.right;
        }

        /**
         * Analyzes a case where the only actions that are allowed are deletions.
         * @return Checking result, {@code true} if we can only delete nodes
         */
        boolean onlyActionIsToDeleteNodes() {
            return this.right == 0 && this.delete == this.left;
        }

        /**
         * Marks that some child of the left node has been replaced by a child of the right node.
         */
        void nodeWasReplaced() {
            this.left = this.left - 1;
            this.right = this.right - 1;
            assert this.right >= this.add;
            assert this.left >= this.delete;
        }
    }
}
