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
package org.cqfn.astranaut.core.algorithms;

import java.util.HashMap;
import java.util.Map;
import org.cqfn.astranaut.core.base.Node;

/**
 * Algorithm for measuring the depth of syntax trees.
 * @since 1.1.0
 */
public final class DepthCalculator {
    /**
     * A table with the calculated values.
     * Since nodes are immutable, depth calculated once for a node will never change.
     */
    private final Map<Node, Integer> calculated;

    /**
     * Constructor.
     */
    public DepthCalculator() {
        this.calculated = new HashMap<>();
    }

    /**
     * Counts the depth of the subtree. For a node that has no children, the depth is 1.
     * @param node The root node of the subtree
     * @return Calculated depth
     */
    public int calculate(final Node node) {
        final int depth;
        if (this.calculated.containsKey(node)) {
            depth = this.calculated.get(node);
        } else {
            int max = 0;
            final int count = node.getChildCount();
            for (int index = 0; index < count; index = index + 1) {
                final int value = this.calculate(node.getChild(index));
                if (max < value) {
                    max = value;
                }
            }
            depth = 1 + max;
            this.calculated.put(node, depth);
        }
        return depth;
    }
}
