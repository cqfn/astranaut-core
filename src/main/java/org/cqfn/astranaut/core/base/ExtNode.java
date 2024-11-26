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
package org.cqfn.astranaut.core.base;

/**
 * Extended node interface.
 *  This interface provides additional data that can be useful for certain algorithms
 *  and can be computed by traversing the tree using methods from the basic {@link Node} interface.
 *  To keep the basic interface as lightweight as possible, these supplementary methods
 *  have been moved here.
 * @since 2.0.0
 */
public interface ExtNode extends PrototypeBasedNode {
    /**
     * Returns the parent node of this node, which is the node that has this node as one
     *  of its children.
     * @return The parent node, or {@code null} if this node is a root node
     */
    ExtNode getParent();

    /**
     * Returns the prototype of the parent node, that is, a reference to the original
     *  non-extended node that contains the prototype of this node in the list of its child nodes.
     * @return The prototype node of the parent node, or {@code null} if this node is a root node
     */
    default Node getParentPrototype() {
        final Node node;
        final ExtNode parent = this.getParent();
        if (parent == null) {
            node = null;
        } else {
            node = parent.getPrototype();
        }
        return node;
    }

    /**
     * Returns the index (sequence number) of this node in the list of children
     *  of this node's parent.
     * @return Index of this node
     */
    int getIndex();

    /**
     * Returns the left neighbor node of this node, which is the node whose index in the parent
     *  node is one less than the index of this node.
     * @return The left neighbor node, or {@code null} if this node is first (has no left neighbor)
     */
    ExtNode getLeft();

    /**
     * Returns the prototype of the left node, that is, the original non-extended node
     *  that is to the left of the prototype of this node.
     * @return The original left neighbor node, or {@code null} if this node is first
     *  (has no left neighbor)
     */
    default Node getLeftPrototype() {
        final Node node;
        final ExtNode left = this.getLeft();
        if (left == null) {
            node = null;
        } else {
            node = left.getPrototype();
        }
        return node;
    }

    /**
     * Returns the right neighbor node of this node, which is the node whose index in the parent
     *  node is one greater than the index of this node.
     * @return The right neighbor node, or {@code null} if this node is last (has no right neighbor)
     */
    ExtNode getRight();

    /**
     * Returns the prototype of the right node, that is, the original non-extended node
     *  that is to the right of the prototype of this node.
     * @return The original right neighbor node, or {@code null} if this node is last
     *  (has no right neighbor)
     */
    default Node getRightPrototype() {
        final Node node;
        final ExtNode right = this.getRight();
        if (right == null) {
            node = null;
        } else {
            node = right.getPrototype();
        }
        return node;
    }

    /**
     * Returns a child node at its index as an extended node.
     * @param index Node index
     * @return Child node wrapped in the interface of the extended node.
     */
    ExtNode getExtChild(int index);

    /**
     * Computes and returns the absolute hash of this node.
     *  The absolute hash is calculated based on the data of this node as well as all its
     *  descendant nodes. If two nodes have identical absolute hashes, their entire subtrees
     *  are considered structurally and data-wise equivalent.
     * @return The absolute hash of the node and its subtree
     */
    int getAbsoluteHash();
}
