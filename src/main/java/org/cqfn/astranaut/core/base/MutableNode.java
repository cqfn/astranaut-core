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
package org.cqfn.astranaut.core.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mutable node whose children can be replaced during syntactic tree customization.
 * @since 1.0
 */
public final class MutableNode implements PrototypeBasedNode {
    /**
     * The parent convertible node.
     */
    private final MutableNode parent;

    /**
     * The prototype node.
     */
    private final Node prototype;

    /**
     * The list of children.
      */
    private final List<Node> children;

    /**
     * Constructor.
     * @param parent The parent convertible node.
     * @param prototype The prototype node.
     */
    private MutableNode(final MutableNode parent, final Node prototype) {
        this.parent = parent;
        this.prototype = prototype;
        this.children = this.initChildrenList();
    }

    /**
     * Constructor.
     * @param prototype The prototype node.
     */
    public MutableNode(final Node prototype) {
        this(null, prototype);
    }

    /**
     * Returns the parent node.
     * @return The parent node
     */
    public MutableNode getParent() {
        return this.parent;
    }

    @Override
    public Fragment getFragment() {
        return this.prototype.getFragment();
    }

    @Override
    public Type getType() {
        return this.prototype.getType();
    }

    @Override
    public String getData() {
        return this.prototype.getData();
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

    /**
     * Returns a child, transformed to {@link MutableNode}, by its index.
     * @param index Child index
     * @return Convertible node
     */
    public MutableNode getMutableChild(final int index) {
        final Node node = this.children.get(index);
        final MutableNode result;
        if (node instanceof MutableNode) {
            result = (MutableNode) node;
        } else {
            result = new MutableNode(this, node);
            this.children.set(index, result);
        }
        return result;
    }

    @Override
    public List<Node> getChildrenList() {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public String toString() {
        return Node.toString(this);
    }

    /**
     * Replaces a child node with another node.
     * @param before Existing child node (before changes)
     * @param after Substitute node
     * @return Result of operation, {@code true} if replacement was successful
     */
    public boolean replaceChild(final Node before, final Node after) {
        boolean result = false;
        final int size = this.children.size();
        for (int index = 0; !result && index < size; index = index + 1) {
            final Node child = this.children.get(index);
            if (child.equals(before)
                || child instanceof MutableNode && ((MutableNode) child).getPrototype() == before) {
                this.children.set(index, after);
                result = true;
            }
        }
        return result;
    }

    /**
     * Builds a non-mutable subtree from this node.
     * @return The root of a non-mutable subtree
     */
    public Node rebuild() {
        final Builder builder = this.prototype.getType().createBuilder();
        builder.setFragment(this.prototype.getFragment());
        builder.setData(this.prototype.getData());
        final List<Node> list = new ArrayList<>(this.children.size());
        for (final Node child : this.children) {
            if (child instanceof MutableNode) {
                list.add(((MutableNode) child).rebuild());
            } else {
                list.add(child);
            }
        }
        builder.setChildrenList(list);
        final Node result;
        if (builder.isValid()) {
            result = builder.createNode();
        } else {
            result = DummyNode.INSTANCE;
        }
        return result;
    }

    @Override
    public Node getPrototype() {
        return this.prototype;
    }

    /**
     * Transforms children nodes to convertible ones.
     * @return List of mutable nodes
     */
    private List<Node> initChildrenList() {
        final int count = this.prototype.getChildCount();
        final List<Node> result = new ArrayList<>(count);
        for (int index = 0; index < count; index = index + 1) {
            result.add(new MutableNode(this, this.prototype.getChild(index)));
        }
        return result;
    }
}
