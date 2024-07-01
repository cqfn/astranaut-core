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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.ChildDescriptor;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.base.Type;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link JsonSerializer} class.
 *
 * @since 1.0.2
 */
class JsonSerializerTest {
    /**
     * The type IntegerLiteral.
     */
    private static final String INT_LITERAL = "IntegerLiteral";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/json/";

    /**
     * Test for a tree serialization to a JSON string.
     */
    @Test
    void testSerializationToString() {
        final Tree tree = Tree.createDraft("TestNode<\"value\">");
        final boolean result = this.serializeAndCompare(
            tree,
            "serialization_to_string_expected.txt"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test for a tree serialization where language is specified.
     */
    @Test
    void testSerializationWithLanguageSpecified() {
        final Tree tree = new Tree(new TestNodeWithTypeWithLanguage());
        final boolean result = this.serializeAndCompare(
            tree,
            "serialization_language_specified.json"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test for a tree serialization to a JSON string.
     */
    @Test
    void testSerializationDiffTree() {
        final boolean result = this.serializeAndCompare(
            LittleTrees.createTreeWithDeleteAction(),
            "tree_containing_delete_action.json"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test for a tree serialization to a JSON string.
     */
    @Test
    void testSerializationPattern() {
        final boolean result = this.serializeAndCompare(
            LittleTrees.createPatternWithHole(),
            "pattern_with_hole.json"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test for a tree serialization to a JSON file.
     * @param temp A temporary directory
     */
    @Test
    void testSerializationToFile(@TempDir final Path temp) {
        final Tree tree = this.createSampleTree();
        final JsonSerializer serializer = new JsonSerializer(tree);
        boolean oops = false;
        String expected = "";
        String result = "";
        final Path actual = temp.resolve("result.json");
        try {
            serializer.serializeToFile(actual.toString());
            expected =
                new FilesReader(
                    JsonSerializerTest.TESTS_PATH.concat("serialization_to_file_expected.json")
                ).readAsString();
            result = new FilesReader(actual.toString()).readAsString();
        } catch (final IOException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test for exception while writing to a JSON file.
     */
    @Test
    void testFilesWriterException() {
        final Tree tree = EmptyTree.INSTANCE;
        final JsonSerializer serializer = new JsonSerializer(tree);
        final boolean result = serializer.serializeToFile("/");
        Assertions.assertFalse(result);
    }

    /**
     * Create a simple tree for testing.
     * @return Tree
     */
    private Tree createSampleTree() {
        final DraftNode.Constructor addition = new DraftNode.Constructor();
        addition.setName("Addition");
        final DraftNode.Constructor left = new DraftNode.Constructor();
        left.setName(JsonSerializerTest.INT_LITERAL);
        left.setData("2");
        final DraftNode.Constructor right = new DraftNode.Constructor();
        right.setName(JsonSerializerTest.INT_LITERAL);
        right.setData("3");
        addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
        return new Tree(addition.createNode());
    }

    /**
     * Serializes syntax tree and compares the result to some sample file.
     * @param tree Syntax tree
     * @param filename File name
     * @return Checking result, {@code true} if the result is obtained and matches the sample
     */
    private boolean serializeAndCompare(final Tree tree, final String filename) {
        final JsonSerializer serializer = new JsonSerializer(tree);
        final String result = serializer.serialize().replace("\r", "");
        boolean oops = false;
        String expected = "";
        try {
            expected =
                new FilesReader(
                    JsonSerializerTest.TESTS_PATH.concat(filename)
                ).readAsString().replace("\r", "");
        } catch (final IOException exception) {
            oops = true;
        }
        return !oops && expected.equals(result);
    }

    /**
     * Some type where language is specified.
     *
     * @since 1.1.0
     */
    private static class TestTypeWithLanguage implements Type {
        @Override
        public String getName() {
            return "GandalfTheGrey";
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getHierarchy() {
            return Collections.singletonList(this.getName());
        }

        @Override
        public Map<String, String> getProperties() {
            return new MapUtils<String, String>().put("language", "elven").make();
        }

        @Override
        public Builder createBuilder() {
            return null;
        }
    }

    /**
     * Some node which has a type where language is specified.
     *
     * @since 1.1.0
     */
    private static class TestNodeWithTypeWithLanguage implements Node {
        @Override
        public Fragment getFragment() {
            return EmptyFragment.INSTANCE;
        }

        @Override
        public Type getType() {
            return new TestTypeWithLanguage();
        }

        @Override
        public String getData() {
            return "Abracadabra";
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public Node getChild(final int index) {
            return null;
        }
    }
}
