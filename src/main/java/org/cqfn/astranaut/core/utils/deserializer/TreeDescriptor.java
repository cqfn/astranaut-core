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

import java.util.HashMap;
import java.util.Map;
import org.cqfn.astranaut.core.algorithms.PatternBuilder;
import org.cqfn.astranaut.core.base.ActionList;
import org.cqfn.astranaut.core.base.DiffTree;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.JsonDeserializer;

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
     * Converts tree into node.
     * @param selector The node factory selector
     * @return A root node
     */
    public Tree convert(final JsonDeserializer.FactorySelector selector) {
        Tree result = EmptyTree.INSTANCE;
        final Factory factory = selector.select(this.language);
        do {
            if (factory == null) {
                break;
            }
            final ActionList actions = new ActionList();
            final Map<Node, Integer> holes = new HashMap<>();
            result = new Tree(this.root.convert(factory, actions, holes));
            if (holes.isEmpty() && !actions.hasActions()) {
                break;
            }
            final DiffTree diff = actions.convertTreeToDiffTree(result);
            result = diff;
            if (holes.isEmpty()) {
                break;
            }
            final PatternBuilder builder = new PatternBuilder(diff);
            for (final Map.Entry<Node, Integer> pair : holes.entrySet()) {
                builder.makeHole(pair.getKey(), pair.getValue());
            }
            result = builder.getPattern();
        } while (false);
        return result;
    }
}
