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
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.PrototypeBasedNode;
import org.cqfn.astranaut.core.base.Type;

/**
 * Represents a normalized node.
 *  A normalized node is a node without properties. All properties of the original node
 *  are extracted into separate child nodes.
 * @since 2.0.0
 */
public final class NormalizedNode implements PrototypeBasedNode {
    /**
     * The original, non-normalized node.
     */
    private final Node original;

    /**
     * List of normalized child nodes.
     */
    private final List<NormalizedNode> children;

    /**
     * List of properties.
     */
    private final Properties properties;

    /**
     * Constructor.
     * @param original The original, non-normalized node.
     */
    public NormalizedNode(final Node original) {
        this.original = original;
        this.properties = NormalizedNode.initProperties(original);
        this.children = NormalizedNode.initChildrenList(original);
    }

    @Override
    public Fragment getFragment() {
        return this.original.getFragment();
    }

    @Override
    public Type getType() {
        return this.original.getType();
    }

    @Override
    public String getData() {
        return this.original.getData();
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public int getChildCount() {
        int count = this.children.size();
        if (this.properties != null) {
            count = count + 1;
        }
        return count;
    }

    @Override
    public Node getChild(final int index) {
        final Node node;
        if (this.properties == null) {
            node = this.children.get(index);
        } else if (index == 0) {
            node = this.properties;
        } else {
            node = this.children.get(index - 1);
        }
        return node;
    }

    @Override
    public Node getPrototype() {
        return this.original;
    }

    /**
     * Initiates list of properties, extracted from original node.
     * @param original Original node
     * @return Node containing list of properties or {@code null} if the original node
     *  does not contain any properties
     */
    private static Properties initProperties(final Node original) {
        Properties object = null;
        final Map<String, String> map = original.getProperties();
        if (!map.isEmpty()) {
            object = new Properties(map);
        }
        return object;
    }

    /**
     * Prepares a list of child normalized nodes.
     * @param original Original node
     * @return List of child normalized nodes
     */
    private static List<NormalizedNode> initChildrenList(final Node original) {
        final int count = original.getChildCount();
        final List<NormalizedNode> list = new ArrayList<>(count);
        for (int index = 0; index < count; index = index + 1) {
            list.add(new NormalizedNode(original.getChild(index)));
        }
        return Collections.unmodifiableList(list);
    }
}
