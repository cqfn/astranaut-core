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

/**
 * Auxiliary structure for mapping child nodes. Contains a subset of child nodes of one node
 *  and a corresponding but unmapped subset of child nodes of another node.
 * @since 2.0.0
 */
class Section {
    /**
     * Subset of the child nodes of the first (left) node.
     */
    private final List<ExtNode> left;

    /**
     * Subset of the child nodes of the second (right) node.
     */
    private final List<ExtNode> right;

    /**
     * Constructor.
     * @param left Subset of the child nodes of the first (left) node
     * @param right Subset of the child nodes of the second (right) node.
     */
    Section(final List<ExtNode> left, final List<ExtNode> right) {
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
        this(Section.createChildrenList(left), Section.createChildrenList(right));
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
     * Checks if the section contains a node.
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
     * @return A new section instance without the specified node, or null if the result is empty
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
                result = new Section(Collections.emptyList(), this.right);
                break;
            }
            final List<ExtNode> list = new ArrayList<>(size - 1);
            for (int elem = 0; elem < size; elem = elem + 1) {
                if (elem != index) {
                    list.add(this.left.get(elem));
                }
            }
            result = new Section(Collections.unmodifiableList(list), this.right);
        } while (false);
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
                result = new Section(this.left, Collections.emptyList());
                break;
            }
            final List<ExtNode> list = new ArrayList<>(size - 1);
            for (int elem = 0; elem < size; elem = elem + 1) {
                if (elem != index) {
                    list.add(this.right.get(elem));
                }
            }
            result = new Section(this.left, Collections.unmodifiableList(list));
        } while (false);
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
}
