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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Subtree}.
 *
 * @since 1.1.4
 */
class SubtreeTest {
    @Test
    void testSubtreeCreation() {
        final Node original = DraftNode.createByDescription("A(B,C(D,E,F))");
        final List<String> list = Arrays.asList("A", "C", "E", "F");
        final Set<Node> selected =
            new NodeSelector(original).select((node, parents) -> list.contains(node.getTypeName()));
        final Subtree algorithm = new Subtree(original, Subtree.INCLUDE);
        final Node subtree = algorithm.create(selected);
        final Node expected = DraftNode.createByDescription("A(C(E,F))");
        Assertions.assertTrue(expected.deepCompare(subtree));
    }

    @Test
    void testUsingOneSubtreeInstanceForCreatingTwoSubtrees() {
        final Node original = DraftNode.createByDescription("X(A,B,C,D,E)");
        final Subtree algorithm = new Subtree(original, Subtree.INCLUDE);
        final Node first = algorithm.create(
            new HashSet<>(
                Arrays.asList(
                    original.getChild(0),
                    original.getChild(1)
                )
            )
        );
        Assertions.assertEquals(2,  first.getChildCount());
        final Node second = algorithm.create(
            new HashSet<>(
                Arrays.asList(
                    original.getChild(0),
                    original.getChild(1),
                    original.getChild(2)
                )
            )
        );
        Assertions.assertEquals(2,  first.getChildCount());
        Assertions.assertEquals(3,  second.getChildCount());
    }

    @Test
    void testingParametersThatProduceEmptyTree() {
        final Node original = DraftNode.createByDescription("X(A,B,C)");
        final Set<Node> set = new HashSet<>(
            Arrays.asList(
                DraftNode.createByDescription("D"),
                DraftNode.createByDescription("E")
            )
        );
        final Subtree algorithm = new Subtree(original, Subtree.INCLUDE);
        final Node subtree = algorithm.create(set);
        Assertions.assertSame(subtree, EmptyTree.INSTANCE);
    }

    @Test
    void testingParametersThatProduceTreeIdenticalToOriginal() {
        final Node original = DraftNode.createByDescription("X(A,B,C,D(E,F))");
        final Set<Node> selected =
            new NodeSelector(original).select((node, parents) -> true);
        final Subtree algorithm = new Subtree(original, Subtree.INCLUDE);
        final Node subtree = algorithm.create(selected);
        Assertions.assertTrue(original.deepCompare(subtree));
    }

    @Test
    void testSubtreeCreationWithExcludedNodes() {
        final Node original = DraftNode.createByDescription("A(B,C(D,E,F))");
        final Set<Node> selected =
            new NodeSelector(original).select(
                (node, parents) -> {
                    final String name = node.getTypeName();
                    return name.equals("B") || name.equals("E");
                }
            );
        final Subtree algorithm = new Subtree(original, Subtree.EXCLUDE);
        final Node subtree = algorithm.create(selected);
        final Node expected = DraftNode.createByDescription("A(C(D,F))");
        Assertions.assertTrue(expected.deepCompare(subtree));
    }

    @Test
    void testSubtreeCreationWhenAllNodesAreExcluded() {
        final Node original = DraftNode.createByDescription("X(A,B,C(D,E))");
        final Set<Node> selected =
            new NodeSelector(original).select((node, parents) -> true);
        final Subtree algorithm = new Subtree(original, Subtree.EXCLUDE);
        final Node subtree = algorithm.create(selected);
        Assertions.assertSame(subtree, EmptyTree.INSTANCE);
    }
}
