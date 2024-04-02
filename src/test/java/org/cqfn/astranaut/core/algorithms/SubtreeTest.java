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

import java.util.Arrays;
import java.util.HashSet;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Subtree}.
 *
 * @since 1.1.4
 */
class SubtreeTest {
    @Test
    void testUsingOneSubtreeInstanceForCreatingTwoSubtrees() {
        final Node original = DraftNode.createByDescription("X(A,B,C,D,E)");
        final Subtree algorithm = new Subtree(original, Subtree.INCLUDE);
        final Node first = algorithm.create(
            new HashSet<>(
                Arrays.asList(
                    original.getChild(0),
                    original.getChild(1)
                )
            )
        );
        Assertions.assertEquals(2,  first.getChildCount());
        final Node second = algorithm.create(
            new HashSet<>(
                Arrays.asList(
                    original.getChild(0),
                    original.getChild(1),
                    original.getChild(2)
                )
            )
        );
        Assertions.assertEquals(2,  first.getChildCount());
        Assertions.assertEquals(3,  second.getChildCount());
    }
}
