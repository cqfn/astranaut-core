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
package org.cqfn.astranaut.core.algorithms.normalization;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link NormalizedNode} class.
 * @since 2.0.0
 */
class NormalizedNodeTest {
    /**
     * The 'color' property.
     */
    private static final String COLOR_PROPERTY = "color";

    @Test
    void testBaseMethods() {
        final Node original = LittleTrees.createAddition(
            LittleTrees.createVariable("x"),
            LittleTrees.createIntegerLiteral(1)
        );
        Assertions.assertNotNull(original.getProperties().get(NormalizedNodeTest.COLOR_PROPERTY));
        final NormalizedNode normalized = new NormalizedNode(original);
        Assertions.assertTrue(normalized.getProperties().isEmpty());
        Assertions.assertSame(original.getFragment(), normalized.getFragment());
        Assertions.assertEquals(original.getTypeName(), normalized.getTypeName());
        Assertions.assertEquals(original.getData(), normalized.getData());
        Assertions.assertEquals(original.getChildCount() + 1, normalized.getChildCount());
        final Node properties = normalized.getChild(0);
        Assertions.assertTrue(properties instanceof  Properties);
        Assertions.assertEquals(original.getProperties().size(), properties.getChildCount());
        Node property = null;
        for (final Node node : properties.getChildrenList()) {
            if (node.getTypeName().equals(NormalizedNodeTest.COLOR_PROPERTY)) {
                property = node;
                break;
            }
        }
        Assertions.assertNotNull(property);
        Assertions.assertEquals(
            original.getProperties().get(NormalizedNodeTest.COLOR_PROPERTY),
            property.getData()
        );
        for (int index = 1; index < normalized.getChildCount(); index = index + 1) {
            final Node left =  original.getChild(index - 1);
            final Node right = normalized.getChild(index);
            Assertions.assertTrue(
                left.getTypeName().equals(right.getTypeName())
                    && left.getData().equals(right.getData())
            );
        }
        Assertions.assertTrue(normalized.toString().startsWith(original.getTypeName()));
        Assertions.assertSame(original, normalized.getPrototype());
    }

    @Test
    void testOriginalWithoutProperties() {
        final Node original = DraftNode.create("A(B,C)");
        Assertions.assertTrue(original.getProperties().isEmpty());
        final Node normalized = new NormalizedNode(original);
        Assertions.assertEquals(original.getChildCount(), normalized.getChildCount());
        for (int index = 0; index < normalized.getChildCount(); index = index + 1) {
            final Node left =  original.getChild(index);
            final Node right = normalized.getChild(index);
            Assertions.assertTrue(
                left.getTypeName().equals(right.getTypeName())
                    && left.getData().equals(right.getData())
            );
        }
    }

    @Test
    void testBuilder() {
        final Node original = LittleTrees.createAddition(
            LittleTrees.createVariable("x"),
            LittleTrees.createIntegerLiteral(1)
        );
        final Node normalized = new NormalizedNode(original);
        final Builder builder = normalized.getType().createBuilder();
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(builder.isValid());
        boolean oops = false;
        try {
            builder.createNode();
        } catch (final IllegalStateException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
        final Node left = LittleTrees.createIntegerLiteral(2);
        Assertions.assertFalse(builder.setChildrenList(Collections.singletonList(left)));
        final Node right = new NormalizedNode(LittleTrees.createIntegerLiteral(3));
        Assertions.assertTrue(builder.setChildrenList(Arrays.asList(left, right)));
        final Map<String, String> map = new TreeMap<>();
        map.put(NormalizedNodeTest.COLOR_PROPERTY, "red");
        final Properties properties = new Properties(map);
        Assertions.assertTrue(builder.setChildrenList(Arrays.asList(properties, left, right)));
        Assertions.assertTrue(builder.isValid());
        final Node created = builder.createNode();
        Assertions.assertNotNull(created);
    }
}
