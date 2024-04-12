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

import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link DeepTraversal} class.
 *
 * @since 1.1.5
 */
class DeepTraversalTest {
    @Test
    void testFindFirstFromRoot() {
        final Node root = DraftNode.createByDescription("A(B,C,D(E<\"test\">,F<\"xxx\">)))");
        final DeepTraversal traversal = new DeepTraversal(root);
        final Node node = traversal.findFirstFromRoot(node1 -> !node1.getData().isEmpty());
        Assertions.assertNotNull(node);
        Assertions.assertEquals("E", node.getTypeName());
    }

    @Test
    void testNotFoundFirstFromRoot() {
        final Node root = DraftNode.createByDescription("A(B,C,D)");
        final DeepTraversal traversal = new DeepTraversal(root);
        final Node node = traversal.findFirstFromRoot(node1 -> !node1.getData().isEmpty());
        Assertions.assertNull(node);
    }
}
