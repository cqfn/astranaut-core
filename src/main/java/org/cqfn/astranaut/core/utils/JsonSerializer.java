/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.Type;

/**
 * Converts a tree to a string that contains JSON object.
 *
 * @since 1.0.2
 */
public final class JsonSerializer {
    /**
     * The 'root' string.
     */
    private static final String STR_ROOT = "root";

    /**
     * The 'language' string.
     */
    private static final String STR_LANGUAGE = "language";

    /**
     * The 'type' string.
     */
    private static final String STR_TYPE = "type";

    /**
     * The 'data' string.
     */
    private static final String STR_DATA = "data";

    /**
     * The 'children' string.
     */
    private static final String STR_CHILDREN = "children";

    /**
     * The 'common' string.
     */
    private static final String STR_COMMON = "common";

    /**
     * The root node.
     */
    private final Node root;

    /**
     * Programming language that defines a factory
     * with which the inverse transformation will be performed.
     */
    private String language;

    /**
     * Constructor.
     * @param root The root node
     */
    public JsonSerializer(final Node root) {
        this.root = root;
        this.language = "";
    }

    /**
     * Converts the tree to a string that contains a JSON object.
     * @return The tree represented as a string
     */
    public String serialize() {
        final JsonObject obj = new JsonObject();
        obj.add(JsonSerializer.STR_ROOT, this.convertNode(this.root));
        if (!this.language.isEmpty()) {
            obj.addProperty(JsonSerializer.STR_LANGUAGE, this.language);
        }
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    /**
     * Converts the tree to a string that contains a JSON object and
     * writes the result to a file.
     * @param filename The file name
     * @return The result, {@code true} if the file was successful written
     */
    public boolean serializeToFile(final String filename) {
        final String json = this.serialize();
        boolean success = true;
        try {
            new FilesWriter(filename).writeString(json);
        } catch (final IOException | InvalidPathException ignored) {
            success = false;
        }
        return success;
    }

    /**
     * Converts the node to a JSON object.
     * @param node The node
     * @return The JSON object
     */
    private JsonObject convertNode(final Node node) {
        final JsonObject result = new JsonObject();
        final Type type = node.getType();
        result.addProperty(JsonSerializer.STR_TYPE, type.getName());
        final String data = node.getData();
        if (!data.isEmpty()) {
            result.addProperty(JsonSerializer.STR_DATA, data);
        }
        final int count = node.getChildCount();
        if (count > 0) {
            final JsonArray children = new JsonArray();
            result.add(JsonSerializer.STR_CHILDREN, children);
            for (int index = 0; index < count; index = index + 1) {
                children.add(this.convertNode(node.getChild(index)));
            }
        }
        if (this.language.isEmpty()) {
            final String property = type.getProperty(JsonSerializer.STR_LANGUAGE);
            if (!JsonSerializer.STR_COMMON.equals(property)) {
                this.language = property;
            }
        }
        return result;
    }
}
