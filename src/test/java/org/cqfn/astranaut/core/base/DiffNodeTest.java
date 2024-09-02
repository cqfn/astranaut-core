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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.example.green.GreenFactory;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.core.utils.JsonDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering difference node, i.e. {@link DiffNode} class.
 *
 * @since 1.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class DiffNodeTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/json/";

    /**
     * File name with tree before 'Delete' action.
     */
    private static final String TREE_BEFO_DELETE = "before_delete_action.json";

    /**
     * File name with tree after 'Delete' action.
     */
    private static final String TREE_AFTER_DELETE = "after_delete_action.json";

    /**
     * File name with tree before 'Replace' action.
     */
    private static final String TREE_BEFORE_REPL = "before_replace_action.json";

    /**
     * File name with tree after 'Replace' action.
     */
    private static final String TREE_AFTER_REPL = "after_replace_action.json";

    /**
     * File name with tree containing 'Insert' action.
     */
    private static final String TREE_WITH_INSERT = "tree_containing_insert_action.json";

    /**
     * File name with tree containing 'Replace' action.
     */
    private static final String TREE_WITH_REPLACE = "tree_containing_replace_action.json";

    /**
     * File name with tree containing 'Delete' action.
     */
    private static final String TREE_WITH_DELETE = "tree_containing_delete_action.json";

    @Test
    void testBaseInterface() {
        final Node original = DraftNode.create("A<\"test\">(B,C,D,E,F)");
        final DiffNode diff = new DiffNode(original);
        Assertions.assertSame(diff.getPrototype(), original);
        Assertions.assertSame(diff.getFragment(), original.getFragment());
        Assertions.assertEquals(diff.getData(), original.getData());
        Assertions.assertEquals(diff.getChildCount(), original.getChildCount());
        Assertions.assertThrows(UnsupportedOperationException.class, diff::createBuilder);
    }

    @Test
    void testInsertGetBefore() {
        final Node root = this.loadTree(DiffNodeTest.TREE_WITH_INSERT);
        Assertions.assertTrue(root instanceof DiffNode);
        final DiffNode diff = (DiffNode) root;
        final Node actual = diff.getBefore();
        Assertions.assertNotEquals(DummyNode.INSTANCE, actual);
        final Node expected = this.loadTree(DiffNodeTest.TREE_AFTER_DELETE);
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void testInsertGetAfter() {
        final Node root = this.loadTree(DiffNodeTest.TREE_WITH_INSERT);
        Assertions.assertTrue(root instanceof DiffNode);
        final DiffNode diff = (DiffNode) root;
        final Node actual = diff.getAfter();
        Assertions.assertNotEquals(DummyNode.INSTANCE, actual);
        final Node expected = this.loadTree(DiffNodeTest.TREE_BEFO_DELETE);
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void testReplace() {
        final Node root = this.loadTree(DiffNodeTest.TREE_WITH_REPLACE);
        Assertions.assertTrue(root instanceof DiffNode);
        final DiffNode diff = (DiffNode) root;
        final Node before = diff.getBefore();
        Assertions.assertNotEquals(DummyNode.INSTANCE, before);
        Assertions.assertTrue(
            before.deepCompare(this.loadTree(DiffNodeTest.TREE_BEFORE_REPL))
        );
        final Node after = diff.getAfter();
        Assertions.assertNotEquals(DummyNode.INSTANCE, after);
        Assertions.assertTrue(
            after.deepCompare(this.loadTree(DiffNodeTest.TREE_AFTER_REPL))
        );
    }

    @Test
    void testDeleteGetBefore() {
        final Node root = this.loadTree(DiffNodeTest.TREE_WITH_DELETE);
        Assertions.assertTrue(root instanceof DiffNode);
        final DiffNode diff = (DiffNode) root;
        final Node actual = diff.getBefore();
        Assertions.assertNotEquals(DummyNode.INSTANCE, actual);
        final Node expected = this.loadTree(DiffNodeTest.TREE_BEFO_DELETE);
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void testDeleteGetAfter() {
        final Node root = this.loadTree(DiffNodeTest.TREE_WITH_DELETE);
        Assertions.assertTrue(root instanceof DiffNode);
        final DiffNode diff = (DiffNode) root;
        final Node actual = diff.getAfter();
        Assertions.assertNotEquals(DummyNode.INSTANCE, actual);
        final Node expected = this.loadTree(DiffNodeTest.TREE_AFTER_DELETE);
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void testInsertNodeFirst() {
        final Node first = LittleTrees.createReturnStatement(null);
        final Node second = LittleTrees.wrapExpressionWithStatement(
            LittleTrees.createAssignment(
                LittleTrees.createVariable("x"),
                LittleTrees.createIntegerLiteral(0)
            )
        );
        final Node before = LittleTrees.createStatementBlock(first);
        final Node after = LittleTrees.createStatementBlock(second, first);
        final DiffNode diff = new DiffNode(before);
        final boolean result = diff.insertNodeAfter(second, null);
        Assertions.assertTrue(result);
        Assertions.assertTrue(before.deepCompare(diff.getBefore()));
        Assertions.assertTrue(after.deepCompare(diff.getAfter()));
    }

    @Test
    void insertAndDelete() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final DiffNode diff = new DiffNode(DraftNode.create("A(B,C,D)", nodes));
        Assertions.assertTrue(diff.deleteNode(nodes.get("B").iterator().next()));
        Assertions.assertTrue(
            diff.insertNodeAfter(DraftNode.create("E"), nodes.get("C").iterator().next())
        );
        Assertions.assertEquals("A(C, E, D)", diff.getAfter().toString());
    }

    @Test
    void testInsertNodeFails() {
        final DiffNode diff = new DiffNode(
            LittleTrees.createStatementBlock(
                LittleTrees.createReturnStatement(null)
            )
        );
        final boolean result = diff.insertNodeAfter(
            LittleTrees.createVariable("x"),
            LittleTrees.createVariable("y")
        );
        Assertions.assertFalse(result);
    }

    @Test
    void testParentReference() {
        final DiffNode root = new DiffNode(DraftNode.create("A(B,C)"));
        final Node child = root.getChild(0);
        Assertions.assertTrue(child instanceof DiffNode);
        Assertions.assertSame(((DiffNode) child).getParent(), root);
    }

    @Test
    void testDiffNodeAsString() {
        final String description = "X(Y, Z)";
        final DiffNode root = new DiffNode(DraftNode.create(description));
        Assertions.assertEquals(description, root.toString());
    }

    @Test
    void getBranchFromBadNode() {
        Assertions.assertSame(
            DummyNode.INSTANCE,
            new DiffNode(new TestNode(TestCase.NULL_BUILDER)).getBefore()
        );
        Assertions.assertSame(
            DummyNode.INSTANCE,
            new DiffNode(new TestNode(TestCase.BAD_DATA)).getBefore()
        );
        Assertions.assertSame(
            DummyNode.INSTANCE,
            new DiffNode(new TestNode(TestCase.BAD_CHILDREN)).getBefore()
        );
        Assertions.assertSame(
            DummyNode.INSTANCE,
            new DiffNode(new TestNode(TestCase.INVALID_BUILDER)).getBefore()
        );
    }

    @Test
    void replaceNonExistingNode() {
        final DiffNode diff = new DiffNode(DraftNode.create("X"));
        Assertions.assertFalse(diff.replaceNode(-1, DummyNode.INSTANCE));
        Assertions.assertFalse(diff.replaceNode(1, DummyNode.INSTANCE));
        Assertions.assertFalse(diff.replaceNode(DummyNode.INSTANCE, DummyNode.INSTANCE));
    }

    @Test
    void replaceAlreadyReplacedNode() {
        final DiffNode diff = new DiffNode(DraftNode.create("X(A)"));
        Assertions.assertTrue(diff.replaceNode(0, DraftNode.create("B")));
        Assertions.assertFalse(diff.replaceNode(0, DraftNode.create("C")));
    }

    @Test
    void deleteNonExistingNode() {
        final DiffNode diff = new DiffNode(DraftNode.create("X"));
        Assertions.assertFalse(diff.deleteNode(-1));
        Assertions.assertFalse(diff.deleteNode(1));
        Assertions.assertFalse(diff.deleteNode(DummyNode.INSTANCE));
    }

    @Test
    void deleteAlreadyDeletedNode() {
        final DiffNode diff = new DiffNode(DraftNode.create("X(A)"));
        Assertions.assertTrue(diff.deleteNode(0));
        Assertions.assertFalse(diff.deleteNode(0));
    }

    /**
     * Returns content of the specified file.
     * @param name The name of the file
     * @return The file content
     */
    private String getFileContent(final String name) {
        final String file = DiffNodeTest.TESTS_PATH.concat(name);
        final String source = new FilesReader(file).readAsStringNoExcept();
        Assertions.assertFalse(source.isEmpty());
        return source;
    }

    /**
     * Loads a tree from a JSON file.
     * @param name The name of the file
     * @return Root node
     */
    private Node loadTree(final String name) {
        final String source = this.getFileContent(name);
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> GreenFactory.INSTANCE
        );
        final Tree tree = deserializer.convert();
        final Node root = tree.getRoot();
        Assertions.assertNotEquals(DummyNode.INSTANCE, root);
        return root;
    }

    /**
     * List of test cases.
     * @since 2.0.0
     */
    private enum TestCase {
        /**
         * Case: node does not have a builder.
         */
        NULL_BUILDER,

        /**
         * Case: builder is not accepting data.
         */
        BAD_DATA,

        /**
         * Case: builder won't accept children.
         */
        BAD_CHILDREN,

        /**
         * Case: builder is not valid.
         */
        INVALID_BUILDER
    }

    /**
     * Custom node for test purposes.
     * @since 2.0.0
     */
    private static final class TestNode extends NodeAndType {
        /**
         * Test case.
         */
        private final TestCase test;

        /**
         * Constructor.
         * @param test Test case
         */
        private TestNode(final TestCase test) {
            this.test = test;
        }

        @Override
        public String getData() {
            return "";
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public Node getChild(final int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public String getName() {
            return "Test";
        }

        @Override
        public Builder createBuilder() {
            final Builder builder;
            if (this.test == TestCase.NULL_BUILDER) {
                builder = null;
            } else {
                builder = new TestBuilder(this.test);
            }
            return builder;
        }
    }

    /**
     * Custom builder for test purposes.
     * @since 2.0.0
     */
    private static final class TestBuilder implements Builder {
        /**
         * Test case.
         */
        private final TestCase test;

        /**
         * Constructor.
         * @param test Test case
         */
        private TestBuilder(final TestCase test) {
            this.test = test;
        }

        @Override
        public void setFragment(final Fragment fragment) {
            this.getClass();
        }

        @Override
        public boolean setData(final String str) {
            return this.test != TestCase.BAD_DATA;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            return this.test != TestCase.BAD_CHILDREN;
        }

        @Override
        public boolean isValid() {
            return this.test != TestCase.INVALID_BUILDER;
        }

        @Override
        public Node createNode() {
            throw new UnsupportedOperationException();
        }
    }
}
