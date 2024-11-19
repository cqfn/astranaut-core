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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TopDownMapper} class.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class TopDownMapperTest {
    @Test
    void testIdenticalTrees() {
        final String description = "A(B(C, D))";
        final Node first = DraftNode.create(description);
        final Node second = DraftNode.create(description);
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(mapping.getRight(first), second);
        Assertions.assertEquals(mapping.getLeft(second), first);
    }

    @Test
    void testPairOfTreesWhereOnlyInsertion() {
        final Node first = DraftNode.create("X()");
        final Node second = DraftNode.create("X(A,B)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<String> inserted = Arrays.asList("A", "B");
        final List<Insertion> list = mapping.getInserted();
        for (final Insertion insertion : list) {
            final String name = insertion.getNode().getTypeName();
            Assertions.assertTrue(inserted.contains(name));
        }
    }

    @Test
    void testPairOfTreesWhereOnlyDeletion() {
        final Node first = DraftNode.create("X(A,B)");
        final Node second = DraftNode.create("X()");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<String> deleted = Arrays.asList("A", "B");
        final Set<Node> set = mapping.getDeleted();
        for (final Node node : set) {
            final String name = node.getTypeName();
            Assertions.assertTrue(deleted.contains(name));
        }
    }

    @Test
    void testPairOfTreesWhereOneAndOneInserted() {
        final Node first = DraftNode.create("X(A)");
        final Node second = DraftNode.create("X(A,B)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("B", inserted.iterator().next().getNode().getTypeName());
    }

    @Test
    void testPairOfTreesWhereTwoAndOneInserted() {
        final Node first = DraftNode.create("X(A,C)");
        final Node second = DraftNode.create("X(A,B,C)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("B", inserted.iterator().next().getNode().getTypeName());
    }

    @Test
    void testNodeInsertedAmongIdenticalNodes() {
        final Node first = DraftNode.create("X(A,A,A,A,C)");
        final Node second = DraftNode.create("X(A,A,A,B,A,C)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("B", inserted.iterator().next().getNode().getTypeName());
        Assertions.assertEquals(second.getChild(0), mapping.getRight(first.getChild(0)));
        Assertions.assertEquals(second.getChild(1), mapping.getRight(first.getChild(1)));
        Assertions.assertEquals(second.getChild(2), mapping.getRight(first.getChild(2)));
        Assertions.assertEquals(second.getChild(4), mapping.getRight(first.getChild(3)));
    }

    @Test
    @Disabled
    void testPairOfTreesWhereTwoAndOneDeleted() {
        final Node first = DraftNode.create("X(A,B)");
        final Node second = DraftNode.create("X(A)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals("B", deleted.iterator().next().getTypeName());
    }

    @Test
    @Disabled
    void testPairOfTreesWhereThreeAndOneReplaced() {
        final Node first = DraftNode.create("X(A,B,C)");
        final Node second = DraftNode.create("X(A,D,C)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> pair = replaced.entrySet().iterator().next();
        Assertions.assertEquals("B", pair.getKey().getTypeName());
        Assertions.assertEquals("D", pair.getValue().getTypeName());
    }

    @Test
    @Disabled
    void testPairOfTreesWhereOneAddedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(C,D,E))");
        final Node second = DraftNode.create("X(A,Y(B,C,F,E))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> added = mapping.getInserted();
        Assertions.assertEquals(1, added.size());
        Assertions.assertEquals("B", added.iterator().next().getNode().getTypeName());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> pair = replaced.entrySet().iterator().next();
        Assertions.assertEquals("D", pair.getKey().getTypeName());
        Assertions.assertEquals("F", pair.getValue().getTypeName());
    }

    @Test
    @Disabled
    void testPairOfTreesWhereOneDeletedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(B,C,D,E))");
        final Node second = DraftNode.create("X(A,Y(C,F,E))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals("B", deleted.iterator().next().getTypeName());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> pair = replaced.entrySet().iterator().next();
        Assertions.assertEquals("D", pair.getKey().getTypeName());
        Assertions.assertEquals("F", pair.getValue().getTypeName());
    }

    @Test
    @Disabled
    void testPairOfTreesWhereTwoAddedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(B,C,D))");
        final Node second = DraftNode.create("X(A,Y(B,E,F,D,F))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> insertions = mapping.getInserted();
        Assertions.assertEquals(2, insertions.size());
        for (final Insertion insertion : insertions) {
            Assertions.assertEquals("F", insertion.getNode().getTypeName());
        }
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> pair = replaced.entrySet().iterator().next();
        Assertions.assertEquals("C", pair.getKey().getTypeName());
        Assertions.assertEquals("E", pair.getValue().getTypeName());
    }

    @Test
    @Disabled
    void testPairOfTreesWhereTwoRemovedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(B,E,F,D,F))");
        final Node second = DraftNode.create("X(A,Y(B,C,D))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Set<Node> deletions = mapping.getDeleted();
        Assertions.assertEquals(2, deletions.size());
        for (final Node deleted : deletions) {
            Assertions.assertEquals("F", deleted.getTypeName());
        }
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> pair = replaced.entrySet().iterator().next();
        Assertions.assertEquals("E", pair.getKey().getTypeName());
        Assertions.assertEquals("C", pair.getValue().getTypeName());
    }

    @Test
    @Disabled
    void testPairOfTreesWhereAllActions() {
        final Node first = DraftNode.create("X(A,B,Y(C,D,E,F,J,K))");
        final Node second = DraftNode.create("X(A,G,Y(H,C,I,E,J,K))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("H", inserted.iterator().next().getNode().getTypeName());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals("F", deleted.iterator().next().getTypeName());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(2, replaced.size());
        final Map<String, String> expected = new TreeMap<>();
        expected.put("B", "G");
        expected.put("D", "I");
        for (final Map.Entry<Node, Node> pair : replaced.entrySet()) {
            Assertions.assertTrue(expected.containsKey(pair.getKey().getTypeName()));
            Assertions.assertEquals(
                pair.getValue().getTypeName(),
                expected.get(pair.getKey().getTypeName())
            );
        }
    }

    @Test
    @Disabled
    void testAddOneAndReplaceOneInDepth() {
        final Node first = DraftNode.create(
            "A(B1,B2,B3,B4(C1,C2(X(R),D1,D2(E1,E2<'aaa'>,E3)),C3),B5)"
        );
        final Node second = DraftNode.create(
            "A(B1,B2,B3,B4(C1,C2(X(Q1,Q2,Q3),X(R),D1,D2(E1,E2<'bbb'>,E3)),C3),B5)"
        );
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("X", inserted.iterator().next().getNode().getTypeName());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(0, deleted.size());
    }

    @Test
    @Disabled
    void testsTwoCompletelyDifferentNodes() {
        final Node first = DraftNode.create("A(B,C)");
        final Node second = DraftNode.create("D(E,F)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
    }

    @Test
    @Disabled
    void testInsertedFirstWhileSecondHasActions() {
        final Node first = DraftNode.create("X(A(X),D,E)");
        final Node second = DraftNode.create("X(B,A(Y),D,E)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("B", inserted.iterator().next().getNode().getTypeName());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> entry = replaced.entrySet().iterator().next();
        Assertions.assertEquals("X", entry.getKey().getTypeName());
        Assertions.assertEquals("Y", entry.getValue().getTypeName());
    }

    @Test
    @Disabled
    void testDeletedFirstWhileSecondHasActions() {
        final Node first = DraftNode.create("X(B,A(X),D,E)");
        final Node second = DraftNode.create("X(A(Y),D,E)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals("B", deleted.iterator().next().getTypeName());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> entry = replaced.entrySet().iterator().next();
        Assertions.assertEquals("X", entry.getKey().getTypeName());
        Assertions.assertEquals("Y", entry.getValue().getTypeName());
    }

    @Test
    @Disabled
    void testComplexInsertion() {
        final Node first = DraftNode.create(
            "A(X(Y<'2'>),X(Y<'4'>))"
        );
        final Node second = DraftNode.create(
            "A(X(Y<'0'>),X(Y<'1'>),X(Y<'2'>),X(Y<'3'>),X(Y<'4'>))"
        );
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(3, inserted.size());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(0, deleted.size());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(0, replaced.size());
    }
}
