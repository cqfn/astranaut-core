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
package org.cqfn.astranaut.core.algorithms.hash;

import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;

/**
 * An interface that calculates the hash of a node.
 *  Hash is a number that uniquely identifies a node by some criteria.
 * @since 1.1.0
 */
public interface Hash {
    /**
     * Calculates the hash of the node.
     * This method computes the hash value for the given node, which can be used
     *  to uniquely identify the node based on its content and structure.
     * @param node Node to calculate the hash for.
     * @return Hash value of the node
     */
    int calculate(Node node);

    /**
     * Calculates the hash of the tree.
     * This default method computes the hash value for the entire tree by
     *  calculating the hash of its root node.
     * @param tree Tree to calculate the hash for.
     * @return Hash value of the tree, derived from its root node.
     */
    default int calculate(Tree tree) {
        return this.calculate(tree.getRoot());
    }
}
