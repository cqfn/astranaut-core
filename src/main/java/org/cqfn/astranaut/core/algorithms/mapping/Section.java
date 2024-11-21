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
     * Сonstructor that constructs a section from a complete set of child nodes
     *  (when no node has been processed yet).
     * @param left First (left) node
     * @param right Second (right) node whose child nodes will be mapped with the child
     *  nodes of the first node.
     */
    Section(final ExtNode left, final ExtNode right) {
        this(Section.createChildrenList(left), Section.createChildrenList(right));
    }

    /**
     * Returns the subset of the child nodes of the first (left) node
     * @return Ordered list on nodes
     */
    List<ExtNode> getLeft() {
        return this.left;
    }

    /**
     * Returns the subset of the child nodes of the second (right) node
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
