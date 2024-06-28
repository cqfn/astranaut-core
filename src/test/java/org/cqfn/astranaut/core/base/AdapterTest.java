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
import java.util.List;
import org.cqfn.astranaut.core.algorithms.conversion.Adapter;
import org.cqfn.astranaut.core.algorithms.conversion.Converter;
import org.cqfn.astranaut.core.example.green.GreenFactory;
import org.cqfn.astranaut.core.example.javascript.rules.Rule0;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Adapter} class.
 *
 * @since 1.0
 */
class AdapterTest {
    /**
     * The 'literal' string constant.
     */
    private static final String STR_LITERAL = "literal";

    /**
     * The 'Expression' string constant.
     */
    private static final String STR_EXPRESSION = "Expression";

    /**
     * The 'Addition' string constant.
     */
    private static final String STR_ADDITION = "Addition";

    /**
     * The 'singleExpression' string constant.
     */
    private static final String STR_SINGLE_EXPR = "singleExpression";

    /**
     * The 'identifier' string constant.
     */
    private static final String STR_IDENTIFIER = "identifier";

    /**
     * The 'IntegerLiteral' string constant.
     */
    private static final String STR_INT_LITERAL = "IntegerLiteral";

    /**
     * The 'Variable' string constant.
     */
    private static final String STR_VARIABLE = "Variable";

    /**
     * The 'numericLiteral' string constant.
     */
    private static final String STR_NUM_LITERAL = "numericLiteral";

