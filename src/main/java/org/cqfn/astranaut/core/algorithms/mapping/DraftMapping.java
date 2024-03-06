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
package org.cqfn.astranaut.core.algorithms.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.core.Node;

/**
 * A mapping that has ambiguities, that is, one-to-many relations,
 * that is, that offers multiple variants of 'right' tree nodes that are somehow related
 * to a single 'left' tree node.
 *
 * @since 1.1.3
 */
final class DraftMapping {
    /**
     * Left-to-right relations.
     */
    private final Map<Node, Set<Node>> ltr;

    /**
     * Right-to-left relations.
     */
    private final Map<Node, Set<Node>> rtl;

    /**
     * Constructor.
     */
    DraftMapping() {
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
    }

    /**
     * Adds the relation of a node from the 'left' tree to some set of nodes from the 'right' tree.
     * @param left Node from the 'left' tree
     * @param right Related nodes from the 'right' tree
     */
    public void addRelation(final Node left, final Set<Node> right) {
        this.ltr.put(left, right);
        for (final Node node : right) {
            final Set<Node> set = this.rtl.computeIfAbsent(node, k -> new HashSet<>());
            set.add(left);
        }
    }

    /**
     * Returns the relation of a node from the 'left' tree to some set of nodes.
     * from the 'right' tree
     * @param left Node from the 'left' tree
     * @return Related nodes from the 'right' tree or empty set
     */
    public Set<Node> getRelation(final Node left) {
        return this.ltr.getOrDefault(left, Collections.emptySet());
    }

    /**
     * Removes the relationship of nodes from the 'left' and 'right' trees.
     * This reduces ambiguity, i.e. if a 'left' node has been correlated with a 'right' node,
     * it can no longer be correlated with any other 'right' node.
     * There should be no ambiguities remaining at the end of the mapping.
     * @param left Node from the 'left' tree
     * @param right Node from the 'right' tree
     */
    public void removeRelation(final Node left, final Node right) {
        this.ltr.remove(left);
        if (this.rtl.containsKey(right)) {
            for (final Node node : this.rtl.get(right)) {
                final Set<Node> set = this.ltr.get(node);
                if (set != null) {
                    set.remove(right);
                }
            }
        }
    }

    /**
     * Returns set of mapped nodes from the 'left' tree.
     * @return Set of nodes that have relations
     */
    public Set<Node> getLeftNodes() {
        return this.ltr.keySet();
    }

    /**
     * Returns {@code} if this collection contains no mappings.
     * @return Checking result
     */
    public boolean isEmpty() {
        return this.ltr.isEmpty();
    }
}
