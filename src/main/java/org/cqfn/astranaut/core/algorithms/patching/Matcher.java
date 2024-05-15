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
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.core.DifferenceNode;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.DeepTraversal;

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
        final DeepTraversal deep = new DeepTraversal(this.root);
        final List<Node> preset = deep.findAll(
            node -> node.getTypeName().equals(pattern.getTypeName()) &&
                node.getData().equals(pattern.getData())
        );
        final Set<Node> set = new HashSet<>();
        set.addAll(preset);
        return set;
    }

    /**
     * Checks if the children of the original tree node matches the children of the pattern node.
     * @param node Node of the original tree
     * @param diff Node of the difference tree (i.e. pattern)
     * @return Matching result ({@code true} if matches)
     */
    private static boolean checkChildren(final Node node, final DifferenceNode diff) {
        final int count = node.getChildCount();
        for (int left = 0; left < count; left = left + 1) {

        }
        return false;
    }

    /**
     * Finds the first child of the original tree node that matches the first child
     * of the pattern node.
     * @param node Node of the original tree
     * @param diff Node of the difference tree (i.e. pattern)
     * @return Index of the found child or a negative number if no match is found
     */
    private static int findFirstMatchingChild(final Node node, final DifferenceNode diff) {
        final int left = node.getChildCount();
        final int right = diff.getChildCount();
        assert right > 0;
        final Node sample = diff.getChild(0);

        for (int index = 0; left - index >= right; index = index + 1) {
            final Node child = node.getChild(index);
        }
        return false;

    }
}
