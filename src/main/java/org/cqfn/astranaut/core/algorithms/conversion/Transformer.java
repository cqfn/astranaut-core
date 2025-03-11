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
package org.cqfn.astranaut.core.algorithms.conversion;

import java.util.List;
import java.util.Optional;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.MutableNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;

/**
 * Transforms a tree to another tree using a list of converters.
 * @since 2.0.0
 */
public final class Transformer {
    /**
     * List of converters that are used in the conversion.
     */
    private final List<Converter> converters;

    /**
     * Factory that is used to create the nodes of the resulting trees.
     */
    private final Factory factory;

    /**
     * Constructor.
     * @param converters List of converters that are used in the conversion
     * @param factory Factory that is used to create the nodes of the resulting trees
     */
    public Transformer(final List<Converter> converters, final Factory factory) {
        this.converters = converters;
        this.factory = factory;
    }

    /**
     * Transforms a syntax tree into another tree.
     * @param tree The tree to be transformed
     * @return A new tree, the result of a transformation
     */
    public Tree transform(final Tree tree) {
        return this.transform(tree.getRoot());
    }

    /**
     * Transforms a syntax tree into another tree.
     * @param root The root node of the tree to be transformed
     * @return A new tree, the result of a transformation
     */
    public Tree transform(final Node root) {
        final MutableNode mutable = new MutableNode(root);
        this.transformNode(mutable);
        final Node result = mutable.rebuild();
        return new Tree(result);
    }

    /**
     * Transforms a mutable node by applying converters to it.
     *  Thus, some child nodes in this node can be modified.
     *  The procedure runs first recursively itself for all child nodes, so the leaf nodes of
     *  the tree are processed first.
     * @param node Mutable node
     */
    private void transformNode(final MutableNode node) {
        final int count = node.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            final MutableNode child = node.getMutableChild(index);
            this.transformNode(child);
            node.replaceRange(index, 1, child.getPrototype());
        }
        boolean flag;
        do {
            flag = false;
            for (final Converter converter : this.converters) {
                int index = -1;
                do {
                    index = this.applyConverter(node, converter, index);
                    flag = flag || index >= 0;
                } while (index >= 0);
            }
        } while (flag);
    }

    /**
     * Applies a converter to a node, attempting to match a sequence of child nodes to some rule.
     * @param node Mutable node
     * @param converter Converter
     * @param start Starting index from which the matching begins
     * @return Index of the new node or -1 if there are no changed nodes
     */
    private int applyConverter(final MutableNode node, final Converter converter,
        final int start) {
        final int result;
        if (node.getChildCount() < converter.getMinConsumed()) {
            result = -1;
        } else if (converter.isRightToLeft()) {
            result = this.applyConverterRightToLeft(node, converter, start);
        } else {
            result = this.applyConverterLeftToRight(node, converter, start);
        }
        return result;
    }

    /**
     * Applies a converter to a node, attempting to match a sequence of child nodes to some rule.
     *  Comparing nodes to patterns starts from the beginning of the sequence,
     *  i.e., the search direction is from left to right.
     * @param node Mutable node
     * @param converter Converter
     * @param start Starting index from which the matching begins
     * @return Index of the new node or -1 if there are no changed nodes
     */
    private int applyConverterLeftToRight(final MutableNode node, final Converter converter,
        final int start) {
        final int count = node.getChildCount();
        final int consumed = converter.getMinConsumed();
        int result = -1;
        for (int index = Math.max(start, 0); index <= count - consumed; index = index + 1) {
            final Optional<ConversionResult> conversion =
                converter.convert(node, index, this.factory);
            if (conversion.isPresent()) {
                final ConversionResult obj = conversion.get();
                node.replaceRange(index, obj.getConsumed(), obj.getNode());
                result = index;
                break;
            }
        }
        return result;
    }

    /**
     * Applies a converter to a node, attempting to match a sequence of child nodes to some rule.
     *  Comparing nodes to patterns starts at the end of the sequence,
     *  i.e., the search direction is from right to left.
     * @param node Mutable node
     * @param converter Converter
     * @param start Starting index from which the matching begins
     * @return Index of the new node or -1 if there are no changed nodes
     */
    private int applyConverterRightToLeft(final MutableNode node, final Converter converter,
        final int start) {
        final int count = node.getChildCount();
        final int consumed = converter.getMinConsumed();
        int result = -1;
        for (int index = Math.max(start, count - consumed); index >= 0; index = index - 1) {
            final Optional<ConversionResult> conversion =
                converter.convert(node, index, this.factory);
            if (conversion.isPresent()) {
                final ConversionResult obj = conversion.get();
                node.replaceRange(index, obj.getConsumed(), obj.getNode());
                result = index;
                break;
            }
        }
        return result;
    }
}
