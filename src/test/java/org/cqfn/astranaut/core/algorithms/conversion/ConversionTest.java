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
package org.cqfn.astranaut.core.algorithms.conversion;

import java.util.Collections;
import java.util.Optional;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.example.converters.AdditionConverter;
import org.cqfn.astranaut.core.example.converters.IntegerConverter;
import org.cqfn.astranaut.core.example.green.GreenFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering conversion algorithms.
 * @since 2.0.0
 */
class ConversionTest {
    /**
     * Root node name (for test purposes).
     */
    private static final String ROOT_NODE_NAME = "Root";

    /**
     * Node representing the '+' operator.
     */
    private static final Node OPERATOR_PLUS = DraftNode.create("Operator<'+'>");

    @Test
    void extractingChildren() {
        final Node root = DraftNode.create(
            ConversionTest.ROOT_NODE_NAME,
            "",
            LittleTrees.createIntegerLiteral(2),
            ConversionTest.OPERATOR_PLUS,
            LittleTrees.createIntegerLiteral(3)
        );
        final Converter converter = AdditionConverter.INSTANCE;
        final Optional<ConversionResult> result =
            converter.convert(root.getChildrenList(), 0, GreenFactory.INSTANCE);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(3, result.get().getConsumed());
        Assertions.assertEquals("2 + 3", result.get().getNode().toString());
    }

    @Test
    void extractingData() {
        final Node before = DraftNode.create("int<'7'>");
        final Node root = DraftNode.create(ConversionTest.ROOT_NODE_NAME, "", before);
        final Converter converter = IntegerConverter.INSTANCE;
        final Optional<ConversionResult> result =
            converter.convert(root.getChildrenList(), 0, GreenFactory.INSTANCE);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("7", result.get().getNode().toString());
    }

    @Test
    void leftToRight() {
        final Node root = DraftNode.create(
            ConversionTest.ROOT_NODE_NAME,
            "",
            LittleTrees.createIntegerLiteral(2),
            ConversionTest.OPERATOR_PLUS,
            LittleTrees.createIntegerLiteral(3),
            ConversionTest.OPERATOR_PLUS,
            LittleTrees.createIntegerLiteral(4)
        );
        final Transformer transformer = new Transformer(
            Collections.singletonList(AdditionConverter.INSTANCE),
            GreenFactory.INSTANCE
        );
        final Tree result = transformer.transform(new Tree(root));
        Assertions.assertEquals(1, result.getRoot().getChildCount());
    }
}
