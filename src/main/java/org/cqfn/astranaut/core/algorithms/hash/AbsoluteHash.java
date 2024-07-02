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

import java.util.HashMap;
import java.util.Map;
import org.cqfn.astranaut.core.base.Node;

/**
 * Computes the absolute hash of a node, that is, a hash that is computed from both the data
 * of node itself and the data of all children of node.
 * The main feature: if subtrees match, the absolute hashes of their roots are equal.
 * This feature allows us to quickly compare trees and find a subtree in a tree.
 *
 * @since 1.1.0
 */
public final class AbsoluteHash implements Hash {
    /**
     * A table with the calculated hashes.
     * Since nodes are immutable, a hash calculated once for a node will never change.
     */
    private final Map<Node, Integer> calculated;

    /**
     * Constructor.
     */
    public AbsoluteHash() {
        this.calculated = new HashMap<>();
    }

    @Override
    public int calculate(final Node node) {
        int hash;
        if (this.calculated.containsKey(node)) {
            hash = this.calculated.get(node);
        } else {
            hash = node.getTypeName().hashCode() * 31 + node.getData().hashCode();
            final int count = node.getChildCount();
            for (int index = 0; index < count; index = index + 1) {
                hash = 31 * hash + this.calculate(node.getChild(index));
            }
            this.calculated.put(node, hash);
        }
        return hash;
    }
}
