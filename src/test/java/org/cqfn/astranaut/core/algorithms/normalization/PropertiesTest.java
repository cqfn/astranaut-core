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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Properties} class.
 * @since 2.0.0
 */
class PropertiesTest {
    @Test
    void testPropertiesList() {
        final Node original = LittleTrees.createIntegerLiteral(0);
        final Map<String, String> expected = original.getProperties();
        Assertions.assertFalse(expected.isEmpty());
        final Node normalized = new NormalizedNode(original);
        Assertions.assertTrue(normalized.getChild(0) instanceof Properties);
        final Properties properties = (Properties) normalized.getChild(0);
        final Map<String, String> actual = properties.collectAllProperties();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testBuilder() {
        final Builder builder = new Properties(Collections.emptyMap()).createBuilder();
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertFalse(builder.setData("abc"));
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(
            builder.setChildrenList(Collections.singletonList(DummyNode.INSTANCE))
        );
        final Map<String, String> expected = new TreeMap<>();
        expected.put("color", "red");
        expected.put("language", "java");
        final List<Node> children = new ArrayList<>(expected.size());
        for (final Map.Entry<String, String> entry : expected.entrySet()) {
            children.add(new Property(entry.getKey(), entry.getValue()));
        }
        Assertions.assertTrue(builder.setChildrenList(children));
        Assertions.assertTrue(builder.isValid());
        final Node properties = builder.createNode();
        Assertions.assertTrue(properties instanceof Properties);
        final Map<String, String> actual = ((Properties) properties).collectAllProperties();
        Assertions.assertEquals(expected, actual);
    }
}
