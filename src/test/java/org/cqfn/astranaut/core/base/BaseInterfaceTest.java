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
        Assertions.assertNotNull(type.getProperty("abracadabra"));
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
    void testNotNullProperty() {
        final Node node = DraftNode.create("X");
        Assertions.assertNotNull(node.getProperty("abc"));
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
        this.testListOtherMethods(root, list);
    }

    void testListModifiersByOne(final List<Node> list) {
        boolean oops = false;
        try {
            list.add(DraftNode.create("H"));
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.remove(list.get(0));
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.clear();
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.set(1, DraftNode.create("K"));
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.add(1, DraftNode.create("L"));
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.remove(1);
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    void testListModifiersByCollection(final List<Node> list) {
        final Collection<Node> collection = Arrays.asList(
            DraftNode.create("I"),
            DraftNode.create("J")
        );
        boolean oops = false;
        try {
            list.addAll(collection);
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.addAll(1, collection);
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.removeAll(collection);
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.retainAll(collection);
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    void testListIterator(final List<Node> list) {
        ListIterator<Node> cursor = list.listIterator();
        final StringBuilder builder = new StringBuilder();
        while (cursor.hasNext()) {
            builder.append(cursor.next().getTypeName());
        }
        Assertions.assertEquals("DEF", builder.toString());
        cursor = list.listIterator(1);
        boolean oops = false;
        try {
            cursor.remove();
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            cursor.set(DraftNode.create("M"));
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            cursor.add(DraftNode.create("N"));
        } catch (final UnsupportedOperationException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        Assertions.assertEquals(1, cursor.nextIndex());
        Assertions.assertEquals(0, cursor.previousIndex());
        Assertions.assertTrue(cursor.hasPrevious());
        Assertions.assertEquals("D", cursor.previous().getTypeName());
        Assertions.assertFalse(cursor.hasPrevious());
    }

    void testListOtherMethods(final Node root, final List<Node> list) {
        Assertions.assertEquals(3, list.size());
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertTrue(list.contains(root.getChild(1)));
        final Node alien = DraftNode.create("G");
        Assertions.assertFalse(list.contains(alien));
        final Iterator<Node> iterator = list.iterator();
        final StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getTypeName());
        }
        Assertions.assertEquals("DEF", builder.toString());
        final Object[] objects = list.toArray();
        Assertions.assertEquals(3, objects.length);
        final Node[] array = new Node[2];
        final Node[] same = list.toArray(array);
        Assertions.assertSame(same, array);
        Assertions.assertSame(root.getChild(0), array[0]);
        Assertions.assertSame(root.getChild(1), array[1]);
        Assertions.assertEquals(1, list.indexOf(root.getChild(1)));
        Assertions.assertEquals(-1, list.indexOf(alien));
        Assertions.assertEquals(1, list.lastIndexOf(root.getChild(1)));
        Assertions.assertEquals(-1, list.lastIndexOf(alien));
        final List<Node> sub = list.subList(1, 3);
        Assertions.assertEquals(2, sub.size());
        Assertions.assertEquals(root.getChild(1), sub.get(0));
        boolean oops = false;
        try {
            sub.get(3);
        } catch (final IndexOutOfBoundsException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        oops = false;
        try {
            list.subList(0, 4);
        } catch (final IndexOutOfBoundsException ex) {
            oops = true;
        }
        Assertions.assertTrue(oops);
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
        Assertions.assertFalse(node.deepCompare(DraftNode.create("X(Q,W,E,R,T(Y))")));
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
}
