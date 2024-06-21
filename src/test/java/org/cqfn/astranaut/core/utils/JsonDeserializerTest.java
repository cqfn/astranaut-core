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

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.example.green.Addition;
import org.cqfn.astranaut.core.example.green.GreenFactory;
import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.cqfn.astranaut.core.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link JsonDeserializer} class.
 *
 * @since 1.0.2
 */
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
     * Test for a tree deserialization from a JSON string.
     */
    @Test
    void testDeserialization() {
        final String source = this.getFileContent("test_deserialization.json");
        final Map<String, Type> types = new TreeMap<>();
        types.put(JsonDeserializerTest.ADD, Addition.TYPE);
        types.put(JsonDeserializerTest.INT_LITERAL, IntegerLiteral.TYPE);
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> new Factory(types)
        );
        final Node result = deserializer.convert();
        Assertions.assertEquals(JsonDeserializerTest.ADD, result.getTypeName());
        Assertions.assertEquals("", result.getData());
        Assertions.assertEquals(2, result.getChildCount());
        final Node first = result.getChild(0);
        Assertions.assertEquals(JsonDeserializerTest.INT_LITERAL, first.getTypeName());
        Assertions.assertEquals("2", first.getData());
        Assertions.assertEquals(0, first.getChildCount());
        final Node second = result.getChild(1);
        Assertions.assertEquals(JsonDeserializerTest.INT_LITERAL, second.getTypeName());
        Assertions.assertEquals("3", second.getData());
        Assertions.assertEquals(0, second.getChildCount());
    }

    /**
     * Test deserialization of a JSON object that contains a tree not related
     * to some language.
     */
    @Test
    void loadIntoDraftNode() {
        final String source = "{ \"root\": { \"type\": \"Example\" } }";
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> new Factory(Collections.emptyMap())
        );
        final Node node = deserializer.convert();
        Assertions.assertNotNull(node);
        Assertions.assertEquals("Example", node.getTypeName());
    }

    /**
     * Testing the deserialization of a tree that contains actions.
     */
    @Test
    void loadTreeWithActions() {
        final String source = this.getFileContent("tree_containing_delete_action.json");
        final JsonDeserializer deserializer = new JsonDeserializer(
            source,
            language -> GreenFactory.INSTANCE
        );
        final Node actual = deserializer.convert();
        final Node expected = LittleTrees.createTreeWithDeleteAction();
        Assertions.assertTrue(expected.deepCompare(actual));
    }

    /**
     * Returns content of the specified file.
     * @param name The name of the file
     * @return The file content
     */
    private String getFileContent(final String name) {
        final String file = JsonDeserializerTest.TESTS_PATH.concat(name);
        boolean oops = false;
        String source = "";
        try {
            source = new FilesReader(file).readAsString(
                (FilesReader.CustomExceptionCreator<BaseException>) ()
                    -> new BaseException() {
                        private static final long serialVersionUID = -6130330765091840343L;

                        @Override
                        public String getInitiator() {
                            return "JsonDeserializerTest";
                        }

                        @Override
                        public String getErrorMessage() {
                            return String.format(
                                "Could not read the file that contains source tree: %s",
                                file
                            );
                        }
                    }
            );
        } catch (final BaseException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return source;
    }
}
