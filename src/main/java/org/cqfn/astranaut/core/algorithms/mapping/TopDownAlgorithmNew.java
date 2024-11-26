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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.core.base.ExtNode;

/**
 * Top-down mapping algorithm.
 *  Compares root nodes first and then children in depth.
 * @since 1.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
final class TopDownAlgorithmNew {
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
    private final List<ExtInsertion> inserted;

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
    TopDownAlgorithmNew() {
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
        this.inserted = new ArrayList<>(0);
        this.replaced = new HashMap<>();
        this.deleted = new HashSet<>();
    }

    /**
     * Performs the mapping.
     * @param left Root node of the 'left' tree
     * @param right Root node of the 'right' tree
     */
    void execute(final ExtNode left, final ExtNode right) {
        final boolean result = this.mapSubtrees(left, right);
        if (!result) {
            this.replaced.put(left, right);
            this.skipLeftSubtree(left);
            this.skipRightSubtree(right);
        }
    }

    /**
     * Returns left-to-right mapping.
     * @return Collection containing nodes from the left tree and corresponding nodes
     *  from the right tree
     */
    Map<ExtNode, ExtNode> getLeftToRight() {
        return this.ltr;
    }

    /**
     * Returns right-to-left mapping.
     * @return Collection containing nodes from the right tree and corresponding nodes
     *  from the left tree
     */
    Map<ExtNode, ExtNode> getRightToLeft() {
        return this.rtl;
    }

    /**
     * Returns inserted nodes.
     * @return Ordered list of insertions
     */
    List<ExtInsertion> getInserted() {
        return this.inserted;
    }

    /**
     * Returns replaces nodes.
     * @return Collection containing replaced nodes (key - node before replacement,
     *  value - after replacement)
     */
    Map<ExtNode, ExtNode> getReplaced() {
        return this.replaced;
    }

    /**
     * Returns deleted nodes.
     * @return Set of deleted nodes
     */
    Set<ExtNode> getDeleted() {
        return this.deleted;
    }

    /**
     * Performs a mapping of two subtrees.
     * @param left Root node of the left subtree
     * @param right Root node of the right subtree
     * @return Mapping result {@code true} if mapping was performed, {@code false} if subtrees
     *  can't be mapped
     */
    private boolean mapSubtrees(final ExtNode left, final ExtNode right) {
        final boolean result;
        if (left.getAbsoluteHash() == right.getAbsoluteHash()) {
            this.mapSubtreesWithTheSameAbsoluteHash(left, right);
            result = true;
        } else if (left.getLocalHash() == right.getLocalHash()) {
            this.mapSubtreesWithTheSameLocalHash(left, right);
            result = true;
        } else {
            result = false;
        }
        return result;
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
     * Maps subtrees with the same absolute hash, adding the corresponding nodes to the resulting
     *  collections.
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     */
    private void mapSubtreesWithTheSameAbsoluteHash(final ExtNode left, final ExtNode right) {
        this.ltr.put(left, right);
        this.rtl.put(right, left);
        for (int index = 0; index < left.getChildCount(); index = index + 1) {
            this.mapSubtreesWithTheSameAbsoluteHash(
                left.getExtChild(index),
                right.getExtChild(index)
            );
        }
    }

    /**
     * Maps subtrees with the same local hash, adding the corresponding nodes to the resulting
     *  collections.
     * @param left Left node (root node of the left subtree)
     * @param right Related node to the left node
     */
    private void mapSubtreesWithTheSameLocalHash(final ExtNode left, final ExtNode right) {
        this.ltr.put(left, right);
        this.rtl.put(right, left);
        final Unprocessed unprocessed = new Unprocessed(left, right);
        for (Section section = unprocessed.getFirstSection(); section != null;
            section = unprocessed.getFirstSection()) {
            final int lsize = section.getLeft().size();
            final int rsize = section.getRight().size();
            if (lsize == 0 && rsize > 0) {
                this.insertAllNodes(unprocessed, left, section);
                continue;
            }
            if (lsize > 0 && rsize == 0) {
                this.deleteAllNodes(unprocessed, section);
                continue;
            }
            if (lsize == 1 && rsize == 1) {
                this.processSectionWithOnePair(unprocessed, section);
                continue;
            }
            if (this.mapIdenticalNodes(unprocessed, section)) {
                continue;
            }
            if (this.mapSimilarNodes(unprocessed, section)) {
                continue;
            }
            this.replaceFirstNodes(unprocessed, section);
        }
    }

    /**
     * Marks all child nodes from section as inserted.
     * @param unprocessed All unprocessed nodes
     * @param node Node where the child nodes will be inserted
     * @param section Section containing unprocessed nodes
     */
    private void insertAllNodes(final Unprocessed unprocessed, final ExtNode node,
        final Section section) {
        ExtNode after = section.getPrevious();
        for (final ExtNode child : section.getRight()) {
            this.inserted.add(new ExtInsertion(child, node, after));
            this.skipRightSubtree(child);
            unprocessed.removeNode(child);
            after = child;
        }
    }

    /**
     * Marks all child nodes from section as deleted.
     * @param unprocessed All unprocessed nodes
     * @param section Current section containing unprocessed nodes
     */
    private void deleteAllNodes(final Unprocessed unprocessed, final Section section) {
        for (final ExtNode child : section.getLeft()) {
            this.deleted.add(child);
            this.skipLeftSubtree(child);
            unprocessed.removeNode(child);
        }
    }

    /**
     * Processes a section that contains only one pair of elements, that is, one element
     *  on the left and one on the right. This is a frequent special case, such processing
     *  will allow not to run more complex mapping algorithms.
     * @param unprocessed All unprocessed nodes
     * @param section Current section containing unprocessed nodes
     */
    private void processSectionWithOnePair(final Unprocessed unprocessed, final Section section) {
        final ExtNode left = section.getLeft().get(0);
        final ExtNode right = section.getRight().get(0);
        this.execute(left, right);
        unprocessed.removeNodes(left, right);
    }

    /**
     * Tries to find and map identical nodes.
     * @param unprocessed All unprocessed nodes
     * @param section Current section containing unprocessed nodes
     * @return Mapping result, {@code true} if at least one pair of nodes has been matched
     */
    private boolean mapIdenticalNodes(final Unprocessed unprocessed, final Section section) {
        boolean result = false;
        do {
            if (section.isFlagSet(Section.FLAG_NO_ABSOLUTE)) {
                break;
            }
            final NodePairFinder.Result mapping =
                new NodePairFinder(section, NodePairFinder.ABSOLUTE_HASH).findMatchingSequence();
            final int count = mapping.getCount();
            if (count == 0) {
                section.setFlag(Section.FLAG_NO_ABSOLUTE);
                break;
            }
            result = true;
            for (int index = 0; index < count; index = index + 1) {
                final ExtNode left = section.getLeft().get(mapping.getLeftOffset() + index);
                final ExtNode right = section.getRight().get(mapping.getRightOffset() + index);
                this.mapSubtreesWithTheSameAbsoluteHash(left, right);
                unprocessed.removeNodes(left, right);
            }
        } while (false);
        return result;
    }

    /**
     * Tries to find and map similar (but not identical) nodes that have matching local hashes.
     * @param unprocessed All unprocessed nodes
     * @param section Current section containing unprocessed nodes
     * @return Mapping result, {@code true} if at least one pair of nodes has been matched
     */
    private boolean mapSimilarNodes(final Unprocessed unprocessed, final Section section) {
        boolean result = false;
        final NodePairFinder.Result mapping =
            new NodePairFinder(section, NodePairFinder.LOCAL_HASH).findMatchingSequence();
        final int count = mapping.getCount();
        if (count > 0) {
            result = true;
            for (int index = 0; index < count; index = index + 1) {
                final ExtNode left = section.getLeft().get(mapping.getLeftOffset() + index);
                final ExtNode right = section.getRight().get(mapping.getRightOffset() + index);
                this.mapSubtreesWithTheSameLocalHash(left, right);
                unprocessed.removeNodes(left, right);
            }
        }
        return result;
    }

    /**
     * Marks that the first node from the left subset is replaced by the first node
     *  from the right subset. Accordingly, both subsets are reduced by one node.
     *  This is a “dangerous” operation, as such a replacement may not be optimal, however,
     *  sequential execution of this operation (if there are no other options) will let
     *  the mapping algorithm terminate anyway, as all nodes will be mapped as a result.
     *  This operation is therefore performed last.
     * @param unprocessed All unprocessed nodes
     * @param section Current section containing unprocessed nodes
     */
    private void replaceFirstNodes(final Unprocessed unprocessed, final Section section) {
        final ExtNode left = section.getLeft().get(0);
        final ExtNode right = section.getRight().get(0);
        this.replaced.put(left, right);
        this.skipLeftSubtree(left);
        this.skipRightSubtree(right);
        unprocessed.removeNodes(left, right);
    }
}
