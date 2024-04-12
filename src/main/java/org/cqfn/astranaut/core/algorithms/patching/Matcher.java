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
package org.cqfn.astranaut.core.algorithms.patching;

import java.util.HashSet;
import java.util.Set;
import org.cqfn.astranaut.core.DifferenceNode;
import org.cqfn.astranaut.core.Node;

/**
 * The matcher matches syntax tree and patterns.
 *
 * @since 1.1.5
 */
class Matcher {
    /**
     * Root node of the tree in which patterns are searched.
     */
    private final Node root;

    /**
     * Constructor.
     * @param root Root node of the tree in which patterns are searched
     */
    Matcher(final Node root) {
        this.root = root;
    }

    /**
     * Matches the tree and the pattern.
     * @param pattern Root node of the pattern
     * @return Nodes that match the root node of the pattern
     */
    Set<Node> match(final DifferenceNode pattern) {
        final Set<Node> set = new HashSet<>();

        return set;
    }

    /**
     * Matches the subtree and the pattern.
     * @param node Root node of the subtree
     * @param pattern Root node of the pattern
     * @return Matching result ({@code true} if matches)
     */
    private static boolean matchSubtree(final Node node, final DifferenceNode pattern) {
        return false;
    }
}
