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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TopDownMapper} class.
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
        Assertions.assertEquals(0, mapping.getNumberOfActions());
    }

    @Test
    void testPairOfTreesWhereOnlyInsertion() {
        final Node first = DraftNode.create("X()");
        final Node second = DraftNode.create("X(A,B)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(2, mapping.getNumberOfActions());
        final Iterator<Insertion> insertions = mapping.getInserted().iterator();
        Insertion insertion = insertions.next();
        Assertions.assertEquals("A", insertion.getNode().getTypeName());
        Assertions.assertNull(insertion.getAfter());
        Assertions.assertSame(first, insertion.getInto());
        insertion = insertions.next();
        Assertions.assertEquals("B", insertion.getNode().getTypeName());
        Assertions.assertEquals("A", insertion.getAfter().getTypeName());
        Assertions.assertFalse(insertions.hasNext());
    }

    @Test
    void testPairOfTreesWhereOnlyDeletion() {
        final Node first = DraftNode.create("X(A,B)");
        final Node second = DraftNode.create("X()");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(2, mapping.getNumberOfActions());
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
        Assertions.assertEquals(1, mapping.getNumberOfActions());
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        final Insertion insertion = inserted.get(0);
        Assertions.assertEquals("B", insertion.getNode().getTypeName());
        Assertions.assertEquals("A", insertion.getAfter().getTypeName());
        Assertions.assertSame(first, insertion.getInto());
    }

    @Test
    void testPairOfTreesWhereTwoAndOneInserted() {
        final Node first = DraftNode.create("X(A,C)");
        final Node second = DraftNode.create("X(A,B,C)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(1, mapping.getNumberOfActions());
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        final Insertion insertion = inserted.get(0);
        Assertions.assertEquals("B", insertion.getNode().getTypeName());
        Assertions.assertEquals("A", insertion.getAfter().getTypeName());
        Assertions.assertSame(first, insertion.getInto());
    }

    @Test
    void testNodeInsertedAmongIdenticalNodes() {
        final Node first = DraftNode.create("X(A,A,A,A,C)");
        final Node second = DraftNode.create("X(A,A,A,B,A,C)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(1, mapping.getNumberOfActions());
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertSame(second.getChild(3), inserted.get(0).getNode());
        Assertions.assertSame(first.getChild(2), inserted.get(0).getAfter());
        Assertions.assertSame(first, inserted.get(0).getInto());
        Assertions.assertEquals(second.getChild(0), mapping.getRight(first.getChild(0)));
        Assertions.assertEquals(second.getChild(1), mapping.getRight(first.getChild(1)));
        Assertions.assertEquals(second.getChild(2), mapping.getRight(first.getChild(2)));
        Assertions.assertEquals(second.getChild(4), mapping.getRight(first.getChild(3)));
        Assertions.assertEquals(second.getChild(5), mapping.getRight(first.getChild(4)));
    }

    @Test
    void testPairOfTreesWhereTwoAndOneDeleted() {
        final Node first = DraftNode.create("X(A,B)");
        final Node second = DraftNode.create("X(A)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(1, mapping.getNumberOfActions());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals("B", deleted.iterator().next().getTypeName());
    }

    @Test
    void testPairOfTreesWhereThreeAndOneReplaced() {
        final Node first = DraftNode.create("X(A,B,C)");
        final Node second = DraftNode.create("X(A,D,C)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(1, mapping.getNumberOfActions());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> pair = replaced.entrySet().iterator().next();
        Assertions.assertEquals("B", pair.getKey().getTypeName());
        Assertions.assertEquals("D", pair.getValue().getTypeName());
    }

    @Test
    void testPairOfTreesWhereAllReplaced() {
        final Node first = DraftNode.create("X(A,B,C)");
        final Node second = DraftNode.create("X(D,E,F)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(3, mapping.getNumberOfActions());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(3, replaced.size());
        Assertions.assertSame(second.getChild(0), replaced.get(first.getChild(0)));
        Assertions.assertSame(second.getChild(1), replaced.get(first.getChild(1)));
        Assertions.assertSame(second.getChild(2), replaced.get(first.getChild(2)));
    }

    @Test
    void testThreeInsertedAndThreeDeleted() {
        final Node first = DraftNode.create("X(Y,Y,B,B,B,Y)");
        final Node second = DraftNode.create("X(Y,A,A,A,Y,Y)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(6, mapping.getNumberOfActions());
        for (final Insertion insertion : mapping.getInserted()) {
            Assertions.assertEquals("A", insertion.getNode().getTypeName());
        }
        for (final Node deletion : mapping.getDeleted()) {
            Assertions.assertEquals("B", deletion.getTypeName());
        }
    }

    @Test
    void testPairOfTreesWhereOneAddedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(C,D,E))");
        final Node second = DraftNode.create("X(A,Y(B,C,F,E))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(2, mapping.getNumberOfActions());
        final List<Insertion> added = mapping.getInserted();
        Assertions.assertEquals(1, added.size());
        Assertions.assertEquals("B", added.get(0).getNode().getTypeName());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Map.Entry<Node, Node> pair = replaced.entrySet().iterator().next();
        Assertions.assertEquals("D", pair.getKey().getTypeName());
        Assertions.assertEquals("F", pair.getValue().getTypeName());
    }

    @Test
    void testPairOfTreesWhereOneDeletedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(B,C,D,E))");
        final Node second = DraftNode.create("X(A,Y(C,F,E))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(2, mapping.getNumberOfActions());
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
    void testPairOfTreesWhereTwoAddedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(B,C,D))");
        final Node second = DraftNode.create("X(A,Y(B,E,F,D,F))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(3, mapping.getNumberOfActions());
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
    void testPairOfTreesWhereTwoRemovedAndOneReplaced() {
        final Node first = DraftNode.create("X(A,Y(B,E,F,D,F))");
        final Node second = DraftNode.create("X(A,Y(B,C,D))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(3, mapping.getNumberOfActions());
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
    void testPairOfTreesWhereAllActions() {
        final Node first = DraftNode.create("X(A,B,Y(C,D,E,F,J,K))");
        final Node second = DraftNode.create("X(A,G,Y(H,C,I,E,J,K))");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(4, mapping.getNumberOfActions());
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
    void testAddOneAndReplaceOneInDepth() {
        final Node first = DraftNode.create(
            "A(B1,B2,B3,B4(C1,C2(            X(R),D1,D2(E1,E2<'aaa'>,E3)),C3),B5)"
        );
        final Node second = DraftNode.create(
            "A(B1,B2,B3,B4(C1,C2(X(Q1,Q2,Q3),X(R),D1,D2(E1,E2<'bbb'>,E3)),C3),B5)"
        );
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertEquals("X(Q1, Q2, Q3)", inserted.get(0).getNode().toString());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(0, deleted.size());
    }

    @Test
    void testsTwoCompletelyDifferentNodes() {
        final Node first = DraftNode.create("A(B,C)");
        final Node second = DraftNode.create("D(E,F)");
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
    }

    @Test
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
    void testSubtreeInsertion() {
        final Node first = DraftNode.create(
            "A(                    X(Y<'2'>),          X(Y<'4'>))"
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

    @Test
    void firstTestComplexChanges() {
        final Node first = DraftNode.create(
            "A(     X(Y<'1'>,Z,A),X(A,D,C  ))"
        );
        final Node second = DraftNode.create(
            "A(X(Q),X(Y<'2'>,Z,B),X(A,  C,E))"
        );
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(2, inserted.size());
        Assertions.assertEquals("E", inserted.get(0).getNode().toString());
        Assertions.assertEquals("X(Q)", inserted.get(1).getNode().toString());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals("D", deleted.iterator().next().toString());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(2, replaced.size());
    }

    @Test
    void secondTestComplexChanges() {
        final Node first = DraftNode.create(
            "A(В,   В,   B,   B,     X(C,D))"
        );
        final Node second = DraftNode.create(
            "A(X(I),X(H),X(G),X(C,E),X(F)  )"
        );
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertSame(second.getChild(3), mapping.getRight(first.getChild(4)));
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        Assertions.assertSame(second.getChild(4), inserted.get(0).getNode());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(4, replaced.size());
    }

    @Test
    void thirdTestComplexChanges() {
        final Node first = DraftNode.create(
            "A(X(B,C),Y(B,C))"
        );
        final Node second = DraftNode.create(
            "A(X(C,D),Y(B<'1'>,C))"
        );
        final Mapper mapper = TopDownMapper.INSTANCE;
        final Mapping mapping = mapper.map(first, second);
        final List<Insertion> inserted = mapping.getInserted();
        Assertions.assertEquals(1, inserted.size());
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        final Map<Node, Node> replaced = mapping.getReplaced();
        Assertions.assertEquals(1, replaced.size());
    }
}
