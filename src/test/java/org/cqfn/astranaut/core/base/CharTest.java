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
package org.cqfn.astranaut.core.base;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link Char} class.
 * @since 2.0.0
 */
class CharTest {
    @Test
    void testBaseInterface() {
        final Char.Constructor ctor = new Char.Constructor();
        ctor.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertFalse(ctor.isValid());
        ctor.setValue('a');
        Assertions.assertFalse(ctor.setData(""));
        Assertions.assertFalse(ctor.setData("abc"));
        Assertions.assertFalse(ctor.setData("\0"));
        Assertions.assertTrue(ctor.setData("b"));
        Assertions.assertFalse(ctor.setChildrenList(Collections.singletonList(DummyNode.INSTANCE)));
        Assertions.assertTrue(ctor.setChildrenList(Collections.emptyList()));
        Assertions.assertTrue(ctor.isValid());
        final Char node = (Char) ctor.createNode();
        Assertions.assertSame(EmptyFragment.INSTANCE, node.getFragment());
        final Type type = node.getType();
        Assertions.assertEquals("Char", type.getName());
        Assertions.assertNotNull(type.createBuilder());
        Assertions.assertEquals('b', node.getSymbol());
        Assertions.assertEquals("b", node.getData());
        Assertions.assertEquals(0, node.getChildCount());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> node.getChild(0));
    }
}
