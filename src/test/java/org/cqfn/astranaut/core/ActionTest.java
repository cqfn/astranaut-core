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
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering actions, i.e. {@link Action} interface and inherited classes.
 *
 * @since 1.1.0
 */
class ActionTest {
    /**
     * The 'Insert' type.
     */
    private static final String INSERT_TYPE = "Insert";

    /**
     * The 'Replace' type.
     */
    private static final String REPLACE_TYPE = "Replace";

    /**
     * The 'Delete' type.
     */
    private static final String DELETE_TYPE = "Delete";

    /**
     * The 'color' property.
     */
    private static final String COLOR_PROPERTY = "color";

    /**
     * The expected color property.
     */
    private static final String EXPECTED_COLOR = "blue";

    /**
     * Testing {@link Insert} action.
     */
    @Test
    void testInsertAction() {
        final Node inserted = LittleTrees.createReturnStatement(null);
        final Action action = new Insert(inserted);
        Assertions.assertEquals(EmptyFragment.INSTANCE, action.getFragment());
        Assertions.assertEquals("", action.getData());
        Assertions.assertEquals(1, action.getChildCount());
        Assertions.assertEquals(inserted, action.getChild(0));
        Assertions.assertNull(action.getChild(1));
        final Type type = action.getType();
        Assertions.assertEquals(ActionTest.INSERT_TYPE, type.getName());
        final List<ChildDescriptor> descriptors = type.getChildTypes();
        Assertions.assertFalse(descriptors.isEmpty());
        final List<String> hierarchy = type.getHierarchy();
        Assertions.assertFalse(hierarchy.isEmpty());
        Assertions.assertEquals(type.getName(), hierarchy.get(0));
        Assertions.assertEquals(
            ActionTest.EXPECTED_COLOR,
            type.getProperty(ActionTest.COLOR_PROPERTY)
        );
        final Builder builder = type.createBuilder();
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(builder.setData("abracadabra"));
        Assertions.assertFalse(builder.isValid());
        Node created = builder.createNode();
        Assertions.assertEquals(EmptyTree.INSTANCE, created);
        Assertions.assertTrue(builder.setChildrenList(Collections.singletonList(inserted)));
        Assertions.assertFalse(builder.setChildrenList(Arrays.asList(inserted, inserted)));
        created = builder.createNode();
        Assertions.assertEquals(ActionTest.INSERT_TYPE, created.getTypeName());
    }

    /**
     * Testing {@link Replace} action.
     */
    @Test
    void testReplaceAction() {
        final Node before = LittleTrees.createVariable("x");
        final Node after = LittleTrees.createIntegerLiteral(0);
        final Action action = new Replace(before, after);
        Assertions.assertEquals(EmptyFragment.INSTANCE, action.getFragment());
        Assertions.assertEquals("", action.getData());
        Assertions.assertEquals(2, action.getChildCount());
        Assertions.assertEquals(before, action.getChild(0));
        Assertions.assertEquals(after, action.getChild(1));
        Assertions.assertNull(action.getChild(2));
        final Type type = action.getType();
        Assertions.assertEquals(ActionTest.REPLACE_TYPE, type.getName());
        final List<ChildDescriptor> descriptors = type.getChildTypes();
        Assertions.assertFalse(descriptors.isEmpty());
        final List<String> hierarchy = type.getHierarchy();
        Assertions.assertFalse(hierarchy.isEmpty());
        Assertions.assertEquals(type.getName(), hierarchy.get(0));
        Assertions.assertEquals(
            ActionTest.EXPECTED_COLOR,
            type.getProperty(ActionTest.COLOR_PROPERTY)
        );
        final Builder builder = type.createBuilder();
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(builder.setData("it's a kind of magic"));
        Assertions.assertFalse(builder.isValid());
        Node created = builder.createNode();
        Assertions.assertEquals(EmptyTree.INSTANCE, created);
        Assertions.assertTrue(builder.setChildrenList(Arrays.asList(before, after)));
        Assertions.assertFalse(builder.setChildrenList(Collections.singletonList(before)));
        created = builder.createNode();
        Assertions.assertEquals(ActionTest.REPLACE_TYPE, created.getTypeName());
    }

    /**
     * Testing {@link Delete} action.
     */
    @Test
    void testDeleteAction() {
        final Node deleted = LittleTrees.createReturnStatement(null);
        final Action action = new Delete(deleted);
        Assertions.assertEquals(EmptyFragment.INSTANCE, action.getFragment());
        Assertions.assertEquals("", action.getData());
        Assertions.assertEquals(1, action.getChildCount());
        Assertions.assertEquals(deleted, action.getChild(0));
        Assertions.assertNull(action.getChild(1));
        final Type type = action.getType();
        Assertions.assertEquals(ActionTest.DELETE_TYPE, type.getName());
        final List<ChildDescriptor> descriptors = type.getChildTypes();
        Assertions.assertFalse(descriptors.isEmpty());
        final List<String> hierarchy = type.getHierarchy();
        Assertions.assertFalse(hierarchy.isEmpty());
        Assertions.assertEquals(type.getName(), hierarchy.get(0));
        Assertions.assertEquals(
            ActionTest.EXPECTED_COLOR,
            type.getProperty(ActionTest.COLOR_PROPERTY)
        );
        final Builder builder = type.createBuilder();
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(builder.setData("I hate syntax trees"));
        Assertions.assertFalse(builder.isValid());
        Node created = builder.createNode();
        Assertions.assertEquals(EmptyTree.INSTANCE, created);
        Assertions.assertTrue(builder.setChildrenList(Collections.singletonList(deleted)));
        Assertions.assertFalse(builder.setChildrenList(Arrays.asList(deleted, deleted)));
        created = builder.createNode();
        Assertions.assertEquals(ActionTest.DELETE_TYPE, created.getTypeName());
    }
}
