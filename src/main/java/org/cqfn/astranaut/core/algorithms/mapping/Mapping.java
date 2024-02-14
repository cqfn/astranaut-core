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

import java.util.Set;
import org.cqfn.astranaut.core.Node;

/**
 * A mapping from one syntactic tree ('left') to another ('right'), i.e.,
 * set of correspondences between the nodes of one tree and the nodes of another tree.
 *
 * @since 1.1.0
 */
public interface Mapping {
    /**
     * Returns the node of the 'right' tree that is mapped to the node of the 'left' tree.
     * @param left A node of the 'left' tree
     * @return The corresponding node of the 'right' tree or
     *  {@code null} if there is nothing corresponding to the node of the 'left' tree
     */
    Node getRight(Node left);

    /**
     * Returns the node of the 'left' tree that is mapped to the node of the 'right' tree.
     * @param right A node of the 'right' tree
     * @return The corresponding node of the 'left' tree or
     *  {@code null} if there is nothing corresponding to the node of the 'right' tree
     */
    Node getLeft(Node right);

    /**
     * Returns the set of nodes of the 'left' tree that need to be removed
     * to get the 'right' tree.
     * @return The set of deleted nodes
     */
    Set<Node> getDeleted();
}
