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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.NullNode;
import org.cqfn.astranaut.core.base.Transformer;

/**
 * Transforms a tree to another tree using a list of converters.
 * @since 2.0.0
 */
public class DefaultTransformer implements Transformer {
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
    public DefaultTransformer(final List<Converter> converters, final Factory factory) {
        this.converters = converters;
        this.factory = factory;
    }

    /**
     * Transforms a node by applying converters to it.
     *  Thus, some child nodes in this node can be modified.
     *  The procedure runs first recursively itself for all child nodes, so the leaf nodes of
     *  the tree are processed first.
     * @param original Original node
     * @return A new node, i.e., the result of the transformation
     */
    public Node transform(final Node original) {
        final List<Node> list = new ArrayList<>(original.getChildrenList());
        final int count = list.size();
        for (int index = 0; index < count; index = index + 1) {
            list.set(index, this.transform(list.get(index)));
        }
        boolean changed = false;
        boolean flag;
        do {
            flag = false;
            for (final Converter converter : this.converters) {
                int index = -1;
                do {
                    index = this.applyConverter(list, converter, index);
                    flag = flag || index >= 0;
                } while (index >= 0);
            }
            changed = changed || flag;
        } while (flag);
        final Node result;
        if (changed) {
            result = DefaultTransformer.buildNode(original, list);
        } else {
            result = original;
        }
        return result;
    }

    /**
     * Applies a converter to a list of nodes node, attempting to match a subsequence to some rule.
     * @param nodes The list of nodes in which the conversion is performed
     * @param converter Converter
     * @param start Starting index from which the matching begins
     * @return Index of the new node or -1 if there are no changed nodes
     */
    private int applyConverter(final List<Node> nodes, final Converter converter,
        final int start) {
        final int result;
        if (nodes.size() < converter.getMinConsumed()) {
            result = -1;
        } else if (converter.isRightToLeft()) {
            result = this.applyConverterRightToLeft(nodes, converter, start);
        } else {
            result = this.applyConverterLeftToRight(nodes, converter, start);
        }
        return result;
    }

    /**
     * Applies a converter to a list of nodes node, attempting to match a subsequence to some rule.
     *  Comparing nodes to patterns starts from the beginning of the sequence,
     *  i.e., the search direction is from left to right.
     * @param nodes The list of nodes in which the conversion is performed
     * @param converter Converter
     * @param start Starting index from which the matching begins
     * @return Index of the new node or -1 if there are no changed nodes
     */
    private int applyConverterLeftToRight(final List<Node> nodes, final Converter converter,
        final int start) {
        final int count = nodes.size();
        final int consumed = converter.getMinConsumed();
        int result = -1;
        for (int index = Math.max(start, 0); index <= count - consumed; index = index + 1) {
            final Optional<ConversionResult> conversion =
                converter.convert(nodes, index, this.factory);
            if (conversion.isPresent()) {
                DefaultTransformer.replaceNodes(nodes, index, conversion.get());
                result = index;
                break;
            }
        }
        return result;
    }

    /**
     * Applies a converter to a list of nodes node, attempting to match a subsequence to some rule.
     *  Comparing nodes to patterns starts at the end of the sequence,
     *  i.e., the search direction is from right to left.
     * @param nodes The list of nodes in which the conversion is performed
     * @param converter Converter
     * @param start Starting index from which the matching begins
     * @return Index of the new node or -1 if there are no changed nodes
     */
    private int applyConverterRightToLeft(final List<Node> nodes, final Converter converter,
        final int start) {
        final int max = nodes.size() - converter.getMinConsumed();
        int result = -1;
        int index = start;
        if (index < 0 || index > max) {
            index = max;
        }
        for (; index >= 0; index = index - 1) {
            final Optional<ConversionResult> conversion =
                converter.convert(nodes, index, this.factory);
            if (conversion.isPresent()) {
                DefaultTransformer.replaceNodes(nodes, index, conversion.get());
                result = index;
                break;
            }
        }
        return result;
    }

    /**
     * Replaces the nodes in the list with those resulting from conversion.
     * @param list The list in which elements will be replaced
     * @param index The index of the first element to be replaced
     * @param conversion The result of the conversion from which the new node is taken
     */
    private static void replaceNodes(final List<Node> list, final int index,
        final ConversionResult conversion) {
        final Node node = conversion.getNode();
        final int count = conversion.getConsumed();
        if (node == NullNode.INSTANCE) {
            list.subList(index, index + count).clear();
            return;
        }
        if (count > 1) {
            list.subList(index + 1, index + count).clear();
        }
        list.set(index, conversion.getNode());
    }

    /**
     * Re-creates a node with other child nodes.
     * @param original Original node
     * @param children List of child nodes
     * @return New node or dummy node if conversion is not possible
     */
    private static Node buildNode(final Node original, final List<Node> children) {
        Node result = DummyNode.INSTANCE;
        final Builder builder = original.getType().createBuilder();
        builder.setFragment(original.getFragment());
        do {
            if (!builder.setData(original.getData())) {
                break;
            }
            if (!builder.setChildrenList(children)) {
                break;
            }
            if (!builder.isValid()) {
                break;
            }
            result = builder.createNode();
        } while (false);
        return result;
    }
}
