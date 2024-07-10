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
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.NodeAndType;
import org.cqfn.astranaut.core.base.PrototypeBasedNode;

/**
 * Represents a normalized node.
 *  A normalized node is a node without properties. All properties of the original node
 *  are extracted into separate child nodes.
 * @since 2.0.0
 */
public final class NormalizedNode extends NodeAndType implements PrototypeBasedNode {
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
    private Properties properties;

    /**
     * Constructor.
     * @param original The original, non-normalized node.
     */
    public NormalizedNode(final Node original) {
        this(
            original,
            NormalizedNode.initProperties(original),
            NormalizedNode.initChildrenList(original)
        );
    }

    /**
     * Private constructor (for internal usage).
     * @param original The original, non-normalized node
     * @param properties List of properties
     * @param children List of normalized child nodes
     */
    private NormalizedNode(final Node original, final Properties properties,
        final List<NormalizedNode> children) {
        this.original = original;
        this.properties = properties;
        this.children = children;
    }

    @Override
    public Fragment getFragment() {
        return this.original.getFragment();
    }

    @Override
    public String getName() {
        return this.original.getTypeName();
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
    public Builder createBuilder() {
        return new NormalizerNodeBuilder(this.original.getType().createBuilder());
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
    public String toString() {
        return Node.toString(this);
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

    /**
     * Builds a normalizer node.
     * @since 2.0.0
     */
    private static final class NormalizerNodeBuilder implements Builder {
        /**
         * Builder for original, non-normalized node.
         */
        private final Builder original;

        /**
         * Properties extracted from the children list.
         */
        private Properties properties;

        /**
         * List of normalized child nodes.
         */
        private List<NormalizedNode> children;

        /**
         * Constructor.
         * @param original Builder for original, non-normalized node
         */
        private NormalizerNodeBuilder(final Builder original) {
            this.original = original;
            this.children = Collections.emptyList();
        }

        @Override
        public void setFragment(final Fragment fragment) {
            this.original.setFragment(fragment);
        }

        @Override
        public boolean setData(final String str) {
            return this.original.setData(str);
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            final List<Node> nodes;
            Properties props = null;
            int size = list.size();
            if (size > 0 && list.get(0) instanceof Properties) {
                props = (Properties) list.get(0);
                nodes = list.subList(1, size);
                size = size - 1;
            } else {
                nodes = list;
            }
            final List<Node> originals = new ArrayList<>(size);
            final List<NormalizedNode> normalized = new ArrayList<>(size);
            for (final Node node : nodes) {
                if (node instanceof NormalizedNode) {
                    final NormalizedNode child = (NormalizedNode) node;
                    originals.add(child.original);
                    normalized.add(child);
                } else {
                    originals.add(node);
                    normalized.add(new NormalizedNode(node));
                }
            }
            final boolean result = this.original.setChildrenList(originals);
            if (result) {
                this.properties = props;
                this.children = normalized;
            }
            return result;
        }

        @Override
        public boolean isValid() {
            return this.original.isValid();
        }

        @Override
        public Node createNode() {
            return new NormalizedNode(
                this.original.createNode(),
                this.properties,
                this.children
            );
        }
    }
}

