/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.Collections;
import org.cqfn.astranaut.core.algorithms.hash.AbsoluteHash;
import org.cqfn.astranaut.core.algorithms.hash.Hash;
import org.cqfn.astranaut.core.algorithms.mapping.TopDownMapper;
import org.cqfn.astranaut.core.base.DiffTree;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link DiffTreeBuilder} class.
 * @since 1.1.0
 */
class DiffTreeBuilderTest {
    /**
     * Testing the construction of a difference tree with an inserted node.
     */
    @Test
    void testTreeWithInsertedNode() {
        final Node before = LittleTrees.createStatementListWithTwoChildren();
        final Node after = LittleTrees.createStatementListWithThreeChildren(
            LittleTrees.createIntegerLiteral(2)
        );
        final DiffTreeBuilder builder = new DiffTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DiffTree diff = builder.getDiffTree();
        final DiffTree expected = LittleTrees.createTreeWithInsertAction();
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore().getRoot()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter().getRoot()));
    }

    /**
     * Testing the construction of a difference tree with a replaced node.
     */
    @Test
    void testTreeWithReplacedNode() {
        final Node before = LittleTrees.createStatementListWithThreeChildren(
            LittleTrees.createIntegerLiteral(2)
        );
        final Node after = LittleTrees.createStatementListWithThreeChildren(
            LittleTrees.createVariable("x")
        );
        final DiffTreeBuilder builder = new DiffTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DiffTree diff = builder.getDiffTree();
        final DiffTree expected = LittleTrees.createTreeWithReplaceAction();
        final Hash hash = new AbsoluteHash();
        final int diffhash = hash.calculate(diff);
        final int expectedhash = hash.calculate(expected);
        Assertions.assertEquals(expectedhash, diffhash);
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore().getRoot()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter().getRoot()));
    }

    /**
     * Testing the construction of a difference tree with a deleted node.
     */
    @Test
    void testTreeWithDeletedNode() {
        final Node before = LittleTrees.createStatementListWithThreeChildren(
            LittleTrees.createIntegerLiteral(2)
        );
        final Node after = LittleTrees.createStatementListWithTwoChildren();
        final DiffTreeBuilder builder = new DiffTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DiffTree diff = builder.getDiffTree();
        final DiffTree expected = LittleTrees.createTreeWithDeleteAction();
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore().getRoot()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter().getRoot()));
    }

    @Test
    void testTreeWithDeletedNodeInDepth() {
        final Node before = LittleTrees.createStatementBlock(
            LittleTrees.createStatementListWithThreeChildren(
                LittleTrees.createIntegerLiteral(2)
            )
        );
        final Node after = LittleTrees.createStatementBlock(
            LittleTrees.createStatementListWithTwoChildren()
        );
        final DiffTreeBuilder builder = new DiffTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DiffTree diff = builder.getDiffTree();
        final DiffTree expected = LittleTrees.createTreeWithDeleteActionInDepth();
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore().getRoot()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter().getRoot()));
    }

    @Test
    void testTreeWithReplacedNotUniqueNode() {
        final Node removed = LittleTrees.createIntegerLiteral(1);
        final Node added = LittleTrees.createIntegerLiteral(2);
        final Node before = LittleTrees.createStatementBlock(
            LittleTrees.wrapExpressionWithStatement(
                LittleTrees.createAssignment(
                    LittleTrees.createVariable("x"),
                    LittleTrees.createAddition(
                        LittleTrees.createVariable("x"),
                        LittleTrees.createIntegerLiteral(1)
                    )
                )
            ),
            LittleTrees.wrapExpressionWithStatement(
                LittleTrees.createAssignment(
                    LittleTrees.createVariable("x"),
                    LittleTrees.createAddition(
                        LittleTrees.createVariable("x"),
                        removed
                    )
                )
            )
        );
        final Node after = LittleTrees.createStatementBlock(
            LittleTrees.wrapExpressionWithStatement(
                LittleTrees.createAssignment(
                    LittleTrees.createVariable("x"),
                    LittleTrees.createAddition(
                        LittleTrees.createVariable("x"),
                        LittleTrees.createIntegerLiteral(1)
                    )
                )
            ),
            LittleTrees.wrapExpressionWithStatement(
                LittleTrees.createAssignment(
                    LittleTrees.createVariable("x"),
                    LittleTrees.createAddition(
                        LittleTrees.createVariable("x"),
                        added
                    )
                )
            )
        );
        final DiffTreeBuilder first = new DiffTreeBuilder(before);
        first.replaceNode(removed, added);
        final DiffTree expected = first.getDiffTree();
        Assertions.assertTrue(before.deepCompare(expected.getBefore().getRoot()));
        Assertions.assertTrue(after.deepCompare(expected.getAfter().getRoot()));
        final DiffTreeBuilder second = new DiffTreeBuilder(before);
        second.build(after, TopDownMapper.INSTANCE);
        final DiffTree actual = second.getDiffTree();
        Assertions.assertTrue(before.deepCompare(actual.getBefore().getRoot()));
        Assertions.assertTrue(after.deepCompare(actual.getAfter().getRoot()));
        Assertions.assertTrue(actual.deepCompare(expected));
    }

    @Test
    void testComplexCase() {
        final Node before = DraftNode.create("X(A,B,Y(C,D,E,F,J,K))");
        final Node after = DraftNode.create("X(A,G,Y(H,C,I,E,J,K))");
        final DiffTreeBuilder builder = new DiffTreeBuilder(new Tree(before));
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DiffTree diff = builder.getDiffTree();
        Assertions.assertTrue(before.deepCompare(diff.getBefore().getRoot()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter().getRoot()));
    }

    @Test
    void testWrongInsertion() {
        final DiffTreeBuilder builder = new DiffTreeBuilder(
            DraftNode.create("X")
        );
        final boolean result = builder.insertNode(
            new Insertion(
                DraftNode.create("A"),
                DraftNode.create("B")
            )
        );
        Assertions.assertFalse(result);
    }

    @Test
    void testWrongReplacement() {
        final DiffTreeBuilder builder = new DiffTreeBuilder(
            DraftNode.create("X")
        );
        final boolean result = builder.replaceNode(
            DraftNode.create("A"),
            DraftNode.create("B")
        );
        Assertions.assertFalse(result);
    }

    @Test
    void testWrongDeletion() {
        final DiffTreeBuilder builder = new DiffTreeBuilder(
            DraftNode.create("X")
        );
        final boolean result = builder.deleteNode(DraftNode.create("A"));
        Assertions.assertFalse(result);
    }

    @Test
    void createDiffTreeFromSubtree() {
        final Node literal = LittleTrees.createIntegerLiteral(1);
        final Node replacement = LittleTrees.createIntegerLiteral(2);
        final Node first = LittleTrees.wrapExpressionWithStatement(
            LittleTrees.createAssignment(
                LittleTrees.createVariable("x"),
                literal
            )
        );
        final Node second = LittleTrees.createReturnStatement(null);
        final Node root = LittleTrees.createStatementBlock(first, second);
        final Node subtree = new SubtreeBuilder(root, SubtreeBuilder.EXCLUDE).create(
            Collections.singleton(second)
        );
        final DiffTreeBuilder builder = new DiffTreeBuilder(subtree);
        final boolean flag = builder.replaceNode(literal, replacement);
        Assertions.assertTrue(flag);
        final DiffTree diff = builder.getDiffTree();
        final Tree after = diff.getAfter();
        final Tree expected = new Tree(
            LittleTrees.createStatementBlock(
                LittleTrees.wrapExpressionWithStatement(
                    LittleTrees.createAssignment(
                        LittleTrees.createVariable("x"),
                        LittleTrees.createIntegerLiteral(2)
                    )
                )
            )
        );
        Assertions.assertTrue(expected.deepCompare(after));
    }
}
