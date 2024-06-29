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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.core.algorithms.DepthFirstWalker;
import org.cqfn.astranaut.core.base.Action;
import org.cqfn.astranaut.core.base.ActionList;
import org.cqfn.astranaut.core.base.Delete;
import org.cqfn.astranaut.core.base.Hole;
import org.cqfn.astranaut.core.base.Insert;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Pattern;
import org.cqfn.astranaut.core.base.PatternNode;
import org.cqfn.astranaut.core.base.Replace;
import org.cqfn.astranaut.core.base.Tree;

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
     * List of actions to be performed on the original tree to apply the pattern.
     */
    private final ActionList actions;

    /**
     * Constructor.
     * @param tree The syntax tree in which patterns will be searched
     */
    Matcher(final Tree tree) {
        this.root = tree.getRoot();
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
    Set<Node> match(final Pattern pattern) {
        final DepthFirstWalker deep = new DepthFirstWalker(this.root);
        final PatternNode head = pattern.getRoot();
        final List<Node> preset = deep.findAll(
            node -> node.getTypeName().equals(head.getTypeName())
                && node.getData().equals(head.getData())
        );
        final Set<Node> set = new HashSet<>();
        for (final Node node : preset) {
            final boolean matches = this.checkNode(node, null, head);
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
        boolean result = node.getTypeName().equals(sample.getTypeName());
        if (!(pattern instanceof Hole)) {
            result = result && node.getData().equals(sample.getData());
            result = result && (node.getChildCount() == 0 || this.checkChildren(node, sample));
        }
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
        for (int index = 0; !result && index < left; index = index + 1) {
            result = true;
            final Iterator<Node> iterator = sample.getIteratorOverChildren();
            int offset = 0;
            Node current = null;
            while (result && offset < right && iterator.hasNext()) {
                final Node child = iterator.next();
                if (child instanceof Insert) {
                    this.actions.insertNodeAfter(((Insert) child).getAfter(), node, current);
                } else if (index + offset >= left) {
                    result = false;
                } else {
                    current = node.getChild(index + offset);
                    result = this.checkNode(
                        current,
                        node,
                        child
                    );
                    offset = offset + 1;
                }
            }
        }
        return result;
    }
}
