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
 * Test for {@link Unprocessed} class.
 * @since 2.0.0
 */
class UnprocessedTest {
    @Test
    void removeNode() {
        final ExtNodeCreator creator = new ExtNodeCreator();
        final ExtNode left = creator.create(DraftNode.create("A(I)"));
        final ExtNode right = creator.create(DraftNode.create("A(J,K)"));
        final Unprocessed unprocessed = new Unprocessed(left, right);
        Section section = unprocessed.getFirstSection();
        Assertions.assertEquals(3, section.getLeft().size() + section.getRight().size());
        unprocessed.removeNode(left);
        section = unprocessed.getFirstSection();
        Assertions.assertEquals(3, section.getLeft().size() + section.getRight().size());
        unprocessed.removeNode(right.getExtChild(1));
        section = unprocessed.getFirstSection();
        Assertions.assertEquals(2, section.getLeft().size() + section.getRight().size());
        unprocessed.removeNode(right.getExtChild(0));
        section = unprocessed.getFirstSection();
        Assertions.assertEquals(1, section.getLeft().size() + section.getRight().size());
        unprocessed.removeNode(left.getExtChild(0));
        section = unprocessed.getFirstSection();
        Assertions.assertNull(section);
    }

    @Test
    void removeNodes() {
        final ExtNodeCreator creator = new ExtNodeCreator();
        final ExtNode left = creator.create(DraftNode.create("A(B,C,D)"));
        final ExtNode right = creator.create(DraftNode.create("A(E,F,G)"));
        final Unprocessed unprocessed = new Unprocessed(left, right);
        Assertions.assertEquals(1, unprocessed.getNumberOfSections());
        Section section = unprocessed.getFirstSection();
        Assertions.assertEquals(6, section.getLeft().size() + section.getRight().size());
        unprocessed.removeNodes(left, right);
        Assertions.assertEquals(1, unprocessed.getNumberOfSections());
        section = unprocessed.getFirstSection();
        Assertions.assertEquals(6, section.getLeft().size() + section.getRight().size());
        unprocessed.removeNodes(left.getExtChild(1), right.getExtChild(1));
        Assertions.assertEquals(2, unprocessed.getNumberOfSections());
        section = unprocessed.getFirstSection();
        Assertions.assertEquals(2, section.getLeft().size() + section.getRight().size());
        unprocessed.removeNodes(left.getExtChild(0), right.getExtChild(0));
        Assertions.assertEquals(1, unprocessed.getNumberOfSections());
        unprocessed.removeNodes(left.getExtChild(2), right.getExtChild(2));
        Assertions.assertEquals(0, unprocessed.getNumberOfSections());
    }
}
