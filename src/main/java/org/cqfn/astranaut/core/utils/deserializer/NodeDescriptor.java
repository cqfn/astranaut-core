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

/**
 * Node descriptor represented as it is stored in the JSON file.
 *
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
     * @param actions List of actions to be added to the tree after deserialization
     *  to produce a difference tree
     * @param holes A set of nodes that need to be replaced with holes
     * @return A node
     */
    public Node convert(final Factory factory, final ActionList actions,
        final Map<Node, Integer> holes) {
        final Node result;
        if (this.type.equals(NodeDescriptor.STR_HOLE)) {
            result = this.convertHole(factory, actions, holes);
        } else {
            result = this.convertUsingBuilder(factory, actions, holes);
        }
        return result;
    }

    /**
     * Converts descriptor into hole.
     * @param factory The node factory
     * @param actions List of actions to be added to the tree after deserialization
     *  to produce a difference tree
     * @param holes A set of nodes that need to be replaced with holes
     * @return Prototype of a hole, i.e., a node turned into hole
     */
    private Node convertHole(final Factory factory, final ActionList actions,
        final Map<Node, Integer> holes) {
        Node original = DummyNode.INSTANCE;
        if (this.prototype != null && this.number != null) {
            original = this.prototype.convert(factory, actions, holes);
            holes.put(original, this.number);
        }
        return original;
    }

    /**
     * Converts descriptor into node using {@link Builder} interface.
     * @param factory The node factory
     * @param actions List of actions to be added to the tree after deserialization
     *  to produce a difference tree
     * @param holes A set of nodes that need to be replaced with holes
     * @return A node
     */
    private Node convertUsingBuilder(final Factory factory, final ActionList actions,
        final Map<Node, Integer> holes) {
        Node result = DummyNode.INSTANCE;
        final Builder builder = factory.createBuilder(this.type);
        if (builder != null) {
            if (this.data != null) {
                builder.setData(this.data);
            }
            boolean filled = true;
            if (this.children != null) {
                filled = builder.setChildrenList(this.convertChildren(factory, actions, holes));
            }
            if (filled && builder.isValid()) {
                result = builder.createNode();
            }
        }
        return result;
    }

    /**
     * Converts child descriptors to a list of nodes.
     * @param factory The node factory
     * @param actions List of actions to be added to the tree after deserialization
     *  to produce a difference tree
     * @param holes A set of nodes that need to be replaced with holes
     * @return List of child nodes
     */
    private List<Node> convertChildren(final Factory factory, final ActionList actions,
        final Map<Node, Integer> holes) {
        final List<Node> list = new ArrayList<>(this.children.size());
        for (final NodeDescriptor child : this.children) {
            final Node converted = child.convert(factory, actions, holes);
            if (converted instanceof Insert) {
                final Node node = ((Insert) converted).getAfter();
                Node after = null;
                final int size = list.size();
                if (size > 0) {
                    after = list.get(size - 1);
                }
                actions.insertNodeAfter(node, null, after);
            } else if (converted instanceof Replace) {
                final Replace action = (Replace) converted;
                final Node node = action.getBefore();
                list.add(node);
                actions.replaceNode(node, action.getAfter());
            } else if (converted instanceof Delete) {
                final Node node = ((Delete) converted).getBefore();
                list.add(node);
                actions.deleteNode(node);
            } else {
                list.add(converted);
            }
        }
        return list;
    }
}
