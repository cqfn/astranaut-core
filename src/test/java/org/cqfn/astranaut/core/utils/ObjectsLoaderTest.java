/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.core.algorithms.conversion.Converter;
import org.cqfn.astranaut.core.algorithms.conversion.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link ObjectsLoader} class.
 * @since 2.0.0
 */
class ObjectsLoaderTest {
    @Test
    void loadSingleton() {
        final ObjectsLoader loader = new ObjectsLoader(
            "org.cqfn.astranaut.core.example.converters.AdditionConverter"
        );
        final Object obj = loader.loadSingleton(-1);
        Assertions.assertTrue(obj instanceof Converter);
    }

    @Test
    void loadSingletonByIndex() {
        final ObjectsLoader loader = new ObjectsLoader(
            "org.cqfn.astranaut.core.example.converters.OperatorMatcher"
        );
        final List<Matcher> list = new ArrayList<>(3);
        for (int index = 0; index < 4; index = index + 1) {
            final Object obj = loader.loadSingleton(index);
            if (obj == null) {
                break;
            }
            Assertions.assertTrue(obj instanceof Matcher);
            list.add((Matcher) obj);
        }
        Assertions.assertEquals(3, list.size());
    }
}
