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
package org.cqfn.astranaut.core.utils.parsing;

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.core.base.Char;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Source;
import org.cqfn.astranaut.core.base.Tree;

/**
 * An interface representing a character parser that extends both {@link Source} and
 *  {@link Iterable}.
 * @since 2.0.0
 */
public interface CharParser extends Source, Iterable<Char> {
    /**
     * Parses the source into a list of {@link Char} elements.
     *  The method iterates over all characters and collects them into a list.
     * @return A {@code List} containing all parsed characters in order.
     */
    default List<Char> parseIntoList() {
        final List<Char> list = new ArrayList<>(0);
        this.forEach(list::add);
        return list;
    }

    /**
     * Parses the source into a tree structure where each character becomes a node.
     *  The method constructs a {@link Tree} with a single root node named {@code "Root"},
     *  and all parsed characters are added as its children. This forms a degenerate
     *  syntax tree useful for further processing, such as applying transformation rules.
     * @return A {@code Tree} where all characters are represented as child nodes under the root.
     */
    default Tree parseIntoTree() {
        final List<Node> children = new ArrayList<>(0);
        this.forEach(children::add);
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("Root");
        ctor.setChildrenList(children);
        return new Tree(ctor.createNode());
    }
}
