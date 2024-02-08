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

import java.util.HashMap;
import java.util.Map;
import org.cqfn.astranaut.core.Node;

/**
 * Top-down mapping algorithm.
 * It starts comparing trees starting from the roots and sequentially 'descends down'.
 *
 * @since 1.1.0
 */
public final class TopDownMapper implements Mapper {
    /**
     * Left-to-right mapping.
     */
    private final Map<Node, Node> ltr;

    /**
     * Right-to-left mapping.
     */
    private final Map<Node, Node> rtl;

    /**
     * Constructor.
     */
    public TopDownMapper() {
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
    }

    @Override
    public Mapping map(final Node left, final Node right) {
        this.compareTwoNodes(left, right);
        return new Mapping() {
            @Override
            public Node getRight(final Node left) {
                return TopDownMapper.this.ltr.get(left);
            }

            @Override
            public Node getLeft(final Node right) {
                return TopDownMapper.this.rtl.get(right);
            }
        };
    }

    /**
     * Compares two nodes and updates the mapping structures if the nodes are matched.
     * @param left Left node
     * @param right Right node
     */
    private void compareTwoNodes(final Node left, final Node right) {
        if (left.getTypeName().equals(right.getTypeName())
            && left.getData().equals(right.getData())) {
            this.ltr.put(left, right);
            this.rtl.put(right, left);
        }
    }
}
