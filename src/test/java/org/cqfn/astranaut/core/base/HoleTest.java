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

import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link Hole} class.
 *
 * @since 1.1.5
 */
class HoleTest {
    @Test
    void testBaseInterface() {
        final Hole hole = new Hole(IntegerLiteral.TYPE, 1);
        Assertions.assertSame(EmptyFragment.INSTANCE, hole.getFragment());
        Assertions.assertEquals(0, hole.getChildCount());
        Assertions.assertNull(hole.getChild(0));
        Assertions.assertEquals(
            "purple",
            hole.getProperties().getOrDefault("color", "")
        );
        Assertions.assertEquals(1, hole.getNumber());
        final Type type = hole.getType();
        final String typename = "IntegerLiteral";
        Assertions.assertEquals(typename, type.getName());
        Assertions.assertEquals(typename, type.getHierarchy().get(0));
        Assertions.assertFalse(type.getProperties().containsKey("abracadabra"));
        final Builder builder = type.createBuilder();
        builder.setData("0");
        final Node node = builder.createNode();
        Assertions.assertEquals(typename, node.getTypeName());
    }
}
