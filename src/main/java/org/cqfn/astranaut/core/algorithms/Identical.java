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
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.hash.SimpleHash;

/**
 * A class which finds and returns identical nodes in a tree.
 * The nodes are identical if their data is not empty, and their {@link SimpleHash} equals.
 *
 * @since 1.1.5
 */
@SuppressWarnings("PMD.ConstructorShouldDoInitialization")
public class Identical {
    /**
     * The root of the tree to search in.
     */
    private final Node root;

    /**
     * The {@link SimpleHash} storage for the tree nodes.
     */
    private final SimpleHash hashes;

    /**
     * The map of the calculated hashes and corresponding nodes.
     */
    private final Map<Integer, Set<Node>> results = new HashMap<>();

    /**
     * Constructor.
     *
     * @param root The root of the tree to search in
     */
    public Identical(final Node root) {
        this.root = root;
        this.hashes = new SimpleHash();
    }

    /**
     * Get identical nodes.
     *
     * @return The set of sets with identical nodes.
     */
    public Set<Set<Node>> get() {
        this.search(this.root);
        return this.results.values()
            .stream()
            .filter(set -> set.size() >= 2)
            .collect(Collectors.toSet());
    }

    /**
     * Calculates {@link SimpleHash} recursively for all nodes with non-empty data and
     * adds entries to the resulting map.
     *
     * @param node The current node to process
     */
    private void search(final Node node) {
        if (!node.getData().isEmpty()) {
            this.results.computeIfAbsent(
                this.hashes.calculate(node),
                s -> new HashSet<>()
            ).add(node);
        }
        for (final Node child: node.getChildrenList()) {
            this.search(child);
        }
    }
}
