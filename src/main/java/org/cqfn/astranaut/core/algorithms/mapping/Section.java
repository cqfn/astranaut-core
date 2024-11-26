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
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.utils.Pair;

/**
 * Auxiliary structure for mapping child nodes. Contains a subset of child nodes of one node
 *  and a corresponding but unmapped subset of child nodes of another node.
 * @since 2.0.0
 */
final class Section {
    /**
     * Index of the flag that indicates that there are no absolute matches in the section.
     */
    static final int FLAG_NO_IDENTICAL = 0;

    /**
     * Index of the flag that indicates that there are no local matches in the section.
     */
    static final int FLAG_NO_SIMILAR = 1;

    /**
     * Node on the left of the first element of the subset of child nodes
     *  of the first (left) node.
     */
    private final ExtNode previous;

    /**
     * Subset of the child nodes of the first (left) node.
     */
    private final List<ExtNode> left;

    /**
     * Subset of the child nodes of the second (right) node.
     */
    private final List<ExtNode> right;

    /**
     * Various flags that help optimize the algorithm.
     */
    private int flags;

    /**
     * Constructor.
     * @param previous Node before the first element of the first (left) subset
     * @param left Subset of the child nodes of the first (left) node
     * @param right Subset of the child nodes of the second (right) node.
     */
    Section(final ExtNode previous, final List<ExtNode> left, final List<ExtNode> right) {
        this.previous = previous;
        this.left = left;
        this.right = right;
    }

    /**
     * Constructor that constructs a section from a complete set of child nodes
     *  (when no node has been processed yet).
     * @param left First (left) node
     * @param right Second (right) node whose child nodes will be mapped with the child
     *  nodes of the first node.
     */
    Section(final ExtNode left, final ExtNode right) {
        this(null, Section.createChildrenList(left), Section.createChildrenList(right));
    }

    /**
     * Returns node before the first element of the first (left) subset.
     * @return A node or {@code null} if no node before left subset
     */
    ExtNode getPrevious() {
        return this.previous;
    }

    /**
     * Returns the subset of the child nodes of the first (left) node.
     * @return Ordered list on nodes
     */
    List<ExtNode> getLeft() {
        return this.left;
    }

    /**
     * Returns the subset of the child nodes of the second (right) node.
     * @return Ordered list on nodes
     */
    List<ExtNode> getRight() {
        return this.right;
    }

    /**
     * Checks if the section contains specific node.
     * @param node Node
     * @return Checking result
     */
    boolean hasNode(final ExtNode node) {
        return this.left.contains(node) || this.right.contains(node);
    }

    /**
     * Creates a new section with the specified node removed from both left and right subsets.
     * If the node is not present in either subset, the original section is returned.
     * If this removal results in both subsets becoming empty, returns null.
     * @param node Node to be removed
     * @return A new section instance without the specified node, or {@code null} if the result
     *  is empty
     */
    Section removeNode(final ExtNode node) {
        final Section result;
        do {
            int index = this.left.indexOf(node);
            if (index >= 0) {
                result = this.removeLeftNode(index);
                break;
            }
            index = this.right.indexOf(node);
            if (index >= 0) {
                result = this.removeRightNode(index);
                break;
            }
            result = this;
        } while (false);
        return result;
    }

    /**
     * Divides the section into two, each of which does not contain the specified nodes.
     *  The first section contains subsets of nodes that are to the left of the nodes
     *  to be deleted. If there are no nodes to the left, the section will be null.
     *  The second section, accordingly, contains subsets of nodes that are to the right
     *  of the nodes to be deleted. If there are no nodes to the right, the section will be null.
     * The point of this action is that when two nodes are mapped, they are removed from the list
     *  of unprocessed nodes, while the remaining nodes are divided into two parts that must
     *  be considered separately, since it is “physically” impossible to map a node from the
     *  first part to a node from the second part. Thus the mapping task is divided into two
     *  smaller similar tasks. By successive division of unmapped sections, the mapping
     *  of the entire initial list of nodes is achieved.
     * @param node The node to be deleted from the left subset
     * @param corresponding The node to be deleted from the right subset,
     *  corresponding to the left node
     * @return Set of new sections, containing two, one or zero sections.
     */
    Pair<Section, Section> removeNodes(final ExtNode node, final ExtNode corresponding) {
        final Pair<List<ExtNode>, List<ExtNode>> xleft = Section.splitSubset(this.left, node);
        final Pair<List<ExtNode>, List<ExtNode>> xright =
            Section.splitSubset(this.right, corresponding);
        final Section first;
        if (xleft.getKey().isEmpty() && xright.getKey().isEmpty()) {
            first = null;
        } else {
            first = new Section(this.previous, xleft.getKey(), xright.getKey());
            first.flags = this.flags;
        }
        final Section second;
        if (xleft.getValue().isEmpty() && xright.getValue().isEmpty()) {
            second = null;
        } else {
            second = new Section(node, xleft.getValue(), xright.getValue());
            second.flags = this.flags;
        }
        return new Pair<>(first, second);
    }

