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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.cqfn.astranaut.core.algorithms.ExtNodeCreator;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;

/**
 * Top-down mapping algorithm.
 *  Compares root nodes first and then children in depth.
 * @since 1.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
final class TopDownAlgorithm {
    /**
     * Left-to-right mapping.
     */
    private final Map<ExtNode, ExtNode> ltr;

    /**
     * Right-to-left mapping.
     */
    private final Map<ExtNode, ExtNode> rtl;

    /**
     * Set containing inserted nodes.
     */
    private final Set<ExtInsertion> inserted;

    /**
     * Map containing replaces nodes.
     */
    private final Map<ExtNode, ExtNode> replaced;

    /**
     * Set of deleted nodes.
     */
    private final Set<ExtNode> deleted;

    /**
     * Constructor.
     */
    TopDownAlgorithm() {
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
     */
    void execute(final Node left, final Node right) {
        final ExtNodeCreator builder = new ExtNodeCreator();
        final ExtNode first = builder.create(left);
        final ExtNode second = builder.create(right);
        final boolean result = this.execute(builder.create(left), builder.create(right));
        if (!result) {
            this.replaced.put(first, second);
            this.skipLeftSubtree(first);
            this.skipRightSubtree(second);
        }
    }

    /**
     * Returns result of mapping.
     * @return Result of mapping
     */
    Mapping getResult() {
        return new Result(this);
    }

    /**
     * Skips the left subtree, considering that all its nodes cannot be matched.
     * @param node Root node of the subtree
     */
    private void skipLeftSubtree(final ExtNode node) {
        this.ltr.put(node, null);
        for (int index = 0; index < node.getChildCount(); index = index + 1) {
            this.skipLeftSubtree(node.getExtChild(index));
        }
    }

    /**
     * Skips the right subtree, considering that all its nodes cannot be matched.
     * @param node Root node of the subtree
     */
    private void skipRightSubtree(final ExtNode node) {
        this.rtl.put(node, null);
        for (int index = 0; index < node.getChildCount(); index = index + 1) {
            this.skipRightSubtree(node.getExtChild(index));
        }
    }

    /**
     * Performs the mapping (private method).
     * @param left Extended root node of the 'left' tree
     * @param right Extended root node of the 'right' tree
     * @return Mapping result ({@code true} if two nodes and their children have been mapped)
     */
    private boolean execute(final ExtNode left, final ExtNode right) {
        final boolean result;
        if (left.getAbsoluteHash() == right.getAbsoluteHash()) {
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
     * Maps subtrees with the same hash, adding the corresponding nodes to the resulting
     *  collections.
     * @param left Left node
     * @param right Related node to the left node
     */
    private void mapSubtreesWithTheSameHash(final ExtNode left, final ExtNode right) {
        this.ltr.put(left, right);
        this.rtl.put(right, left);
        for (int index = 0; index < left.getChildCount(); index = index + 1) {
            this.mapSubtreesWithTheSameHash(left.getExtChild(index), right.getExtChild(index));
        }
    }

    /**
     * Maps subtrees with different hashes.
     * @param left Left node
     * @param right Related node to the left node
     */
    private void mapSubtreesWithDifferentHashes(final ExtNode left, final ExtNode right) {
        final Unprocessed unprocessed = new Unprocessed(left, right);
        do {
            if (unprocessed.onlyActionIsToInsertNodes()) {
                this.insertAllNotYetMappedNodes(left, right);
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
    private boolean mapTwoFirstUnmappedNodes(final ExtNode left, final ExtNode right,
        final Unprocessed unprocessed) {
        final ExtNode first = this.findFirstUnmappedChild(left);
        final ExtNode second = this.findFirstUnmappedChild(right);
        boolean result = this.matchTwoIdenticalNodesOrRightNeighbors(first, second, unprocessed);
        if (!result) {
            result = this.matchTwoDifferenceNodesOrRightNeighbors(first, second, unprocessed);
        }
        return result;
    }

    /**
     * Checks if a node can be associated with another node or with a node's right neighbor
     *  because some of them are identical.
     * @param first First node
     * @param second Second node
     * @param unprocessed Number of unprocessed nodes
     * @return Mapping result, {@code true} if nodes were mapped
     */
    private boolean matchTwoIdenticalNodesOrRightNeighbors(final ExtNode first,
        final ExtNode second, final Unprocessed unprocessed) {
        final boolean result;
        if (first.getAbsoluteHash() == second.getAbsoluteHash()) {
            this.mapSubtreesWithTheSameHash(first, second);
            unprocessed.removeOnePair();
            result = true;
        } else if (second.getRight() != null
            && first.getAbsoluteHash() == second.getRight().getAbsoluteHash()) {
            this.mapSubtreesWithTheSameHash(first, second.getRight());
            unprocessed.removeOnePair();
            final ExtInsertion insertion = new ExtInsertion(
                second,
                first.getParent(),
                first.getLeft()
            );
            this.inserted.add(insertion);
            this.skipRightSubtree(second);
            unprocessed.nodeWasInserted();
            result = true;
        } else if (first.getRight() != null
            && first.getRight().getAbsoluteHash() == second.getAbsoluteHash()) {
            this.mapSubtreesWithTheSameHash(first.getRight(), second);
            unprocessed.removeOnePair();
            this.deleted.add(first);
            this.skipLeftSubtree(first);
            unprocessed.nodeWasDeleted();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Checks if a node can be related to another node or to the right neighbor of a node,
     *  since children of some nodes can be related, although all these nodes are not
     *  absolutely identical.
     * @param first First node
     * @param second Second node
     * @param unprocessed Number of unprocessed nodes
     * @return Mapping result, {@code true} if nodes were mapped
     */
    private boolean matchTwoDifferenceNodesOrRightNeighbors(final ExtNode first,
        final ExtNode second, final Unprocessed unprocessed) {
        boolean result;
        do {
            result = this.execute(first, second);
            if (result) {
                unprocessed.removeOnePair();
                break;
            }
            if (second.getRight() != null) {
                result = this.execute(first, second.getRight());
            }
            if (result) {
                unprocessed.removeOnePair();
                final ExtInsertion insertion = new ExtInsertion(
                    second,
                    first.getParent(),
                    first.getLeft()
                );
                this.inserted.add(insertion);
                this.skipRightSubtree(second);
                unprocessed.nodeWasInserted();
                break;
            }
            if (first.getRight() != null) {
                result = this.execute(first.getRight(), second);
            }
            if (result) {
                unprocessed.removeOnePair();
                this.deleted.add(first);
                this.skipLeftSubtree(first);
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
    private void replaceTwoFirstUnmappedNodes(final ExtNode left, final ExtNode right,
        final Unprocessed unprocessed) {
        final ExtNode first = this.findFirstUnmappedChild(left);
        final ExtNode second = this.findFirstUnmappedChild(right);
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
    private ExtNode findFirstUnmappedChild(final ExtNode node) {
        ExtNode result = null;
        int index = 0;
        do {
            final ExtNode child = node.getExtChild(index);
            if (!this.ltr.containsKey(child) && !this.rtl.containsKey(child)) {
                result = child;
            }
            index = index + 1;
        } while (result == null);
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
    private boolean mapTwoLastUnmappedNodes(final ExtNode left, final ExtNode right,
        final Unprocessed unprocessed) {
        final ExtNode first = this.findLastUnmappedChild(left);
        final ExtNode second = this.findLastUnmappedChild(right);
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
    private ExtNode findLastUnmappedChild(final ExtNode node) {
        final int count = node.getChildCount();
        ExtNode result = null;
        int index = count - 1;
        do {
            final ExtNode child = node.getExtChild(index);
            if (!this.ltr.containsKey(child) && !this.rtl.containsKey(child)) {
                result = child;
            }
            index = index - 1;
        } while (result == null);
        return result;
    }

    /**
     * For all child nodes of the right node that are not yet mapped, performs
     *  the 'Insert' operation.
     * @param left Left node
     * @param right Related node to the left node
     */
    private void insertAllNotYetMappedNodes(final ExtNode left, final ExtNode right) {
        final int count = right.getChildCount();
        ExtNode after = null;
        for (int index = 0; index < count; index = index + 1) {
            final ExtNode node = right.getExtChild(index);
            if (this.rtl.containsKey(node)) {
                after = this.rtl.get(node);
            } else {
                final ExtInsertion insertion = new ExtInsertion(node, left, after);
                this.inserted.add(insertion);
                this.skipRightSubtree(node);
                after = node;
            }
        }
    }

    /**
     * For all child nodes of the left node that are not yet mapped, performs
     *  the 'Delete' operation.
     * @param left Left node
     */
    private void deleteAllNotYetMappedNodes(final ExtNode left) {
        final int count = left.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final ExtNode node = left.getExtChild(index);
            if (!this.ltr.containsKey(node)) {
                this.deleted.add(node);
                this.ltr.put(node, null);
            }
        }
    }

    /**
     * Insertion descriptor like {@link Insertion}, but working with extended nodes.
     * @since 2.0.0
     */
    private static final class ExtInsertion {
        /**
         * Node being inserted.
         */
        private final ExtNode inserted;

        /**
         * Parent node into which the child node will be inserted.
         */
        private final ExtNode into;

        /**
         * Child node after which to insert.
         */
        private final ExtNode after;

        /**
         * Constructor.
         * @param inserted Node being inserted
         * @param into Parent node into which the child node will be inserted
         * @param after Child node after which to insert
         */
        ExtInsertion(final ExtNode inserted, final ExtNode into, final ExtNode after) {
            this.inserted = Objects.requireNonNull(inserted);
            this.into = into;
            this.after = after;
        }

        /**
         * Converts the descriptor to a 'classic' {@link Insertion}.
         * @return An insertion descriptor that uses non-extended nodes
         */
        public Insertion toInsertion() {
            final Node third;
            if (this.after == null) {
                third = null;
            } else {
                third = this.after.getPrototype();
            }
            return new Insertion(this.inserted.getPrototype(), this.into.getPrototype(), third);
        }
    }

    /**
     * Mapping result.
     * @since 1.1.0
     */
    private static final class Result implements Mapping {
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
         * @param data Structure from which the mapping results can be taken
         */
        private Result(final TopDownAlgorithm data) {
            this.ltr = Result.convert(data.ltr);
            this.rtl = Result.convert(data.rtl);
            this.inserted = Collections.unmodifiableSet(
                data.inserted.stream()
                    .map(ExtInsertion::toInsertion)
                    .collect(Collectors.toSet())
            );
            this.replaced = Result.convert(data.replaced);
            this.deleted = Collections.unmodifiableSet(
                data.deleted.stream()
                    .map(ExtNode::getPrototype)
                    .collect(Collectors.toSet())
            );
        }

        @Override
        public Node getRight(final Node node) {
            return this.ltr.get(node);
        }

        @Override
        public Node getLeft(final Node node) {
            return this.rtl.get(node);
        }

        @Override
        public Set<Insertion> getInserted() {
            return this.inserted;
        }

        @Override
        public Map<Node, Node> getReplaced() {
            return this.replaced;
        }

        @Override
        public Set<Node> getDeleted() {
            return this.deleted;
        }

        /**
         * Converts a collection (map) of extended nodes to a collection of their prototypes.
         * @param original Original collection
         * @return Resulting collection
         */
        private static Map<Node, Node> convert(final Map<ExtNode, ExtNode> original) {
            final Map<Node, Node> result = new HashMap<>();
            for (final Map.Entry<ExtNode, ExtNode> entry : original.entrySet()) {
                Node value = null;
                if (entry.getValue() != null) {
                    value = entry.getValue().getPrototype();
                }
                result.put(entry.getKey().getPrototype(), value);
            }
            return Collections.unmodifiableMap(result);
        }
    }

    /**
     * Number of unprocessed child nodes.
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
            return this.left == 0;
        }

        /**
         * Analyzes a case where the only actions that are allowed are deletions.
         * @return Checking result, {@code true} if we can only delete nodes
         */
        boolean onlyActionIsToDeleteNodes() {
            return this.right == 0;
        }

        /**
         * Notes that some child node of the right node has been recognized as an inserted node.
         */
        void nodeWasInserted() {
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
            this.left = this.left - 1;
            this.delete = this.delete - 1;
        }

        /**
         * Marks that some child of the left node has been mapped or replaced by a child
         * of the right node.
         */
        void removeOnePair() {
            this.left = this.left - 1;
            this.right = this.right - 1;
        }
    }
}
