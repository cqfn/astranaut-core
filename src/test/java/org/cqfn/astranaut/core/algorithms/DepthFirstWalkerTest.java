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
package org.cqfn.astranaut.core.algorithms;

import java.util.List;
import java.util.Optional;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link DepthFirstWalker} class.
 * @since 1.1.5
 */
class DepthFirstWalkerTest {
    @Test
    void testFindFirst() {
        final Node root = DraftNode.create("A(B,C,D(E<\"eee\">,F<\"fff\">)))");
        final DepthFirstWalker traversal = new DepthFirstWalker(root);
        final Optional<Node> node = traversal.findFirst(node1 -> !node1.getData().isEmpty());
        Assertions.assertTrue(node.isPresent());
        Assertions.assertEquals("E", node.get().getTypeName());
    }

    @Test
    void testNotFoundFirst() {
        final Node root = DraftNode.create("A(B,C,D)");
        final DepthFirstWalker traversal = new DepthFirstWalker(root);
        final Optional<Node> node = traversal.findFirst(node1 -> !node1.getData().isEmpty());
        Assertions.assertFalse(node.isPresent());
    }

    @Test
    void testFindAll() {
        final Node root = DraftNode.create("A(B,C<\"ccc\">,D(E<\"eee\">)))");
        final DepthFirstWalker traversal = new DepthFirstWalker(root);
        final List<Node> list = traversal.findAll(node1 -> !node1.getData().isEmpty());
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void testCollectAll() {
        final Node root = DraftNode.create("A(B,C,D,E)");
        final DepthFirstWalker traversal = new DepthFirstWalker(root);
        final List<Node> list = traversal.collectAll();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(5, list.size());
    }

    @Test
    void testFindNothing() {
        final Node root = DraftNode.create("A(X,Y,Z)");
        final DepthFirstWalker traversal = new DepthFirstWalker(root);
        final List<Node> list = traversal.findAll(node1 -> !node1.getData().isEmpty());
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());
    }
}