    /**
     * Testing tree converter.
     */
    @Test
    void testTreeConverter() {
        final Node original = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            this.createNode(
                AdapterTest.STR_SINGLE_EXPR,
                "",
                this.createNode(
                    AdapterTest.STR_IDENTIFIER,
                    "",
                    this.createNode(AdapterTest.STR_LITERAL, "x")
                )
            ),
            this.createNode(AdapterTest.STR_LITERAL, "+"),
            this.createNode(
                AdapterTest.STR_SINGLE_EXPR,
                "",
                this.createNode(
                    AdapterTest.STR_LITERAL,
                    "",
                    this.createNode(
                        AdapterTest.STR_NUM_LITERAL,
                        "",
                        this.createNode(AdapterTest.STR_LITERAL, "0")
                    )
                )
            )
        );
        final List<Converter> converters = Arrays.asList(
            new VariableConverter(),
            new NumericConverter(),
            new AdditionConverter()
        );
        final Factory factory = GreenFactory.INSTANCE;
        final Adapter adapter = new Adapter(converters, factory);
        final Node converted = adapter.convert(original);
        Assertions.assertEquals(converted.getTypeName(), AdapterTest.STR_ADDITION);
        Assertions.assertEquals(converted.getChildCount(), 2);
        Assertions.assertEquals(
            converted.getChild(0).getTypeName(),
            AdapterTest.STR_VARIABLE
        );
        Assertions.assertEquals(converted.getChild(0).getData(), "x");
        Assertions.assertEquals(
            converted.getChild(1).getTypeName(),
            AdapterTest.STR_INT_LITERAL
        );
        Assertions.assertEquals(converted.getChild(1).getData(), "0");
    }

    /**
     * Testing tree converter (complex case).
     */
    @Test
    void testTreeConverterComplexCase() {
        final Node subtree = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            this.createNode(
                AdapterTest.STR_SINGLE_EXPR,
                "",
                this.createNode(
                    AdapterTest.STR_LITERAL,
                    "",
                    this.createNode(
                        AdapterTest.STR_NUM_LITERAL,
                        "",
                        this.createNode(AdapterTest.STR_LITERAL, "2")
                    )
                )
            ),
            this.createNode(AdapterTest.STR_LITERAL, "+"),
            this.createNode(
                AdapterTest.STR_SINGLE_EXPR,
                "",
                this.createNode(
                    AdapterTest.STR_LITERAL,
                    "",
                    this.createNode(
                        AdapterTest.STR_NUM_LITERAL,
                        "",
                        this.createNode(AdapterTest.STR_LITERAL, "3")
                    )
                )
            )
        );
        final Node original = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            this.createNode(
                AdapterTest.STR_SINGLE_EXPR,
                "",
                this.createNode(
                    AdapterTest.STR_IDENTIFIER,
                    "",
                    this.createNode(AdapterTest.STR_LITERAL, "x")
                )
            ),
            this.createNode(AdapterTest.STR_LITERAL, "+"),
            subtree
        );
        final List<Converter> converters = Arrays.asList(
            new VariableConverter(),
            new NumericConverter(),
            new AdditionConverter()
        );
        final Factory factory = GreenFactory.INSTANCE;
        final Adapter adapter = new Adapter(converters, factory);
        final Node converted = adapter.convert(original);
        Assertions.assertEquals(converted.getTypeName(), AdapterTest.STR_ADDITION);
        Assertions.assertEquals(converted.getChildCount(), 2);
        Assertions.assertEquals(converted.getChild(0).getTypeName(), AdapterTest.STR_VARIABLE);
        Assertions.assertEquals(converted.getChild(0).getData(), "x");
        Assertions.assertEquals(converted.getChild(1).getTypeName(), AdapterTest.STR_ADDITION);
    }

    /**
     * Test covering the variable converter.
     */
    @Test
    void variableConverterTest() {
        final Factory factory = GreenFactory.INSTANCE;
        final Converter converter = new VariableConverter();
        final Node original = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            this.createNode(
                AdapterTest.STR_IDENTIFIER,
                "",
                this.createNode(
                    AdapterTest.STR_LITERAL,
                    "x"
                )
            )
        );
        final Node converted = converter.convert(original, factory);
        Assertions.assertEquals(converted.getTypeName(), AdapterTest.STR_VARIABLE);
        Assertions.assertEquals(converted.getChildCount(), 0);
        Assertions.assertEquals(converted.getData(), "x");
        Assertions.assertTrue(converted.belongsToGroup(AdapterTest.STR_EXPRESSION));
        final Node bad = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            this.createNode(
                AdapterTest.STR_LITERAL,
                "",
                this.createNode(
                    AdapterTest.STR_NUM_LITERAL,
                    "",
                    this.createNode(AdapterTest.STR_LITERAL, "0")
                )
            )
        );
        final Node empty = converter.convert(bad, factory);
        Assertions.assertEquals(empty, DummyNode.INSTANCE);
    }

    /**
     * Test covering the variable converter created as an example for generation.
     */
    @Test
    void exampleVariableConverterTest() {
        final Factory factory = GreenFactory.INSTANCE;
        final Converter converter = Rule0.INSTANCE;
        final Node original = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            this.createNode(
                AdapterTest.STR_IDENTIFIER,
                "",
                this.createNode(
                    AdapterTest.STR_LITERAL,
                    "Z"
                )
            )
        );
        final Node converted = converter.convert(original, factory);
        Assertions.assertEquals(converted.getTypeName(), AdapterTest.STR_VARIABLE);
        Assertions.assertEquals(converted.getChildCount(), 0);
        Assertions.assertEquals(converted.getData(), "Z");
        Assertions.assertTrue(converted.belongsToGroup(AdapterTest.STR_EXPRESSION));
    }

    /**
     * Test covering the numeric literal converter.
     */
    @Test
    void numericConverterTest() {
        final Factory factory = GreenFactory.INSTANCE;
        final Converter converter = new NumericConverter();
        final Node original = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            this.createNode(
                AdapterTest.STR_LITERAL,
                "",
                this.createNode(
                    AdapterTest.STR_NUM_LITERAL,
                    "",
                    this.createNode(AdapterTest.STR_LITERAL, "0")
                )
            )
        );
        final Node converted = converter.convert(original, factory);
        Assertions.assertEquals(converted.getTypeName(), AdapterTest.STR_INT_LITERAL);
        Assertions.assertEquals(converted.getChildCount(), 0);
        Assertions.assertEquals(converted.getData(), "0");
        Assertions.assertTrue(converted.belongsToGroup(AdapterTest.STR_EXPRESSION));
    }

    /**
     * Test covering the addition operators converter.
     */
    @Test
    void additionConverterTest() {
        final Factory factory = GreenFactory.INSTANCE;
        final Converter converter = new AdditionConverter();
        final Builder left = factory.createBuilder(AdapterTest.STR_VARIABLE);
        left.setData("x");
        final Builder right = factory.createBuilder(AdapterTest.STR_INT_LITERAL);
        right.setData("0");
        final Node original = this.createNode(
            AdapterTest.STR_SINGLE_EXPR,
            "",
            left.createNode(),
            this.createNode(AdapterTest.STR_LITERAL, "+"),
            right.createNode()
        );
        final Node converted = converter.convert(original, factory);
        Assertions.assertEquals(converted.getTypeName(), AdapterTest.STR_ADDITION);
        Assertions.assertEquals(converted.getChildCount(), 2);
        Assertions.assertTrue(converted.belongsToGroup(AdapterTest.STR_EXPRESSION));
    }

    /**
     * Creates node for test purposes.
     * @param type The type name
     * @param data The data (in a textual format)
     * @param children The list of children
     * @return A new node
     */
    private Node createNode(final String type, final String data, final Node... children) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setData(data);
        ctor.setChildrenList(Arrays.asList(children));
        return ctor.createNode();
    }

    /**
     * Converter variables to a new format.
     * @since 1.0
     */
    private static class VariableConverter implements Converter {
        @Override
        public Node convert(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_SINGLE_EXPR) && root.getChildCount() == 1) {
                result = VariableConverter.firstRule(root.getChild(0), factory);
            }
            return result;
        }

        /**
         * Checking the applicability of the rule to a subtree.
         * @param root Root node of the subtree
         * @param factory The node factory
         * @return Converted node
         */
        private static Node firstRule(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_IDENTIFIER) && root.getChildCount() == 1) {
                result = VariableConverter.secondRule(root.getChild(0), factory);
            }
            return result;
        }

        /**
         * Checking the applicability of the rule to a subtree.
         * @param root Root node of the subtree
         * @param factory The node factory
         * @return Converted node
         */
        private static Node secondRule(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_LITERAL) && root.getChildCount() == 0) {
                final Builder builder = factory.createBuilder(AdapterTest.STR_VARIABLE);
                builder.setData(root.getData());
                result = builder.createNode();
            }
            return result;
        }
    }

    /**
     * Converter numeric literals to a new format.
     * @since 1.0
     */
    private static class NumericConverter implements Converter {
        @Override
        public Node convert(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_SINGLE_EXPR) && root.getChildCount() == 1) {
                result = firstRule(root.getChild(0), factory);
            }
            return result;
        }

        /**
         * Checking the applicability of the rule to a subtree.
         * @param root Root node of the subtree
         * @param factory The node factory
         * @return Converted node
         */
        private static Node firstRule(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_LITERAL) && root.getChildCount() == 1) {
                result = secondRule(root.getChild(0), factory);
            }
            return result;
        }

        /**
         * Checking the applicability of the rule to a subtree.
         * @param root Root node of the subtree
         * @param factory The node factory
         * @return Converted node
         */
        private static Node secondRule(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_NUM_LITERAL) && root.getChildCount() == 1) {
                result = thirdRule(root.getChild(0), factory);
            }
            return result;
        }

        /**
         * Checking the applicability of the rule to a subtree.
         * @param root Root node of the subtree
         * @param factory The node factory
         * @return Converted node
         */
        private static Node thirdRule(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_LITERAL) && root.getChildCount() == 0) {
                final Builder builder = factory.createBuilder(AdapterTest.STR_INT_LITERAL);
                builder.setData(root.getData());
                result = builder.createNode();
            }
            return result;
        }
    }

    /**
     * Converter addition operators to a new format.
     * @since 1.0
     */
    private static class AdditionConverter implements Converter {
        /**
         * Expected operator.
         */
        private static final String EXPECTED_OPERATOR = "+";

        /**
         * Expected node count.
         */
        private static final int EXPECTED_COUNT = 3;

        @Override
        public Node convert(final Node root, final Factory factory) {
            Node result = DummyNode.INSTANCE;
            if (root.belongsToGroup(AdapterTest.STR_SINGLE_EXPR)
                && root.getChildCount() == AdditionConverter.EXPECTED_COUNT) {
                final Node first = root.getChild(0);
                final Node second = root.getChild(1);
                final Node third = root.getChild(2);
                if (first.belongsToGroup(AdapterTest.STR_EXPRESSION)
                    && second.belongsToGroup(AdapterTest.STR_LITERAL)
                    && AdditionConverter.EXPECTED_OPERATOR.equals(second.getData())
                    && third.belongsToGroup(AdapterTest.STR_EXPRESSION)) {
                    final Builder builder = factory.createBuilder(AdapterTest.STR_ADDITION);
                    builder.setChildrenList(Arrays.asList(first, third));
                    result = builder.createNode();
                }
            }
            return result;
        }
    }
}
