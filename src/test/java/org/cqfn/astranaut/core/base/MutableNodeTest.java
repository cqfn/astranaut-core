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

import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link MutableNode} class.
 * @since 1.0
 */
class MutableNodeTest {
    @Test
    void testBaseInterface() {
        final Node left = LittleTrees.createVariable("x");
        final Node right = LittleTrees.createVariable("y");
        final Node original = LittleTrees.createAddition(left, right);
        final MutableNode mutable = new MutableNode(original);
        Assertions.assertTrue(mutable.deepCompare(original));
        Assertions.assertSame(original, mutable.getPrototype());
        Assertions.assertSame(mutable.getMutableChild(0).getParent(), mutable);
        Assertions.assertSame(original.getFragment(), mutable.getFragment());
        Assertions.assertEquals(right.getData(), mutable.getChildrenList().get(1).getData());
        Assertions.assertTrue(mutable.toString().startsWith(original.getTypeName()));
        Assertions.assertFalse(mutable.replaceChild(DummyNode.INSTANCE, DummyNode.INSTANCE));
        final Node substitute = LittleTrees.createIntegerLiteral(3);
        Assertions.assertTrue(mutable.replaceChild(right, substitute));
        final Node after = mutable.getMutableChild(1);
        Assertions.assertTrue(after.deepCompare(substitute));
        Assertions.assertTrue(
            mutable.replaceChild(left, LittleTrees.createIntegerLiteral(2))
        );
        final Node result = mutable.rebuild();
        Assertions.assertFalse(result instanceof MutableNode);
        Assertions.assertTrue(mutable.deepCompare(result));
    }

    @Test
    void testBadTransformation() {
        final Node left = LittleTrees.createIntegerLiteral(2);
        final Node right = LittleTrees.createIntegerLiteral(3);
        final Node original = LittleTrees.createAddition(left, right);
        final MutableNode mutable = new MutableNode(original);
        Assertions.assertTrue(
            mutable.replaceChild(left, LittleTrees.createReturnStatement(null))
        );
        final Node result = mutable.rebuild();
        Assertions.assertSame(DummyNode.INSTANCE, result);
    }
}
