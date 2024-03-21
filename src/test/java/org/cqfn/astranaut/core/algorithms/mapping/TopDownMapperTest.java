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

import java.util.Arrays;
import java.util.Collections;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TopDownMapper} class.
 *
 * @since 1.0
 */
class TopDownMapperTest {
    @Test
    void testIdenticalTrees() {
        final Node first = TopDownMapperTest.createTreeAlpha();
        final Node second = TopDownMapperTest.createTreeAlpha();
        final Mapper mapper = new TopDownMapper();
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(mapping.getRight(first), second);
        Assertions.assertEquals(mapping.getLeft(second), first);
    }

    /**
     * Creates tree A(B(C, D)).
     * @return Root node
     */
    private static Node createTreeAlpha() {
        final DraftNode.Constructor ccc = new DraftNode.Constructor();
        ccc.setName("C");
        final DraftNode.Constructor ddd = new DraftNode.Constructor();
        ddd.setName("D");
        final DraftNode.Constructor bbb = new DraftNode.Constructor();
        bbb.setName("B");
        bbb.setChildrenList(Arrays.asList(ccc.createNode(), ddd.createNode()));
        final DraftNode.Constructor aaa = new DraftNode.Constructor();
        aaa.setName("A");
        aaa.setChildrenList(Collections.singletonList(bbb.createNode()));
        return aaa.createNode();
    }
}
