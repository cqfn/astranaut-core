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
package org.cqfn.astranaut.core.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.cqfn.astranaut.core.algorithms.LabeledTreeBuilder;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.example.green.Addition;
import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering base interfaces, i.e. {@link Node}, {@link Type} and {@link Builder}.
 *
 * @since 1.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class BaseInterfaceTest {
    @Test
    void testNotNullFragment() {
        final Node node = DraftNode.create("X");
        final Fragment fragment = node.getFragment();
        Assertions.assertNotNull(fragment);
    }

    @Test
    void testType() {
        final Builder builder = new IntegerLiteral.Constructor();
        builder.setData("0");
        final Node node = builder.createNode();
        final Type type = node.getType();
        Assertions.assertNotNull(type);
        Assertions.assertEquals("IntegerLiteral", type.getName());
        Assertions.assertEquals(node.getTypeName(), type.getName());
        Assertions.assertNotNull(type.getChildTypes());
        Assertions.assertFalse(type.getHierarchy().isEmpty());
        Assertions.assertEquals(type.getHierarchy().get(0), type.getName());
        Assertions.assertNotNull(type.getProperties());
        final String group = "Expression";
        Assertions.assertTrue(type.belongsToGroup(group));
        Assertions.assertTrue(node.belongsToGroup(group));
    }

    @Test
    void testData() {
        final Node first = DraftNode.create("X");
        Assertions.assertNotNull(first.getData());
        final Node second = DraftNode.create("X<\"data\">");
        Assertions.assertEquals("data", second.getData());
    }

    @Test
    void testNotNullProperties() {
        final Node node = DraftNode.create("X");
        Assertions.assertNotNull(node.getProperties());
    }

    @Test
    void testChildren() {
        final Node root = DraftNode.create("X(A,B,C)");
        Assertions.assertEquals(3, root.getChildCount());
        final Node second = root.getChild(1);
        Assertions.assertEquals("B", second.getTypeName());
    }

    @Test
    void testChildrenList() {
        final Node root = DraftNode.create("X(D,E,F)");
        final List<Node> list = root.getChildrenList();
        this.testListModifiersByOne(list);
        this.testListModifiersByCollection(list);
        this.testListIterator(list);
        this.testListIteratorExceptions(list);
        this.testSublist(root, list);
        this.testListOtherMethods(root, list);
    }

    void testListModifiersByOne(final List<Node> list) {
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.add(DraftNode.create("H"))
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.remove(list.get(0))
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.clear()
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.set(1, DraftNode.create("K"))
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.add(1, DraftNode.create("L"))
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.remove(1)
        );
    }

    void testListModifiersByCollection(final List<Node> list) {
        final Collection<Node> collection = Arrays.asList(
            DraftNode.create("I"),
            DraftNode.create("J")
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.addAll(collection)
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.addAll(1, collection)
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.removeAll(collection)
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> list.retainAll(collection)
        );
    }

    void testListIterator(final List<Node> list) {
        final ListIterator<Node> first = list.listIterator();
        final StringBuilder builder = new StringBuilder();
        while (first.hasNext()) {
            builder.append(first.next().getTypeName());
        }
        Assertions.assertThrows(NoSuchElementException.class, first::next);
        Assertions.assertEquals("DEF", builder.toString());
        final ListIterator<Node> second = list.listIterator(1);
        Assertions.assertThrows(UnsupportedOperationException.class, second::remove);
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> second.set(DraftNode.create("M"))
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> second.add(DraftNode.create("N"))
        );
        Assertions.assertEquals(1, second.nextIndex());
        Assertions.assertEquals(0, second.previousIndex());
        Assertions.assertTrue(second.hasPrevious());
        Assertions.assertEquals("D", second.previous().getTypeName());
        Assertions.assertFalse(second.hasPrevious());
        Assertions.assertThrows(NoSuchElementException.class, second::previous);
    }

    void testListIteratorExceptions(final List<Node> list) {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.listIterator(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.listIterator(10));
    }

    void testSublist(final Node root, final List<Node> list) {
        final List<Node> sub = list.subList(1, 3);
        Assertions.assertEquals(2, sub.size());
        Assertions.assertEquals(root.getChild(1), sub.get(0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sub.get(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sub.get(3));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.subList(-1, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.subList(0, 4));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.subList(2, 1));
    }

    void testListOtherMethods(final Node root, final List<Node> list) {
        Assertions.assertEquals(3, list.size());
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertTrue(list.contains(root.getChild(1)));
        final Node alien = DraftNode.create("G");
        Assertions.assertFalse(list.contains(alien));
        Assertions.assertTrue(list.containsAll(list));
        Assertions.assertFalse(list.containsAll(Collections.singletonList(alien)));
        final Iterator<Node> iterator = list.iterator();
        final StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getTypeName());
        }
        Assertions.assertEquals("DEF", builder.toString());
        final Object[] objects = list.toArray();
        Assertions.assertEquals(3, objects.length);
        final Node[] smaller = new Node[2];
        final Node[] same = list.toArray(smaller);
        Assertions.assertSame(same, smaller);
        Assertions.assertSame(root.getChild(0), smaller[0]);
        Assertions.assertSame(root.getChild(1), smaller[1]);
        final Node[] biggest = new Node[4];
        list.toArray(biggest);
        Assertions.assertSame(root.getChild(2), biggest[2]);
        Assertions.assertNull(biggest[3]);
        Assertions.assertEquals(1, list.indexOf(root.getChild(1)));
        Assertions.assertEquals(-1, list.indexOf(alien));
        Assertions.assertEquals(-1, list.indexOf(""));
        Assertions.assertEquals(1, list.lastIndexOf(root.getChild(1)));
        Assertions.assertEquals(-1, list.lastIndexOf(alien));
        Assertions.assertEquals(-1, list.lastIndexOf(""));
    }

    @Test
    void testEmptyChildrenList() {
        final List<Node> list = DraftNode.create("X").getChildrenList();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void testIteratorOverChildren() {
        final Node node = DraftNode.create("X(I,J,K)");
        final Iterator<Node> iterator = node.getIteratorOverChildren();
        final StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getTypeName());
        }
        Assertions.assertEquals("IJK", builder.toString());
    }

    @Test
    void testForEachChild() {
        final Node node = DraftNode.create("X(L,M,N)");
        final StringBuilder builder = new StringBuilder();
        node.forEachChild(child -> builder.append(child.getTypeName()));
        Assertions.assertEquals("LMN", builder.toString());
    }

    @Test
    void testDeepCompare() {
        final String description = "X(Q,W,E,R<\"data\",T(Y))";
        final Node node = DraftNode.create(description);
        Assertions.assertTrue(node.deepCompare(node));
        Assertions.assertTrue(node.deepCompare(DraftNode.create(description)));
        Assertions.assertFalse(node.deepCompare(DraftNode.create("X(Q,V,E,R<\"data\",T(Y))")));
        Assertions.assertFalse(node.deepCompare(DraftNode.create("X(Q,W,E,R<\"test\",T(Y))")));
        Assertions.assertFalse(node.deepCompare(DraftNode.create("X(Q,W,E,R,T(Y))")));
        Assertions.assertFalse(node.deepCompare(DraftNode.create("X(Q,W,E,R)")));
        final Node labeled = new LabeledTreeBuilder(node)
            .build(Collections.singleton(node), "color", "red")
            .getRoot();
        Assertions.assertEquals(labeled.toString(), node.toString());
        Assertions.assertFalse(node.deepCompare(labeled));
    }

    @Test
    void testDeepClone() {
        final String description = "X(A(B,C,D<\"data\">),E,F,G(H(I,j)))";
        final Node node = DraftNode.create(description);
        final Node clone = node.deepClone();
        Assertions.assertNotSame(node, clone);
        Assertions.assertTrue(clone.deepCompare(node));
    }

    @Test
    void testBuilder() {
        final Builder first = new IntegerLiteral.Constructor();
        first.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertFalse(first.setData(""));
        Assertions.assertTrue(first.setData("1"));
        Assertions.assertFalse(
            first.setChildrenList(Collections.singletonList(DraftNode.create("A")))
        );
        Assertions.assertTrue(first.setChildrenList(Collections.emptyList()));
        Assertions.assertTrue(first.isValid());
        final Node left = first.createNode();
        Assertions.assertNotNull(left);
        final Builder second = left.getType().createBuilder();
        Assertions.assertTrue(second.setData("2"));
        final Node right = second.createNode();
        final Builder third = new Addition.Constructor();
        Assertions.assertTrue(third.setData(""));
        Assertions.assertFalse(third.setData("abc"));
        Assertions.assertFalse(third.setChildrenList(Collections.singletonList(left)));
        Assertions.assertTrue(third.setChildrenList(Arrays.asList(left, right)));
        Assertions.assertTrue(third.isValid());
        final Node addition = third.createNode();
        Assertions.assertNotNull(addition);
    }

    @Test
    void testLocalHash() {
        final DiffTree first = LittleTrees.createTreeWithDeleteAction();
        final DiffTree second = LittleTrees.createTreeWithDeleteAction();
        int expected = first.getRoot().getLocalHash();
        int actual = second.getRoot().getLocalHash();
        Assertions.assertEquals(expected, actual);
        expected = first.getRoot().getChild(0).getLocalHash();
        actual = second.getRoot().getChild(0).getLocalHash();
        Assertions.assertEquals(expected, actual);
    }
}
