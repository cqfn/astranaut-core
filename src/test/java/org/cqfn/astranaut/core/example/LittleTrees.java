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
package org.cqfn.astranaut.core.example;

import java.util.Arrays;
import java.util.Collections;
import org.cqfn.astranaut.core.algorithms.DiffTreeBuilder;
import org.cqfn.astranaut.core.algorithms.PatternBuilder;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DiffTree;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Pattern;
import org.cqfn.astranaut.core.example.green.Addition;
import org.cqfn.astranaut.core.example.green.ExpressionStatement;
import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.cqfn.astranaut.core.example.green.Return;
import org.cqfn.astranaut.core.example.green.SimpleAssignment;
import org.cqfn.astranaut.core.example.green.StatementBlock;
import org.cqfn.astranaut.core.example.green.Variable;
import org.junit.jupiter.api.Assertions;

/**
 * Little trees for testing purposes.
 * @since 1.1.0
 */
@SuppressWarnings({"PMD.ProhibitPublicStaticMethods", "PMD.TooManyMethods"})
public final class LittleTrees {
    /**
     * Private constructor.
     */
    private LittleTrees() {
    }

    /**
     * Creates a node that represents variable access.
     * @param name Variable name
     * @return Resulting node
     */
    public static Node createVariable(final String name) {
        Node result = DummyNode.INSTANCE;
        final Variable.Constructor ctor = new Variable.Constructor();
        ctor.setData(name);
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents integer literal.
     * @param value Integer value
     * @return Resulting node
     */
    public static Node createIntegerLiteral(final int value) {
        Node result = DummyNode.INSTANCE;
        final IntegerLiteral.Constructor ctor = new IntegerLiteral.Constructor();
        ctor.setData(String.valueOf(value));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents assignment operator.
     * @param left Left operand (assignable expression)
     * @param right Right operand (expression)
     * @return Resulting node
     */
    public static Node createAssignment(final Node left, final Node right) {
        Node result = DummyNode.INSTANCE;
        final SimpleAssignment.Constructor ctor = new SimpleAssignment.Constructor();
        ctor.setChildrenList(Arrays.asList(left, right));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents additional operator.
     * @param left Left operand (expression)
     * @param right Right operand (expression)
     * @return Resulting node
     */
    public static Node createAddition(final Node left, final Node right) {
        Node result = DummyNode.INSTANCE;
        final Addition.Constructor ctor = new Addition.Constructor();
        ctor.setChildrenList(Arrays.asList(left, right));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Represents expression as a statement.
     * @param expression Expression
     * @return Resulting node
     */
    public static Node wrapExpressionWithStatement(final Node expression) {
        Node result = DummyNode.INSTANCE;
        final ExpressionStatement.Constructor ctor = new ExpressionStatement.Constructor();
        ctor.setChildrenList(Collections.singletonList(expression));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents return statement.
     * @param expression Expression to return (if exist)
     * @return Resulting node
     */
    public static Node createReturnStatement(final Node expression) {
        Node result = DummyNode.INSTANCE;
        final Return.Constructor ctor = new Return.Constructor();
        if (expression != null) {
            ctor.setChildrenList(Collections.singletonList(expression));
        }
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents block of statements.
     * @param statements List of statements
     * @return Resulting node
     */
    public static Node createStatementBlock(final Node... statements) {
        Node result = DummyNode.INSTANCE;
        final StatementBlock.Constructor ctor = new StatementBlock.Constructor();
        ctor.setChildrenList(Arrays.asList(statements));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a tree (statement list) that has two children.
     * @return Root node
     */
    public static Node createStatementListWithTwoChildren() {
        return createStatementBlock(
            wrapExpressionWithStatement(
                createAssignment(
                    createVariable("x"),
                    createIntegerLiteral(1)
                )
            ),
            createReturnStatement(
                createVariable("x")
            )
        );
    }

    /**
     * Creates a tree (statement list) that has three children.
     * @param assignable Node whose value is assigned in the third (middle) statement
     * @return Root node
     */
    public static Node createStatementListWithThreeChildren(final Node assignable) {
        return createStatementBlock(
            wrapExpressionWithStatement(
                createAssignment(
                    createVariable("x"),
                    createIntegerLiteral(1)
                )
            ),
            wrapExpressionWithStatement(
                createAssignment(
                    createVariable("y"),
                    assignable
                )
            ),
            createReturnStatement(
                createVariable("x")
            )
        );
    }

    /**
     * Creates a tree that has an "insert" action in it.
     * @return Root node
     */
    public static DiffTree createTreeWithInsertAction() {
        final Node after =
            wrapExpressionWithStatement(
                createAssignment(
                    createVariable("x"),
                    createIntegerLiteral(1)
                )
            );
        final Node inserted = wrapExpressionWithStatement(
            createAssignment(
                createVariable("y"),
                createIntegerLiteral(2)
            )
        );
        final DiffTreeBuilder builder = new DiffTreeBuilder(
            createStatementBlock(
                after,
                createReturnStatement(
                    createVariable("x")
                )
            )
        );
        builder.insertNode(new Insertion(inserted, after));
        return builder.getDiffTree();
    }

    /**
     * Creates a tree that has an “insert” action, with insert as the very first child node.
     * @return Root node
     */
    public static DiffTree createTreeWithInsertFirstAction() {
        final Node inserted = wrapExpressionWithStatement(
            createAssignment(
                createVariable("y"),
                createIntegerLiteral(2)
            )
        );
        final Node second =
            wrapExpressionWithStatement(
                createAssignment(
                    createVariable("x"),
                    createIntegerLiteral(1)
                )
            );
        final Node parent =
            createStatementBlock(
                second,
                createReturnStatement(
                    createVariable("x")
                )
            );
        final DiffTreeBuilder builder = new DiffTreeBuilder(parent);
        builder.insertNode(new Insertion(inserted, parent, null));
        return builder.getDiffTree();
    }

    /**
     * Creates a tree that has a "replace" action in it.
     * @return Root node
     */
    public static DiffTree createTreeWithReplaceAction() {
        final Node before = createIntegerLiteral(2);
        final Node after = createVariable("x");
        final DiffTreeBuilder builder = new DiffTreeBuilder(
            createStatementBlock(
                wrapExpressionWithStatement(
                    createAssignment(
                        createVariable("x"),
                        createIntegerLiteral(1)
                    )
                ),
                wrapExpressionWithStatement(
                    createAssignment(
                        createVariable("y"),
                        before
                    )
                ),
                createReturnStatement(
                    createVariable("x")
                )
            )
        );
        builder.replaceNode(before, after);
        return builder.getDiffTree();
    }

    /**
     * Creates a tree that has a "delete" action in it.
     * @return Root node
     */
    public static DiffTree createTreeWithDeleteAction() {
        final Node victim = wrapExpressionWithStatement(
            createAssignment(
                createVariable("y"),
                createIntegerLiteral(2)
            )
        );
        final DiffTreeBuilder builder = new DiffTreeBuilder(
            createStatementBlock(
                wrapExpressionWithStatement(
                    createAssignment(
                        createVariable("x"),
                        createIntegerLiteral(1)
                    )
                ),
                victim,
                createReturnStatement(
                    createVariable("x")
                )
            )
        );
        builder.deleteNode(victim);
        return builder.getDiffTree();
    }

    /**
     * Creates a tree that has a "delete" action in it.
     * This action is just below the root, so that the number and type of children of the root
     * do not change.
     * @return Root node
     */
    public static DiffTree createTreeWithDeleteActionInDepth() {
        final Node victim = wrapExpressionWithStatement(
            createAssignment(
                createVariable("y"),
                createIntegerLiteral(2)
            )
        );
        final DiffTreeBuilder builder = new DiffTreeBuilder(
            createStatementBlock(
                createStatementBlock(
                    wrapExpressionWithStatement(
                        createAssignment(
                            createVariable("x"),
                            createIntegerLiteral(1)
                        )
                    ),
                    victim,
                    createReturnStatement(
                        createVariable("x")
                    )
                )
            )
        );
        builder.deleteNode(victim);
        return builder.getDiffTree();
    }

    /**
     * Creates pattern with a hole.
     * @return A pattern
     */
    public static Pattern createPatternWithHole() {
        Builder ctor = new Variable.Constructor();
        ctor.setData("w");
        final Node first = ctor.createNode();
        ctor = new IntegerLiteral.Constructor();
        ctor.setData("1");
        final Node second = ctor.createNode();
        ctor = new Addition.Constructor();
        ctor.setChildrenList(Arrays.asList(first, second));
        final Node addition = ctor.createNode();
        ctor = new IntegerLiteral.Constructor();
        ctor.setData("2");
        final Node replacement = ctor.createNode();
        final DiffTreeBuilder dtbld = new DiffTreeBuilder(addition);
        dtbld.replaceNode(second, replacement);
        final PatternBuilder pbld = new PatternBuilder(dtbld.getDiffTree());
        pbld.makeHole(first, 0);
        final Pattern pattern = pbld.getPattern();
        Assertions.assertNotNull(pattern);
        return pattern;
    }
}
