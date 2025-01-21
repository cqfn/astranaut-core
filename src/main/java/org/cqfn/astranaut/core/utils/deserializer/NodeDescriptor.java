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
import java.util.Map;
import org.cqfn.astranaut.core.base.ActionList;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Delete;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Insert;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Replace;
import org.cqfn.astranaut.core.utils.Promise;

/**
 * Node descriptor represented as it is stored in the JSON file.
 * @since 1.1.0
 */
public class NodeDescriptor {
    /**
     * The 'hole' string.
     */
    private static final String STR_HOLE = "Hole";

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
     * The number (for holes only).
     */
    private Integer number;

    /**
     * The prototype descriptor (for holes only).
     */
    private NodeDescriptor prototype;

    /**
     * Converts descriptor into node.
     * @param factory The node factory
     * @param actions List of actions to be added to the tree after deserialization to produce
     *  a difference tree
     * @param holes Set of nodes that need to be replaced with holes
     * @return A node
     */
    public Node convert(final Factory factory,
        final ActionList actions, final Map<Node, Integer> holes) {
        final Converter converter = new Converter(factory, actions, holes);
        return converter.convert(this);
    }

    /**
     * Converter that converts descriptors into nodes.
     * @since 2.0.0
     */
    private static final class Converter {
        /**
         * The node factory.
         */
        private final Factory factory;

        /**
         * List of actions to be added to the tree after deserialization to produce
         * a difference tree.
         */
        private final ActionList actions;

        /**
         * Set of nodes that need to be replaced with holes.
         */
        private final Map<Node, Integer> holes;

        /**
         * Constructor.
         * @param factory The node factory
         * @param actions List of actions to be added to the tree after deserialization to produce
         *  a difference tree
         * @param holes Set of nodes that need to be replaced with holes
         */
        private Converter(final Factory factory,
            final ActionList actions, final Map<Node, Integer> holes) {
            this.factory = factory;
            this.actions = actions;
            this.holes = holes;
        }

        /**
         * Converts descriptor into node.
         * @param descriptor A descriptor that describes a node
         * @return A node
         */
        public Node convert(final NodeDescriptor descriptor) {
            final Node result;
            if (descriptor.type.equals(NodeDescriptor.STR_HOLE)) {
                result = this.convertHole(descriptor);
            } else {
                result = this.convertUsingBuilder(descriptor);
            }
            return result;
        }

        /**
         * Converts descriptor into hole.
         * @param descriptor A descriptor that describes a node
         * @return Prototype of a hole, i.e., a node turned into hole
         */
        private Node convertHole(final NodeDescriptor descriptor) {
            Node original = DummyNode.INSTANCE;
            if (descriptor.prototype != null && descriptor.number != null) {
                original = this.convert(descriptor.prototype);
                this.holes.put(original, descriptor.number);
            }
            return original;
        }

        /**
         * Converts descriptor into node using {@link Builder} interface.
         * @param descriptor A descriptor that describes a node
         * @return A node
         */
        private Node convertUsingBuilder(final NodeDescriptor descriptor) {
            Node result = DummyNode.INSTANCE;
            final Builder builder = this.factory.createBuilder(descriptor.type);
            if (builder != null) {
                Promise<Node> parent = null;
                if (descriptor.data != null) {
                    builder.setData(descriptor.data);
                }
                boolean filled = true;
                if (descriptor.children != null) {
                    final List<Node> list = new ArrayList<>(descriptor.children.size());
                    parent = this.convertChildren(descriptor, list);
                    filled = builder.setChildrenList(list);
                }
                if (filled && builder.isValid()) {
                    result = builder.createNode();
                }
                if (parent != null) {
                    parent.set(result);
                }
            }
            return result;
        }

        /**
         * Converts child descriptors to a list of nodes.
         * @param descriptor A descriptor that describes a node
         * @param list Resulting list of converted nodes
         * @return List of child nodes
         */
        private Promise<Node> convertChildren(final NodeDescriptor descriptor,
            final List<Node> list) {
            Promise<Node> parent = null;
            for (final NodeDescriptor child : descriptor.children) {
                final Node converted = this.convert(child);
                if (converted instanceof Insert) {
                    final Node node = ((Insert) converted).getAfter();
                    Node after = null;
                    final int size = list.size();
                    if (size > 0) {
                        after = list.get(size - 1);
                    }
                    parent = this.actions.insertNodeAfter(node, after);
                } else if (converted instanceof Replace) {
                    final Replace action = (Replace) converted;
                    final Node node = action.getBefore();
                    list.add(node);
                    this.actions.replaceNode(node, action.getAfter());
                } else if (converted instanceof Delete) {
                    final Node node = ((Delete) converted).getBefore();
                    list.add(node);
                    this.actions.deleteNode(node);
                } else {
                    list.add(converted);
                }
            }
            return parent;
        }
    }
}
