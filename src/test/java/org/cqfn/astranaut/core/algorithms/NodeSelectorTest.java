/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.Set;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link NodeSelector}.
 * @since 1.1.4
 */
class NodeSelectorTest {
    @Test
    void test() {
        final Node tree = DraftNode.create("A(B(C(D(E(F)))))");
        final NodeSelector selector = new NodeSelector(tree);
        final StringBuilder builder = new StringBuilder();
        final Set<Node> set = selector.select(
            (node, parents) -> {
                boolean selected = false;
                if (node.getTypeName().equals("F")) {
                    selected = true;
                    for (final Node parent : parents) {
                        builder.append(parent.getTypeName());
                    }
                }
                return selected;
            }
        );
        Assertions.assertEquals(1, set.size());
        Assertions.assertEquals("F", set.iterator().next().getTypeName());
        Assertions.assertEquals("EDCBA", builder.toString());
    }
}
