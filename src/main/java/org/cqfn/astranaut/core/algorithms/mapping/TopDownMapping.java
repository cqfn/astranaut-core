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
package org.cqfn.astranaut.core.algorithms.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;

/**
 * Result of top-down mapping algorithm.
 * @since 2.0.0
 */
final class TopDownMapping implements Mapping {
    /**
     * Left-to-right mapping.
     */
    private final Map<Node, Node> ltr;

    /**
     * Right-to-left mapping.
     */
    private final Map<Node, Node> rtl;

    /**
     * Set containing inserted nodes.
     */
    private final List<Insertion> inserted;

    /**
     * Map containing replaces nodes.
     */
    private final Map<Node, Node> replaced;

    /**
     * Set of deleted nodes.
     */
    private final Set<Node> deleted;

    /**
     * Constructor.
     * @param algorithm Structure from which the mapping results can be taken
     */
    TopDownMapping(final TopDownAlgorithm algorithm) {
        this.ltr = TopDownMapping.convert(algorithm.getLeftToRight());
        this.rtl = TopDownMapping.convert(algorithm.getRightToLeft());
        this.inserted = Collections.unmodifiableList(
            algorithm.getInserted().stream()
                .map(ExtInsertion::toInsertion)
                .collect(Collectors.toList())
        );
        this.replaced = TopDownMapping.convert(algorithm.getReplaced());
        this.deleted = Collections.unmodifiableSet(
            algorithm.getDeleted().stream()
                .map(ExtNode::getPrototype)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Node getRight(final Node node) {
        return this.ltr.get(node);
    }

    @Override
    public Node getLeft(final Node node) {
        return this.rtl.get(node);
    }

    @Override
    public List<Insertion> getInserted() {
        return this.inserted;
    }

    @Override
    public Map<Node, Node> getReplaced() {
        return this.replaced;
    }

    @Override
    public Set<Node> getDeleted() {
        return this.deleted;
    }

    /**
     * Converts a collection (map) of extended nodes to a collection of their prototypes.
     * @param original Original collection
     * @return Resulting collection
     */
    private static Map<Node, Node> convert(final Map<ExtNode, ExtNode> original) {
        final Map<Node, Node> result = new HashMap<>();
        for (final Map.Entry<ExtNode, ExtNode> entry : original.entrySet()) {
            Node value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().getPrototype();
            }
            result.put(entry.getKey().getPrototype(), value);
        }
        return Collections.unmodifiableMap(result);
    }
}
