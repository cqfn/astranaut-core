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

import org.cqfn.astranaut.core.base.ActionList;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.FactorySelector;
import org.cqfn.astranaut.core.base.Node;

/**
 * Tree descriptor represented as it is stored in the JSON file.
 *
 * @since 1.1.0
 */
public class TreeDescriptor {
    /**
     * The root node.
     */
    private NodeDescriptor root;

    /**
     * The language.
     */
    private String language;

    /**
     * Constructor.
     */
    @SuppressWarnings({"PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public TreeDescriptor() {
    }

    /**
     * Converts tree into node.
     * @param selector The node factory selector
     * @return A root node
     */
    public Node convert(final FactorySelector selector) {
        Node result = EmptyTree.INSTANCE;
        final Factory factory = selector.select(this.language);
        if (factory != null) {
            final ActionList actions = new ActionList();
            result = this.root.convert(factory, actions);
            if (actions.hasActions()) {
                result = actions.convertTreeToDifferenceTree(result);
            }
        }
        return result;
    }
}
