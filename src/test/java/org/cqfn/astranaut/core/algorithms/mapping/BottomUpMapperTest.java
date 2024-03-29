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

import java.util.Set;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link BottomUpMapper} class.
 *
 * @since 1.0
 */
class BottomUpMapperTest {
    /**
     * Test in which the identical trees are mapped.
     */
    @Test
    void testIdenticalTrees() {
        final Node first = LittleTrees.createTreeWithDeleteAction();
        final Node second = LittleTrees.createTreeWithDeleteAction();
        final Mapper mapper = new BottomUpMapper();
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(mapping.getRight(first), second);
        Assertions.assertEquals(mapping.getLeft(second), first);
    }

    /**
     * Testing mapping of two trees, with some node removed in the second tree.
     */
    @Test
    void testOneWasRemoved() {
        final Node first = LittleTrees.createStatementListWithThreeChildren(
            LittleTrees.createIntegerLiteral(2)
        );
        final Node second = LittleTrees.createStatementListWithTwoChildren();
        final Mapper mapper = new BottomUpMapper();
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(mapping.getRight(first), second);
        Assertions.assertEquals(mapping.getLeft(second), first);
        Node left = first.getChild(0);
        Node right = second.getChild(0);
        Assertions.assertEquals(mapping.getRight(left), right);
        Assertions.assertEquals(mapping.getLeft(right), left);
        left = first.getChild(2);
        right = second.getChild(1);
        Assertions.assertEquals(mapping.getRight(left), right);
        Assertions.assertEquals(mapping.getLeft(right), left);
        final Set<Node> deleted = mapping.getDeleted();
        Assertions.assertEquals(1, deleted.size());
        Assertions.assertEquals(first.getChild(1), deleted.iterator().next());
    }
}
