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
package org.cqfn.astranaut.core.utils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DefaultFactory;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.base.Type;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.example.green.Addition;
import org.cqfn.astranaut.core.example.green.GreenFactory;
import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link JsonDeserializer} class.
 * @since 1.0.2
 */
@SuppressWarnings("PMD.TooManyMethods")
class JsonDeserializerTest {
    /**
     * The "Addition" type.
     */
    private static final String ADD = "Addition";

    /**
     * The "IntegerLiteral" type.
     */
    private static final String INT_LITERAL = "IntegerLiteral";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/json/";

    /**
     * Small syntax tree description.
     */
    private static final String SMALL_TREE = "{ \"root\": { \"type\": \"Example\" } }";

    /**
     * Name of the file containing simple tree.
     */
    private static final String SIMPLE_TREE_NAME = "test_deserialization.json";

    @Test
    void testDeserialization() {
        final String source = this.getFileContent(JsonDeserializerTest.SIMPLE_TREE_NAME);
        final Map<String, Type> types = new TreeMap<>();
        types.put(JsonDeserializerTest.ADD, Addition.TYPE);
        types.put(JsonDeserializerTest.INT_LITERAL, IntegerLiteral.TYPE);
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> new DefaultFactory(types)
        );
        final Tree result = deserializer.convert();
        final Node root = result.getRoot();
        Assertions.assertEquals(JsonDeserializerTest.ADD, root.getTypeName());
        Assertions.assertEquals("", root.getData());
        Assertions.assertEquals(2, root.getChildCount());
        final Node first = root.getChild(0);
        Assertions.assertEquals(JsonDeserializerTest.INT_LITERAL, first.getTypeName());
        Assertions.assertEquals("2", first.getData());
        Assertions.assertEquals(0, first.getChildCount());
        final Node second = root.getChild(1);
        Assertions.assertEquals(JsonDeserializerTest.INT_LITERAL, second.getTypeName());
        Assertions.assertEquals("3", second.getData());
        Assertions.assertEquals(0, second.getChildCount());
    }

    @Test
    void loadIntoDraftNode() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            JsonDeserializerTest.SMALL_TREE,
            language -> DefaultFactory.EMPTY
        );
        final Tree tree = deserializer.convert();
        Assertions.assertNotNull(tree);
        Assertions.assertEquals("Example", tree.getRoot().getTypeName());
    }

    @Test
    void loadTreeWithInsertAction() {
        final String source = this.getFileContent("tree_containing_insert_action.json");
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> GreenFactory.INSTANCE
        );
        final Tree actual = deserializer.convert();
        final Tree expected = LittleTrees.createTreeWithInsertAction();
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void loadTreeWithInsertFirstAction() {
        final String source = this.getFileContent("tree_containing_insert_first_action.json");
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> GreenFactory.INSTANCE
        );
        final Tree actual = deserializer.convert();
        final Tree expected = LittleTrees.createTreeWithInsertFirstAction();
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void loadTreeWithDeleteAction() {
        final String source = this.getFileContent("tree_containing_delete_action.json");
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> GreenFactory.INSTANCE
        );
        final Tree actual = deserializer.convert();
        final Tree expected = LittleTrees.createTreeWithDeleteAction();
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void loadPatternWithHole() {
        final String source = this.getFileContent("pattern_with_hole.json");
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> GreenFactory.INSTANCE
        );
        final Tree actual = deserializer.convert();
        final Tree expected = LittleTrees.createPatternWithHole();
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    @Test
    void deserializeInvalidJson() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            ".[]test",
            language -> DefaultFactory.EMPTY
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(EmptyTree.INSTANCE, result);
    }

    @Test
    void deserializeJsonWithWrongFormat() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            "null",
            language -> DefaultFactory.EMPTY
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(EmptyTree.INSTANCE, result);
    }

    @Test
    void deserializeWithNullFactory() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            JsonDeserializerTest.SMALL_TREE,
            language -> null
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(EmptyTree.INSTANCE, result);
    }

    @Test
    void deserializeWithFactoryThatProducesNullBuilder() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            JsonDeserializerTest.SMALL_TREE,
            language -> new Factory() {
                @Override
                public Type getType(final String name) {
                    return null;
                }

                @Override
                public Builder createBuilder(final String name) {
                    return null;
                }
            }
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(DummyNode.INSTANCE, result.getRoot());
    }

    @Test
    void deserializeWithFactoryThatProducesInvalidBuilder() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            JsonDeserializerTest.SMALL_TREE,
            language -> new Factory() {
                @Override
                public Type getType(final String name) {
                    return null;
                }

                @Override
                public Builder createBuilder(final String name) {
                    return new InvalidBuilder();
                }
            }
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(DummyNode.INSTANCE, result.getRoot());
    }

    @Test
    void deserializeWithFactoryThatProducesBuilderDoesNotAcceptChildren() {
        final String source = this.getFileContent(JsonDeserializerTest.SIMPLE_TREE_NAME);
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> new Factory() {
                @Override
                public Type getType(final String name) {
                    return null;
                }

                @Override
                public Builder createBuilder(final String name) {
                    return new BuilderDoesNotAcceptChildren(name);
                }
            }
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(DummyNode.INSTANCE, result.getRoot());
    }

    @Test
    void deserializeInvalidHoleWithoutNumber() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            "{ \"root\": { \"type\": \"Hole\", \"prototype\": { \"type\": \"Node\" } } }",
            language -> DefaultFactory.EMPTY
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(DummyNode.INSTANCE, result.getRoot());
    }

    @Test
    void deserializeInvalidHoleWithoutPrototype() {
        final JsonDeserializer deserializer = new JsonDeserializer(
            "{ \"root\": { \"type\": \"Hole\", \"number\": 19 } } }",
            language -> DefaultFactory.EMPTY
        );
        final Tree result = deserializer.convert();
        Assertions.assertSame(DummyNode.INSTANCE, result.getRoot());
    }

    /**
     * Returns content of the specified file.
     * @param name The name of the file
     * @return The file content
     */
    private String getFileContent(final String name) {
        final String file = JsonDeserializerTest.TESTS_PATH.concat(name);
        final String source = new FilesReader(file).readAsStringNoExcept();
        Assertions.assertFalse(source.isEmpty());
        return source;
    }

    /**
     * Builder that doesn't accept children, for testing purposes.
     * @since 2.0.0
     */
    private static final class BuilderDoesNotAcceptChildren implements Builder {
        /**
         * Type name of node to be created.
         */
        private final String name;

        /**
         * Constructor.
         * @param name Type name of node to be created
         */
        private BuilderDoesNotAcceptChildren(final String name) {
            this.name = name;
        }

        @Override
        public void setFragment(final Fragment fragment) {
            this.getClass();
        }

        @Override
        public boolean setData(final String str) {
            return true;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            return false;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Node createNode() {
            return DraftNode.create(this.name);
        }
    }

    /**
     * Invalid builder for test purposes.
     * @since 2.0.0
     */
    private static final class InvalidBuilder implements Builder {
        @Override
        public void setFragment(final Fragment fragment) {
            this.getClass();
        }

        @Override
        public boolean setData(final String str) {
            return true;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            return true;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public Node createNode() {
            throw new UnsupportedOperationException();
        }
    }
}
