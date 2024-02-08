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

import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link AbsoluteHash} class.
 *
 * @since 1.0
 */
class AbsoluteHashTest {
    /**
     * Testing the feature that two matching subtrees will have the same hash.
     */
    @Test
    void testingEqualityFeature() {
        final Hash hash = new AbsoluteHash();
        final Node first = LittleTrees.createTreeWithDeleteAction();
        final Node second = LittleTrees.createTreeWithDeleteAction();
        int expected = hash.calculate(first);
        int actual = hash.calculate(second);
        Assertions.assertEquals(expected, actual);
        expected = hash.calculate(first.getChild(0));
        actual = hash.calculate(second.getChild(0));
        Assertions.assertEquals(expected, actual);
    }
}
