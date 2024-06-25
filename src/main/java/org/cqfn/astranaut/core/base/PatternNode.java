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
package org.cqfn.astranaut.core.base;

import java.util.ArrayList;
import java.util.List;

/**
 * A node that contains child nodes as well as actions on those nodes.
 * Child nodes can be replaced by holes.
 *
 * @since 1.1.5
 */
public final class PatternNode implements PatternItem {
    /**
     * The prototype node, i.e. 'ordinary', non-difference original node.
     */
    private final Node prototype;

    /**
     * The list of children with actions.
      */
    private final List<PatternItem> children;

    /**
     * Constructor.
     * @param diff Node of the differential tree from which the pattern is built
     */
    public PatternNode(final DiffNode diff) {
        this.prototype = diff.getPrototype();
        this.children = PatternNode.initChildrenList(diff);
    }

    /**
     * Returns the prototype node.
     * @return The prototype node
     */
    public Node getPrototype() {
        return this.prototype;
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

    @Override
    public String toString() {
        return this.prototype.toString();
    }

    /**
     * Turns a child node into a hole.
     * @param node Child node
     * @param number Hole number
     * @return Result of operation, @return {@code true} if node was transformer
     */
    public boolean makeHole(final Node node, final int number) {
        boolean result = false;
        final int index = this.findChildIndex(node);
        if (index >= 0) {
            final Hole hole = new Hole(node.getType(), number);
            this.children.set(index, hole);
            result = true;
        }
        return result;
    }

    /**
     * Transforms children nodes of difference node to pattern items.
     * @param diff Difference node
     * @return List of pattern item
     */
    private static List<PatternItem> initChildrenList(final DiffNode diff) {
        final int count = diff.getChildCount();
        final List<PatternItem> result = new ArrayList<>(count);
        for (int index = 0; index < count; index = index + 1) {
            final Node child = diff.getChild(index);
            if (child instanceof DiffNode) {
                result.add(
                    new PatternNode((DiffNode) child)
                );
            } else if (child instanceof Action) {
                result.add((PatternItem) child);
            }
        }
        return result;
    }

    /**
     * Searches the index of a child element by its prototype.
     * @param node Prototype of the node whose index is to be found
     * @return Index or -1 if there is no such node
     */
    private int findChildIndex(final Node node) {
        int result = -1;
        final int count = this.children.size();
        for (int index = 0; index < count; index = index + 1) {
            final PatternItem child = this.children.get(index);
            if (child instanceof PatternNode
                && node == ((PatternNode) child).getPrototype()) {
                result = index;
                break;
            }
        }
        return result;
    }
}
