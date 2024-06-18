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
package org.cqfn.astranaut.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link DraftNode} class.
 *
 * @since 1.1.0
 */
class DraftNodeTest {
    /**
     * Testing {@link  DraftNode#createByDescription(String)} and
     * {@link  DraftNode#toString()} methods.
     */
    @Test
    void createAndSerialize() {
        final String[] cases = {
            "X",
            "X(A)",
            "X(A, B, C)",
            "X(A(B, C))",
            "Addition(Expression, Expression)",
            "Node<\"data\">",
        };
        for (final String test : cases) {
            Assertions.assertTrue(DraftNodeTest.createAndSerialize(test));
        }
    }

    /**
     * Testing {@link DraftNode.Constructor} class.
     */
    @Test
    void constructorTest() {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        Assertions.assertEquals("", ctor.toString());
        final String name = "Name";
        ctor.setName(name);
        Assertions.assertEquals(name, ctor.toString());
        ctor.setData("data");
        Assertions.assertEquals("Name<\"data\">", ctor.toString());
        final List<Node> children = Arrays.asList(
            DraftNode.createByDescription("A"),
            DraftNode.createByDescription("B"),
            DraftNode.createByDescription("C")
        );
        ctor.setChildrenList(children);
        final String serialized = "Name<\"data\">(A, B, C)";
        Assertions.assertEquals(serialized, ctor.toString());
        Assertions.assertEquals(serialized, ctor.createNode().toString());
    }

    /**
     * Testing {@link DraftNode.Constructor#addChild(Node)} method.
     */
    @Test
    void addNodeTest() {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("X");
        ctor.addChild(DraftNode.createByDescription("A"));
        final String serialized = "X(A)";
        Assertions.assertEquals(serialized, ctor.createNode().toString());
    }

    /**
     * Testing {@link DraftNode#createByDescription(String, Map)} method.
     */
    @Test
    void testExtendedDescriptorProcessor() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node root = DraftNode.createByDescription("X(A,A,B(C,D))", nodes);
        Assertions.assertSame(
            root,
            nodes.computeIfAbsent(
                "X",
                s -> Collections.singleton(EmptyTree.INSTANCE)
            ).iterator().next()
        );
        Assertions.assertEquals(
            2,
            nodes.computeIfAbsent(
                "A",
                s -> Collections.singleton(EmptyTree.INSTANCE)
            ).size()
        );
        Assertions.assertEquals(
            2,
            nodes.computeIfAbsent(
                "B",
                s -> Collections.singleton(EmptyTree.INSTANCE)
            ).iterator().next().getChildCount()
        );
    }

    @Test
    void typeTest() {
        final Node node = DraftNode.createByDescription("A");
        final Type type = node.getType();
        Assertions.assertEquals("A", type.getName());
        Assertions.assertEquals(0, type.getChildTypes().size());
        Assertions.assertEquals(1, type.getHierarchy().size());
        Assertions.assertEquals("A", type.getHierarchy().get(0));
        final Builder builder = type.createBuilder();
        final Node clone = builder.createNode();
        Assertions.assertTrue(node.deepCompare(clone));
    }

    @Test
    void wrongConstructionTest() {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        boolean oops = false;
        try {
            ctor.createNode();
        } catch (final IllegalStateException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    /**
     * Testing {@link  DraftNode#createByDescription(String)} and
     * {@link  DraftNode#toString()} methods (testing of one case).
     * @param description Syntax tree description
     * @return Testing result ({@code true} if passed)
     */
    private static boolean createAndSerialize(final String description) {
        final Node node = DraftNode.createByDescription(description);
        final String serialized = node.toString();
        return description.equals(serialized);
    }
}
