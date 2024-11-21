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
import java.util.List;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.utils.Pair;

/**
 * Auxiliary algorithm for top-down mapping.
 * Finds the most matching pairs of nodes. Only nodes with the same hash are pairs.
 *  The best pair is two nodes with minimal index difference.
 * @since 2.0.0
 */
class NodePairFinder {
    /**
     * A list of nodes and matching paired (with the same absolute hash) nodes
     *  with index differences.
     */
    private final List<Pair<ExtNode, List<Pair<ExtNode, Integer>>>> absolute;

    /**
     * A list of nodes and matching paired (with the same local hash) nodes
     *  with index differences.
     */
    private final List<Pair<ExtNode, List<Pair<ExtNode, Integer>>>> local;

    /**
     * Constructor.
     */
    NodePairFinder() {
        this.absolute = new ArrayList<>(0);
        this.local = new ArrayList<>(0);
    }

    /**
     * Fills structures by finding all pairs and calculating index differences.
     * @param left Root node of the left subtree
     * @param right Root node of the right subtree
     */
    void fill(final ExtNode left, final ExtNode right) {
        for (int first = 0; first < left.getChildCount(); first = first + 1) {
            final List<Pair<ExtNode, Integer>> abspairs = new ArrayList<>(0);
            final List<Pair<ExtNode, Integer>> localpairs = new ArrayList<>(0);
            final ExtNode child = left.getExtChild(first);
            this.absolute.add(new Pair<>(child, abspairs));
            this.local.add(new Pair<>(child, localpairs));
            for (int second = 0; second < right.getChildCount(); second = second + 1) {
                final ExtNode applicant = right.getExtChild(second);
                if (child.getAbsoluteHash() == applicant.getAbsoluteHash()) {
                    abspairs.add(new Pair<>(applicant, Math.abs(second - first)));
                } else if (child.getLocalHash() == applicant.getLocalHash()) {
                    localpairs.add(new Pair<>(applicant, Math.abs(second - first)));
                }
            }
        }
    }

    /**
     * Returns a pair of nodes such that their absolute hash matches
     *  and the difference in indices is minimal.
     * @return A pair of nodes or {@code null} if there is no such pair
     */
    Pair<ExtNode, ExtNode> getBestPairOfIdenticalNodes() {
        Pair<ExtNode, ExtNode> result = null;
        int min = 0;
        for (final Pair<ExtNode, List<Pair<ExtNode, Integer>>> entry : this.absolute) {
            final ExtNode left = entry.getKey();
            for (final Pair<ExtNode, Integer> pair : entry.getValue()) {
                if (result == null || min > pair.getValue()) {
                    result = new Pair<>(left, pair.getKey());
                    min = pair.getValue();
                }
            }
        }
        return result;
    }

    /**
     * Returns a pair of nodes such that their absolute hashes match and this pair is to the
     *  right of the reference (already mapped) pair.
     * @param reference Reference pair
     * @return A pair of nodes or {@code null} if there is no such pair
     */
    Pair<ExtNode, ExtNode> getRightPairOfIdenticalNodes(final Pair<ExtNode, ExtNode> reference) {
        Pair<ExtNode, ExtNode> result = null;
        for (int index = reference.getKey().getIndex() + 1;
            result == null && index < this.absolute.size(); index = index + 1) {
            final Pair<ExtNode, List<Pair<ExtNode, Integer>>> entry = this.absolute.get(index);
            final ExtNode left = entry.getKey();
            for (final Pair<ExtNode, Integer> pair : entry.getValue()) {
                final ExtNode right = pair.getKey();
                if (reference.getValue().getIndex() < right.getIndex()) {
                    result = new Pair<>(left, right);
                    break;
                }
            }
        }
        return result;
    }
}
