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
package org.cqfn.astranaut.core.algorithms.normalization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;

/**
 * List of properties extracted from the original node.
 * @since 2.0.0
 */
public final class Properties implements Node {
    /**
     * Type of node containing list of properties extracted from the original node.
     */
    private static final Type TYPE = new PropertiesType();

    /**
     * List of properties.
     */
    private final List<Property> list;

    /**
     * Constructor.
     * @param map Map, containing properties from original node
     */
    public Properties(final Map<String, String> map) {
        this.list = Properties.initList(map);
    }

    @Override
    public Type getType() {
        return Properties.TYPE;
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public int getChildCount() {
        return this.list.size();
    }

    @Override
    public Node getChild(final int index) {
        return this.list.get(index);
    }

    /**
     * Initiates list of property nodes.
     * @param map Map, containing properties from original node
     * @return List of nodes, each containing one property
     */
    private static List<Property> initList(final Map<String, String> map) {
        final List<Property> list = new ArrayList<>(map.size());
        for (final Map.Entry<String, String> property : map.entrySet()) {
            list.add(new Property(property.getKey(), property.getValue()));
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Type of node containing list of properties extracted from the original node.
     * @since 2.0.0
     */
    private static final class PropertiesType implements Type {
        @Override
        public String getName() {
            return "Properties";
        }

        @Override
        public Builder createBuilder() {
            throw new UnsupportedOperationException();
        }
    }
}
