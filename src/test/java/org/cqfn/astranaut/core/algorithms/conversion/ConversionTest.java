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

import java.util.Arrays;
import java.util.Optional;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.example.converters.AdditionConverter;
import org.cqfn.astranaut.core.example.green.GreenFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering conversion algorithms.
 * @since 2.0.0
 */
class ConversionTest {
    @Test
    void addition() {
        final Node left = LittleTrees.createIntegerLiteral(2);
        final Node right = LittleTrees.createIntegerLiteral(3);
        final Node operator = DraftNode.create("Operator<'+'>");
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("Root");
        ctor.setChildrenList(Arrays.asList(left, operator, right));
        final Node root = ctor.createNode();
        final Converter converter = AdditionConverter.INSTANCE;
        final Optional<ConversionResult> result =
            converter.convert(root, 0, GreenFactory.INSTANCE);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(0, result.get().getStartIndex());
        Assertions.assertEquals(3, result.get().getConsumed());
        Assertions.assertEquals(3, result.get().getNextIndex());
        Assertions.assertEquals("2 + 3", result.get().getNode().toString());
    }
}
