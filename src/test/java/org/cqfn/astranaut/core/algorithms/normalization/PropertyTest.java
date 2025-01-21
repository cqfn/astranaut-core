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
package org.cqfn.astranaut.core.algorithms.normalization;

import java.util.Collections;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Property} class.
 * @since 2.0.0
 */
class PropertyTest {
    @Test
    void testBaseMethods() {
        final String name = "color";
        final String before = "red";
        final String after = "blue";
        final Node property = new Property(name, before);
        Assertions.assertEquals(name, property.getTypeName());
        Assertions.assertEquals(before, property.getData());
        Assertions.assertEquals(0, property.getChildCount());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> property.getChild(0));
        final Builder builder = property.getType().createBuilder();
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(after));
        Assertions.assertFalse(
            builder.setChildrenList(Collections.singletonList(DummyNode.INSTANCE))
        );
        Assertions.assertTrue(builder.setChildrenList(Collections.emptyList()));
        Assertions.assertTrue(builder.isValid());
        final Node created = builder.createNode();
        Assertions.assertEquals(name, created.getTypeName());
        Assertions.assertEquals(after, created.getData());
    }
}
