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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.algorithms.ExtNodeCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ExtNode} class.
 * @since 2.0.0
 */
class ExtNodeTest {
    @Test
    void testBaseInterface() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node original = DraftNode.create("A(B,C,D)", nodes);
        final Node alpha = nodes.get("A").iterator().next();
        final Node beta = nodes.get("B").iterator().next();
        final Node gamma = nodes.get("C").iterator().next();
        final Node delta = nodes.get("D").iterator().next();
        final ExtNode ext = new ExtNodeCreator().create(original);
        Assertions.assertTrue(ext.deepCompare(original));
        Assertions.assertSame(alpha, ext.getPrototype());
        Assertions.assertSame(ext, ext.getExtChild(0).getParent());
        Assertions.assertSame(alpha, ext.getExtChild(1).getParentPrototype());
        Assertions.assertNull(ext.getExtChild(0).getLeft());
        Assertions.assertSame(ext.getExtChild(0), ext.getExtChild(1).getLeft());
        Assertions.assertSame(gamma, ext.getExtChild(1).getPrototype());
        Assertions.assertSame(beta, ext.getExtChild(1).getLeftPrototype());
        Assertions.assertSame(ext.getExtChild(2), ext.getExtChild(1).getRight());
        Assertions.assertSame(delta, ext.getExtChild(1).getRightPrototype());
        Assertions.assertEquals(original.toString(), ext.toString());
    }
}
