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
package org.cqfn.astranaut.core.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.cqfn.astranaut.core.base.Node;

/**
 * A class which finds and returns identical nodes in a tree.
 *  Nodes are listed if they have data and their hashes match.
 * @since 1.1.5
 */
public class IdenticalNodeFinder {
    /**
     * The root of the tree to search in.
     */
    private final Node root;

    /**
     * Constructor.
     *
     * @param root The root of the tree to search in
     */
    public IdenticalNodeFinder(final Node root) {
        this.root = root;
    }

    /**
     * Get identical nodes.
     *
     * @return The set of sets with identical nodes.
     */
    public Set<Set<Node>> find() {
        final Map<Integer, Set<Node>> result = new HashMap<>();
        this.search(this.root, result);
        return result.values()
            .stream()
            .filter(set -> set.size() >= 2)
            .collect(Collectors.toSet());
    }

    /**
     * Calculates hash recursively for all nodes with non-empty data and
     * adds entries to the resulting map.
     *
     * @param node The current node to process
     * @param result Where to put the result
     */
    private void search(final Node node, final Map<Integer, Set<Node>> result) {
        if (!node.getData().isEmpty()) {
            result.computeIfAbsent(
                node.getLocalHash(),
                s -> new HashSet<>()
            ).add(node);
        }
        for (final Node child: node.getChildrenList()) {
            this.search(child, result);
        }
    }
}
