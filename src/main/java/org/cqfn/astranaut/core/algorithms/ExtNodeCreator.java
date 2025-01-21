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
package org.cqfn.astranaut.core.algorithms;

import java.util.Map;
import org.cqfn.astranaut.core.algorithms.hash.AbsoluteHash;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;

/**
 * Creates extended nodes ({@link ExtNode}) from 'regular' nodes ({@link Node}).
 * @since 2.0.0
 */
public final class ExtNodeCreator {
    /**
     * Set of node hashes.
     */
    private final AbsoluteHash hashes;

    /**
     * Constructor.
     */
    public ExtNodeCreator() {
        this.hashes = new AbsoluteHash();
    }

    /**
     * Creates an extended node from a 'regular' node.
     * @param node Original node
     * @return Extended node
     */
    public ExtNode create(final Node node) {
        return this.create(node, null);
    }

    /**
     * Creates an extended node from a 'regular' node (recursive method).
     * @param node Original node
     * @param parent Parent node of the node to be created
     * @return Extended node
     */
    private ExtNodeImpl create(final Node node, final ExtNode parent) {
        final ExtNodeImpl ext = new ExtNodeImpl();
        ext.prototype = node;
        ext.parent = parent;
        ext.index = -1;
        ext.hash = this.hashes.calculate(node);
        final int count = node.getChildCount();
        ext.children = new ExtNodeImpl[count];
        int index;
        for (index = 0; index < count; index = index + 1) {
            final ExtNodeImpl child = this.create(node.getChild(index), ext);
            child.index = index;
            ext.children[index] = child;
        }
        for (index = 1; index < count; index = index + 1) {
            ext.children[index].left = ext.children[index - 1];
        }
        for (index = 0; index < count - 1; index = index + 1) {
            ext.children[index].right = ext.children[index + 1];
        }
        return ext;
    }

    /**
     * Class that implements the extended node interface.
     * @since 2.0.0
     */
    private static final class ExtNodeImpl implements ExtNode {
        /**
         * Prototype (not-extended) node.
         */
        private Node prototype;

        /**
         * Parent node of this node, which is the node that has this node as on of its children.
         */
        private ExtNode parent;

        /**
         * Index (sequence number) of this node in the list of children of this node's parent.
         */
        private int index;

        /**
         * Left neighbor node of this node, which is the node whose index in the parent node
         *  is one less than the index of this node.
         */
        private ExtNode left;

        /**
         * Right neighbor node of this node, which is the node whose index in the parent node
         *  is one greater than the index of this node.
         */
        private ExtNode right;

        /**
         * Absolute hash.
         */
        private int hash;

        /**
         * Array of child nodes wrapped in the extended node interface.
         */
        private ExtNodeImpl[] children;

        @Override
        public Node getPrototype() {
            return this.prototype;
        }

        @Override
        public ExtNode getParent() {
            return this.parent;
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        @Override
        public ExtNode getLeft() {
            return this.left;
        }

        @Override
        public ExtNode getRight() {
            return this.right;
        }

        @Override
        public int getAbsoluteHash() {
            return this.hash;
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
            return this.children.length;
        }

        @Override
        public Node getChild(final int idx) {
            return this.children[idx];
        }

        @Override
        public ExtNode getExtChild(final int idx) {
            return this.children[idx];
        }

        @Override
        public Map<String, String> getProperties() {
            return this.prototype.getProperties();
        }

        @Override
        public String toString() {
            return this.prototype.toString();
        }
    }
}
