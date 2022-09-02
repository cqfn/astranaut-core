/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.cqfn.astranaut.core;

import java.util.Arrays;
import org.cqfn.astranaut.core.example.green.Addition;
import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link ConvertibleNode} class.
 *
 * @since 1.0
 */
public class ConvertibleNodeTest {
    /**
     * Testing the transformation from 'typical' node to convertible.
     */
    @Test
    public void transformation() {
        IntegerLiteral.Constructor icr = new IntegerLiteral.Constructor();
        icr.setData("7");
        final Node left = icr.createNode();
        icr = new IntegerLiteral.Constructor();
        icr.setData("11");
        final Node right = icr.createNode();
        final Addition.Constructor acr = new Addition.Constructor();
        acr.setChildrenList(Arrays.asList(left, right));
        final Node addition = acr.createNode();
        final ConvertibleNode convertible = new ConvertibleNode(addition);
        Assertions.assertEquals("Addition", convertible.getType().getName());
        Assertions.assertEquals(2, convertible.getChildCount());
        final ConvertibleNode first = convertible.getConvertibleChild(0);
        Assertions.assertEquals(convertible, first.getParent());
        final Node second = convertible.getChild(1);
        icr = new IntegerLiteral.Constructor();
        icr.setData("13");
        final Node third = icr.createNode();
        final boolean result = convertible.replaceChild(second, third);
        Assertions.assertTrue(result);
    }
}