    /**
     * Returns whether the flag is set.
     * @param index Index of the flag
     * @return Value of the flag
     */
    boolean isFlagSet(final int index) {
        return (this.flags & (1 << index)) > 0;
    }

    /**
     * Sets the flag.
     * @param index Index of the flag
     */
    void setFlag(final int index) {
        this.flags = this.flags | (1 << index);
    }

    /**
     * Creates a new section with the specified node removed from the left subset.
     * @param index Node index
     * @return A new section instance without the specified node, or null if the result is empty
     */
    private Section removeLeftNode(final int index) {
        final Section result;
        do {
            final int size = this.left.size();
            if (size == 1 && this.right.isEmpty()) {
                result = null;
                break;
            }
            if (size == 1) {
                result = new Section(this.left.get(0), Collections.emptyList(), this.right);
                break;
            }
            final List<ExtNode> list = new ArrayList<>(size - 1);
            for (int elem = 0; elem < size; elem = elem + 1) {
                if (elem != index) {
                    list.add(this.left.get(elem));
                }
            }
            final ExtNode before;
            if (index > 0) {
                before = this.previous;
            } else {
                before = this.left.get(0);
            }
            result = new Section(before, Collections.unmodifiableList(list), this.right);
        } while (false);
        if (result != null) {
            result.flags = this.flags;
        }
        return result;
    }

    /**
     * Creates a new section with the specified node removed from the right subset.
     * @param index Node index
     * @return A new section instance without the specified node, or null if the result is empty
     */
    private Section removeRightNode(final int index) {
        final Section result;
        do {
            final int size = this.right.size();
            if (size == 1 && this.left.isEmpty()) {
                result = null;
                break;
            }
            if (size == 1) {
                result = new Section(this.previous, this.left, Collections.emptyList());
                break;
            }
            final List<ExtNode> list = new ArrayList<>(size - 1);
            for (int elem = 0; elem < size; elem = elem + 1) {
                if (elem != index) {
                    list.add(this.right.get(elem));
                }
            }
            result = new Section(this.previous, this.left, Collections.unmodifiableList(list));
        } while (false);
        if (result != null) {
            result.flags = this.flags;
        }
        return result;
    }

    /**
     * Creates a list of the child nodes of the node.
     * @param node Node
     * @return Unmodifiable list of child nodes
     */
    private static List<ExtNode> createChildrenList(final ExtNode node) {
        final int count = node.getChildCount();
        final List<ExtNode> list = new ArrayList<>(count);
        for (int index = 0; index < count; index = index + 1) {
            list.add(node.getExtChild(index));
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Splits a list of nodes into two using the specified node as a delimiter.
     * The delimiter is not included in either set.
     * @param list List of nodes
     * @param node Delimiter node
     * @return Pair of resulting lists
     */
    private static Pair<List<ExtNode>, List<ExtNode>> splitSubset(final List<ExtNode> list,
        final ExtNode node) {
        final Pair<List<ExtNode>, List<ExtNode>> result;
        final int size = list.size();
        final int index = list.indexOf(node);
        if (index < 0) {
            result = new Pair<>(Collections.emptyList(), list);
        } else if (index == 0 && size == 1) {
            result = new Pair<>(Collections.emptyList(), Collections.emptyList());
        } else if (index == 0) {
            result = new Pair<>(Collections.emptyList(), list.subList(1, size));
        } else if (index == size - 1) {
            result = new Pair<>(list.subList(0, index), Collections.emptyList());
        } else {
            result = new Pair<>(list.subList(0, index), list.subList(index + 1, size));
        }
        return result;
    }
}
