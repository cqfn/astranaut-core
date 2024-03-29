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
import java.util.Set;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.EmptyFragment;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Subtree}.
 *
 * @since 1.1.4
 */
class SubtreeTest {

    @Test
    void testSubtree() {
        final Pair<Node, Set<Node>> init = LittleTrees.createTreeWithSubtree();
        final Node tree = init.getKey();
        final Set<Node> nodes = init.getValue();
        final Subtree subtree = new Subtree(tree);
        final SubtreeNode root = subtree.create(nodes);
        final SubtreeNode changed = root
            .getChild(0).getChild(0)
            .getChild(0).getChild(0);
        Assertions.assertEquals("IntegerLiteral", changed.getType().getName());
        final SubtreeNode leaf = root.getChild(0).getChild(2);
        Assertions.assertEquals(0, leaf.getChildCount());
        Assertions.assertEquals(root.getFragment(), EmptyFragment.INSTANCE);
        Assertions.assertEquals(root.getData(), tree.getData());
    }

    @Test
    void testSubtreeWithDelete() {
        final Node tree = LittleTrees.createTreeWithDeleteAction();
        final Set<Node> nodes = new HashSet<>();
        nodes.add(tree);
        final Node stmtleft = tree.getChild(0);
        final Node assleft = stmtleft.getChild(0);
        final Node literal = assleft.getChild(1);
        nodes.add(stmtleft);
        nodes.add(assleft);
        nodes.add(literal);
        final Node delete = tree.getChild(1);
        final Node stmtright = delete.getChild(0);
        final Node assright = stmtright.getChild(0);
        nodes.add(delete);
        nodes.add(stmtright);
        nodes.add(assright);
        nodes.add(tree.getChild(2));
        final Subtree subtree = new Subtree(tree);
        final SubtreeNode root = subtree.create(nodes);
        final SubtreeNode changed = root.getChild(0).getChild(0).getChild(0);
        Assertions.assertEquals("IntegerLiteral", changed.getType().getName());
        final SubtreeNode leaf = root.getChild(2);
        Assertions.assertEquals(0, leaf.getChildCount());
        final SubtreeNode actionleaf = root.getChild(1).getChild(0).getChild(0);
        Assertions.assertEquals(actionleaf.getType().getName(), "SimpleAssignment");
        Assertions.assertEquals(0, actionleaf.getChildCount());
    }

    @Test
    void testUsingOneSubtreeInstanceForCreatingTwoSubtrees() {
        final Node original = DraftNode.createByDescription("X(A,B,C,D,E)");
        final Subtree subtree = new Subtree(original);
        final Node first = subtree.create(
            new HashSet<>(
                Arrays.asList(
                    original.getChild(0),
                    original.getChild(1)
                )
            )
        );
        Assertions.assertEquals(2,  first.getChildCount());
        final Node second = subtree.create(
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
}
