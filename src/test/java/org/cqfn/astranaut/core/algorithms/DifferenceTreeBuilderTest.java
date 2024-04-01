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

import org.cqfn.astranaut.core.DifferenceNode;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.hash.AbsoluteHash;
import org.cqfn.astranaut.core.algorithms.hash.Hash;
import org.cqfn.astranaut.core.algorithms.mapping.TopDownMapper;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link DifferenceTreeBuilder} class.
 *
 * @since 1.1.0
 */
class DifferenceTreeBuilderTest {
    /**
     * Testing the construction of a difference tree with an inserted node.
     */
    @Test
    void testTreeWithInsertedNode() {
        final Node before = LittleTrees.createStatementListWithTwoChildren();
        final Node after = LittleTrees.createStatementListWithThreeChildren(
            LittleTrees.createIntegerLiteral(3)
        );
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DifferenceNode diff = builder.getRoot();
        final Node expected = LittleTrees.createTreeWithInsertAction();
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter()));
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
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DifferenceNode diff = builder.getRoot();
        final Node expected = LittleTrees.createTreeWithReplaceAction();
        final Hash hash = new AbsoluteHash();
        final int diffhash = hash.calculate(diff);
        final int expectedhash = hash.calculate(expected);
        Assertions.assertEquals(expectedhash, diffhash);
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter()));
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
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DifferenceNode diff = builder.getRoot();
        final Node expected = LittleTrees.createTreeWithDeleteAction();
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter()));
    }

    /**
     * Testing the construction of a difference tree with a deleted node.
     * This node is just below the root, so that the number and type of children of the root
     * do not change.
     */
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
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(before);
        final boolean result = builder.build(after, TopDownMapper.INSTANCE);
        Assertions.assertTrue(result);
        final DifferenceNode diff = builder.getRoot();
        final Node expected = LittleTrees.createTreeWithDeleteActionInDepth();
        Assertions.assertTrue(expected.deepCompare(diff));
        Assertions.assertTrue(before.deepCompare(diff.getBefore()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter()));
    }

    @Test
    @Disabled
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
        final DifferenceTreeBuilder first = new DifferenceTreeBuilder(before);
        first.replaceNode(removed, added);
        final DifferenceNode expected = first.getRoot();
        Assertions.assertTrue(before.deepCompare(expected.getBefore()));
        Assertions.assertTrue(after.deepCompare(expected.getAfter()));
        final DifferenceTreeBuilder second = new DifferenceTreeBuilder(before);
        second.build(after, TopDownMapper.INSTANCE);
        final DifferenceNode actual = second.getRoot();
        Assertions.assertTrue(before.deepCompare(actual.getBefore()));
        Assertions.assertTrue(after.deepCompare(actual.getAfter()));
        Assertions.assertTrue(actual.deepCompare(expected));
    }
}
