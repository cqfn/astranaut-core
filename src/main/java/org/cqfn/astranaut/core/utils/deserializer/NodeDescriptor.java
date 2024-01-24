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
package org.cqfn.astranaut.core.utils.deserializer;

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.core.Builder;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.Node;

/**
 * Node descriptor represented as it is stored in the JSON file.
 *
 * @since 1.0.7
 */
public class NodeDescriptor {
    /**
     * The node type.
     */
    private String type;

    /**
     * The node data.
     */
    private String data;

    /**
     * The list of children.
     */
    private List<NodeDescriptor> children;

    /**
     * Constructor.
     */
    @SuppressWarnings({"PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public NodeDescriptor() {
    }

    /**
     * Converts descriptor into node.
     * @param factory The node factory
     * @return A node
     */
    public Node convert(final Factory factory) {
        Node result = EmptyTree.INSTANCE;
        final Builder builder = factory.createBuilder(this.type);
        if (builder != null) {
            if (this.data != null) {
                builder.setData(this.data);
            }
            if (this.children != null) {
                final List<Node> list = new ArrayList<>(this.children.size());
                for (final NodeDescriptor child : this.children) {
                    list.add(child.convert(factory));
                }
                builder.setChildrenList(list);
            }
            if (builder.isValid()) {
                result = builder.createNode();
            }
        }
        return result;
    }
}
