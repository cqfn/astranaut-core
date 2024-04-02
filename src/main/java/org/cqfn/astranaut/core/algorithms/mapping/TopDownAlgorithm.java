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
                this.mapSubtreesWithDifferentHashes(left, right);
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
    private void mapSubtreesWithDifferentHashes(final Node left, final Node right) {
        final Unprocessed unprocessed = new Unprocessed(left, right);
        assert unprocessed.hasNodes();
        do {
            if (unprocessed.onlyActionIsToInsertNodes()) {
                this.insertAllNotYetMappedNodes(right);
                break;
            }
            if (unprocessed.onlyActionIsToDeleteNodes()) {
                this.deleteAllNotYetMappedNodes(left);
                break;
            }
            if (this.mapTwoFirstUnmappedNodes(left, right, unprocessed)) {
                continue;
            }
            if (this.mapTwoLastUnmappedNodes(left, right, unprocessed)) {
                continue;
            }
            this.replaceTwoFirstUnmappedNodes(left, right, unprocessed);
        } while (unprocessed.hasNodes());
    }

    /**
     * Finds the first unmapped child of the left node and the first unmapped child
     *  of the right node and tries to map them.
     * @param left Left node
     * @param right Related node to the left node
     * @param unprocessed Number of unprocessed nodes
     * @return Mapping result, {@code true} if such nodes were found and mapped
     */
    private boolean mapTwoFirstUnmappedNodes(final Node left, final Node right,
        final Unprocessed unprocessed) {
        final Child first = this.findFirstUnmappedChild(left);
        final Child second = this.findFirstUnmappedChild(right);
        boolean result;
        do {
            result = this.execute(first.node, second.node);
            if (result) {
                unprocessed.removeOnePair();
                break;
            }
            if (second.after != null) {
                result = this.execute(first.node, second.after);
            }
            if (result) {
                unprocessed.removeOnePair();
                final Insertion insertion = new Insertion(second.node, right, first.before);
                this.inserted.add(insertion);
                this.rtl.put(second.node, null);
                unprocessed.nodeWasInserted();
                break;
            }
            if (first.after != null) {
                result = this.execute(first.after, second.node);
            }
            if (result) {
                unprocessed.removeOnePair();
                this.deleted.add(first.node);
                this.ltr.put(first.node, null);
                unprocessed.nodeWasDeleted();
                break;
            }
        } while (false);
        return result;
    }

    /**
     * Finds the first unmapped child of the left node and the first unmapped child
     *  of the right node and adds a 'Replace' operation for them.
     * This is a universal operation because it reduces the number of unprocessed pairs,
     *  and sooner or later there will be no nodes left and the algorithm will inevitably
     *  terminate with some result. This is fate. However, this operation may produce
     *  suboptimal results, and should therefore be used last.
     * @param left Left node
     * @param right Related node to the left node
     * @param unprocessed Number of unprocessed nodes
     */
    private void replaceTwoFirstUnmappedNodes(final Node left, final Node right,
        final Unprocessed unprocessed) {
        final Node first = this.findFirstUnmappedChild(left).node;
        final Node second = this.findFirstUnmappedChild(right).node;
        this.replaced.put(first, second);
        this.ltr.put(first, second);
        this.rtl.put(second, first);
        unprocessed.removeOnePair();
    }

    /**
     * Finds the first child node that has not yet been mapped.
     * @param node Parent node
     * @return First child node that has not yet been mapped
     */
    private Child findFirstUnmappedChild(final Node node) {
        final int count = node.getChildCount();
        Child result = null;
        for (int index = 0; index < count; index = index + 1) {
            final Node child = node.getChild(index);
            if (!this.ltr.containsKey(child) && !this.rtl.containsKey(child)) {
                Node before = null;
                if (index > 0) {
                    before = node.getChild(index - 1);
                }
                Node after = null;
                if (index < count - 1) {
                    after = node.getChild(index + 1);
                }
                result = new Child(child, before, after);
                break;
            }
        }
        assert result != null;
        return result;
    }

    /**
     * Finds the last unmapped child of the left node and the last unmapped child
     *  of the right node and tries to map them.
     * @param left Left node
     * @param right Related node to the left node
     * @param unprocessed Number of unprocessed nodes
     * @return Mapping result, {@code true} if such nodes were found and mapped
     */
    private boolean mapTwoLastUnmappedNodes(final Node left, final Node right,
        final Unprocessed unprocessed) {
        final Node first = this.findLastUnmappedChild(left);
        final Node second = this.findLastUnmappedChild(right);
        final boolean result = this.execute(first, second);
        if (result) {
            unprocessed.removeOnePair();
        }
        return result;
    }

    /**
     * Finds the last child node that has not yet been mapped.
     * @param node Parent node
     * @return Last child node that has not yet been mapped
     */
    private Node findLastUnmappedChild(final Node node) {
        final int count = node.getChildCount();
        Node result = null;
        for (int index = count - 1; index >= 0; index = index - 1) {
            final Node child = node.getChild(index);
            if (!this.ltr.containsKey(child) && !this.rtl.containsKey(child)) {
                result = child;
                break;
            }
        }
        assert result != null;
        return result;
    }

    /**
     * For all child nodes of the right node that are not yet mapped, performs
     *  the 'Insert' operation.
     * @param right Related node to the left node
     */
    private void insertAllNotYetMappedNodes(final Node right) {
        final int count = right.getChildCount();
        Node after = null;
        for (int index = 0; index < count; index = index + 1) {
            final Node node = right.getChild(index);
            if (this.rtl.containsKey(node)) {
                after = this.rtl.get(node);
            } else {
                final Insertion insertion = new Insertion(node, right, after);
                this.inserted.add(insertion);
                this.rtl.put(node, null);
                after = node;
            }
        }
    }

    /**
     * For all child nodes of the left node that are not yet mapped, performs
     *  the 'Delete' operation.
     * @param left Related node to the left node
     */
    private void deleteAllNotYetMappedNodes(final Node left) {
        final int count = left.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final Node node = left.getChild(index);
            if (!this.ltr.containsKey(node)) {
                this.deleted.add(node);
                this.ltr.put(node, null);
            }
        }
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
        private int add;

        /**
         * Number of nodes to be deleted.
         */
        private int delete;

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
        boolean hasNodes() {
            return this.left > 0 || this.right > 0;
        }

        /**
         * Analyzes a case where the only actions that are allowed are insertions.
         * @return Checking result, {@code true} if we can only add nodes
         */
        boolean onlyActionIsToInsertNodes() {
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
         * Notes that some child node of the right node has been recognized as an inserted node.
         */
        void nodeWasInserted() {
            assert this.right > 0;
            this.right = this.right - 1;
            if (this.add > 0) {
                this.add = this.add - 1;
            } else {
                this.delete = this.delete + 1;
            }
        }

        /**
         * Notes that some child node of the right node has been recognized as a deleted node.
         */
        void nodeWasDeleted() {
            assert this.left > 0;
            this.left = this.left - 1;
            if (this.delete > 0) {
                this.delete = this.delete - 1;
            } else {
                this.add = this.add + 1;
            }
        }

        /**
         * Marks that some child of the left node has been mapped or replaced by a child
         * of the right node.
         */
        void removeOnePair() {
            this.left = this.left - 1;
            this.right = this.right - 1;
            assert this.right >= this.add;
            assert this.left >= this.delete;
        }
    }

    /**
     * A child node found by some criteria.
     *
     * @since 1.1.0
     */
    private static class Child {
        /**
         * Child node itself.
         */
        private final Node node;

        /**
         * Child node before (if exists).
         */
        private final Node before;

        /**
         * Child node after (if exists).
         */
        private final Node after;

        /**
         * Constructor.
         * @param node Child node itself
         * @param before Child node before
         * @param after Child node after
         */
        Child(final Node node, final Node before, final Node after) {
            this.node = node;
            this.before = before;
            this.after = after;
        }

        @Override
        public String toString() {
            return this.node.toString();
        }
    }
}

