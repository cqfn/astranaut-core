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
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.Insertion;
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
        final String description = "A(B(C, D))";
        final Node first = DraftNode.createByDescription(description);
        final Node second = DraftNode.createByDescription(description);
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(mapping.getRight(first), second);
        Assertions.assertEquals(mapping.getLeft(second), first);
    }

    @Test
    void testPairOfTreesWhereOnlyInsertion() {
        final Node first = DraftNode.createByDescription("X()");
        final Node second = DraftNode.createByDescription("X(A,B)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<String> inserted = Arrays.asList("A", "B");
        final Set<Insertion> set = mapping.getInserted();
        for (final Insertion insertion : set) {
            final String name = insertion.getNode().getTypeName();
            Assertions.assertTrue(inserted.contains(name));
        }
    }

    @Test
    void testPairOfTreesWhereOneAndOneInserted() {
        final Node first = DraftNode.createByDescription("X(A)");
        final Node second = DraftNode.createByDescription("X(A,B)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Set<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("B", inserted.iterator().next().getNode().getTypeName());
    }

    @Test
    void testPairOfTreesWhereTwoAndOneInserted() {
        final Node first = DraftNode.createByDescription("X(A,C)");
        final Node second = DraftNode.createByDescription("X(A,B,C)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Set<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("B", inserted.iterator().next().getNode().getTypeName());
    }
}
