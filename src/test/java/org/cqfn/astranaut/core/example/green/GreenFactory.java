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
package org.cqfn.astranaut.core.example.green;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.DefaultFactory;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Type;

/**
 * Factory that creates 'green' nodes.
 * @since 1.0
 */
public final class GreenFactory extends DefaultFactory {
    /**
     * The instance.
     */
    public static final Factory INSTANCE = new GreenFactory();

    /**
     * Private constructor.
     */
    private GreenFactory() {
        super(Collections.unmodifiableMap(GreenFactory.init()));
    }

    /**
     * Initialises the set of types arranged by name.
     * @return The map of types by name
     */
    private static Map<String, Type> init() {
        final List<Type> types = Arrays.asList(
            Addition.TYPE,
            ExpressionList.TYPE,
            ExpressionStatement.TYPE,
            IntegerLiteral.TYPE,
            Multiplication.TYPE,
            Return.TYPE,
            SimpleAssignment.TYPE,
            StatementBlock.TYPE,
            Variable.TYPE
        );
        final Map<String, Type> map = new TreeMap<>();
        for (final Type type : types) {
            map.put(type.getName(), type);
        }
        return map;
    }
}
