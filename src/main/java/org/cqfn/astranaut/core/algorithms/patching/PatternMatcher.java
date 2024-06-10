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
import org.cqfn.astranaut.core.Action;
import org.cqfn.astranaut.core.Delete;
import org.cqfn.astranaut.core.DifferenceNode;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.Replace;
import org.cqfn.astranaut.core.algorithms.DeepTraversal;
import org.cqfn.astranaut.core.utils.deserializer.ActionList;

/**
 * The matcher matches syntax tree and patterns.
 *
 * @since 1.1.5
 */
class PatternMatcher {
    /**
     * Root node of the tree in which patterns are searched.
     */
    private final Node root;

    /**
     * List of actions to be performed on the original tree to apply the pattern.
     */
    private final ActionList actions;

    /**
     * Constructor.
     * @param root Root node of the tree in which patterns are searched
     */
    PatternMatcher(final Node root) {
        this.root = root;
        this.actions = new ActionList();
    }

    /**
     * Returns the list of actions that were compiled when the pattern was matched.
     * @return Action list
     */
    public ActionList getActionList() {
        return this.actions;
    }

    /**
     * Matches the tree and the pattern.
     * @param pattern Root node of the pattern
     * @return Nodes that match the root node of the pattern
     */
    Set<Node> match(final DifferenceNode pattern) {
        final DeepTraversal deep = new DeepTraversal(this.root);
        final List<Node> preset = deep.findAll(
            node -> node.getTypeName().equals(pattern.getTypeName())
                && node.getData().equals(pattern.getData())
        );
        final Set<Node> set = new HashSet<>();
        for (final Node node : preset) {
            final boolean matches = this.checkNode(node, null, pattern);
            if (matches) {
                set.add(node);
            }
        }
        return set;
    }

    /**
     * Checks if the node of the original tree matches the pattern node.
     * @param node Node of the original tree
     * @param parent Parent node of the node being checked.
     * @param pattern Node of the difference tree (i.e. pattern)
     * @return Matching result ({@code true} if matches)
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean checkNode(final Node node, final Node parent, final Node pattern) {
        final Node sample;
        if (pattern instanceof Replace || pattern instanceof Delete) {
            sample = ((Action) pattern).getBefore();
        } else {
            sample = pattern;
        }
        final boolean result = node.getChildCount() >= sample.getChildCount()
            && node.getTypeName().equals(sample.getTypeName())
            && node.getData().equals(sample.getData())
            && this.checkChildren(node, sample);
        if (result && pattern instanceof Replace) {
            this.actions.replaceNode(node, ((Action) pattern).getAfter());
        } else if (result & pattern instanceof Delete) {
            this.actions.deleteNode(node);
        }
        return result;
    }

    /**
     * Checks if the children of the node of the original tree
     *  matches the children of the pattern node.
     * @param node Node of the original tree
     * @param sample Node of the difference tree (i.e. pattern)
     * @return Matching result ({@code true} if matches)
     */
    private boolean checkChildren(final Node node, final Node sample) {
        final int left = node.getChildCount();
        final int right = sample.getChildCount();
        boolean result = false;
        for (int index = 0; !result && index < left - right + 1; index = index + 1) {
            result = true;
            for (int offset = 0; result && offset < right; offset = offset + 1) {
                result = this.checkNode(
                    node.getChild(index + offset),
                    node,
                    sample.getChild(offset)
                );
            }
        }
        return result;
    }
}
