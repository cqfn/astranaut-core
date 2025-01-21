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
package org.cqfn.astranaut.core.algorithms.mapping;

import org.cqfn.astranaut.core.algorithms.ExtNodeCreator;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.ExtNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link NodePairFinder} class.
 * @since 2.0.0
 */
class NodePairFinderTest {
    @Test
    void matchNothing() {
        final NodePairFinder.Result result = this.match("A(B,C)", "A(D,E,F)");
        Assertions.assertEquals(0, result.getCount());
        Assertions.assertEquals(-1, result.getLeftOffset());
        Assertions.assertEquals(-1, result.getRightOffset());
    }

    @Test
    void matchOneFromSeven() {
        final NodePairFinder.Result result = this.match("A(B,C,D,E,F,G,H)", "A(I,J,F,K)");
        Assertions.assertEquals(1, result.getCount());
        Assertions.assertEquals(4, result.getLeftOffset());
        Assertions.assertEquals(2, result.getRightOffset());
    }

    @Test
    void matchTwoFromFour() {
        final NodePairFinder.Result result = this.match("A(B,B,C,B)", "A(D,B,B,B)");
        Assertions.assertEquals(2, result.getCount());
        Assertions.assertEquals(0, result.getLeftOffset());
        Assertions.assertEquals(1, result.getRightOffset());
    }

    @Test
    void matchThreeFromNine() {
        final NodePairFinder.Result result = this.match("A(C,C,B,B,B,C,C)", "A(B,B,B,B,B,B,B,B,B)");
        Assertions.assertEquals(3, result.getCount());
        Assertions.assertEquals(2, result.getLeftOffset());
        Assertions.assertEquals(2, result.getRightOffset());
    }

    @Test
    void matchShorterButBetter() {
        final NodePairFinder.Result result = this.match(
            "A(D,F,F,D,E,E,E,D,C,C,C,C)",
            "A(B,B,B,B,B,B,B,F,F,B,B,B,B,B,E,E,E,B,B,B,B,B,C,C,C,C)"
        );
        Assertions.assertEquals(2, result.getCount());
        Assertions.assertEquals(1, result.getLeftOffset());
        Assertions.assertEquals(7, result.getRightOffset());
    }

    /**
     * Matches a pair of nodes and finds the best sequence of matching child nodes.
     * @param first Description of the first node
     * @param second Description of the second node
     * @return Matching result
     */
    private NodePairFinder.Result match(final String first, final String second) {
        final ExtNodeCreator creator = new ExtNodeCreator();
        final ExtNode left = creator.create(DraftNode.create(first));
        final ExtNode right = creator.create(DraftNode.create(second));
        final Section section = new Section(left, right);
        final NodePairFinder finder = new NodePairFinder(section, NodePairFinder.ABSOLUTE_HASH);
        return finder.findMatchingSequence();
    }
}
