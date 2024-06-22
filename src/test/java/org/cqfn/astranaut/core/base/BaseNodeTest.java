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

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering base node, i.e. {@link Node} interface.
 *
 * @since 1.1.0
 */
class BaseNodeTest {
    /**
     * Testing {@link  Node#deepCompare(Node)} method.
     */
    @Test
    void testDeepCompareMethod() {
        final Node first = LittleTrees.createTreeWithDeleteAction();
        Assertions.assertTrue(first.deepCompare(first));
        final DifferenceNode second = LittleTrees.createTreeWithDeleteAction();
        Assertions.assertTrue(first.deepCompare(second));
        final boolean deleted = second.deleteNode(0);
        Assertions.assertTrue(deleted);
        Assertions.assertFalse(first.deepCompare(second));
    }

    /**
     * Testing {@link  Node#deepCompare(Node)} method.
     */
    @Test
    void testNodeIterator() {
        final Node root = DraftNode.createByDescription("A(B,C)");
        final Iterator<Node> iterator = root.getIteratorOverChildren();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals("B", iterator.next().getTypeName());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals("C", iterator.next().getTypeName());
        Assertions.assertFalse(iterator.hasNext());
        boolean oops = false;
        try {
            iterator.next();
        } catch (final NoSuchElementException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
