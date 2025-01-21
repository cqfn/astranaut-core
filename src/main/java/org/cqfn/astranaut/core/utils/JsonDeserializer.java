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

import com.kniazkov.json.Json;
import com.kniazkov.json.JsonException;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.FactoryProvider;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.deserializer.TreeDescriptor;

/**
 * Converts a string that contains a JSON object to a tree.
 * @since 1.0.2
 */
public final class JsonDeserializer {
    /**
     * String contains JSON object.
     */
    private final String source;

    /**
     * The factory provider.
     */
    private final FactoryProvider provider;

    /**
     * Constructor.
     * @param source String that contains JSON object
     * @param provider The factory provider
     */
    public JsonDeserializer(final String source, final FactoryProvider provider) {
        this.source = source;
        this.provider = provider;
    }

    /**
     * Converts the source string contains JSON object to a syntax tree.
     * @return Root node
     */
    public Tree convert() {
        Tree result = EmptyTree.INSTANCE;
        try {
            final TreeDescriptor tree = Json.parse(this.source, TreeDescriptor.class);
            if (tree != null) {
                result = tree.convert(this.provider);
            }
        } catch (final JsonException ignored) {
        }
        return result;
    }
}
