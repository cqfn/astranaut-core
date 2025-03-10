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
package org.cqfn.astranaut.core.algorithms.conversion;

import java.util.List;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Extracted} class.
 * @since 2.0.0
 */
class ExtractedTest {
    @Test
    void testBaseInterface() {
        final Extracted extracted = new Extracted();
        Assertions.assertTrue(extracted.getNodes(0).isEmpty());
        extracted.addNode(0, DraftNode.create("A"));
        extracted.addNode(0, DraftNode.create("B"));
        extracted.addNode(1, DraftNode.create("C"));
        extracted.addNode(2, DraftNode.create("D"));
        Assertions.assertTrue(extracted.getNodes().isEmpty());
        final List<Node> list = extracted.getNodes(0, 1, 3);
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals("A", list.get(0).getTypeName());
        Assertions.assertEquals("B", list.get(1).getTypeName());
        Assertions.assertEquals("C", list.get(2).getTypeName());
        Assertions.assertTrue(extracted.getData(0).isEmpty());
        extracted.addData(0, "aaa");
        extracted.addData(1, "bbb");
        Assertions.assertEquals("aaa", extracted.getData(0));
        Assertions.assertTrue(extracted.getData(2).isEmpty());
    }
}
