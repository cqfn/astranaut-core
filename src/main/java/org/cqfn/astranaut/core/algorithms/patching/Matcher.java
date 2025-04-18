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
package org.cqfn.astranaut.core.algorithms.patching;

import java.util.Collections;
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
 * @since 1.1.5
 */
class Matcher {
    /**
     * Root node of the tree in which patterns are searched.
     */
    private final Node root;

    /**
     * Set of nodes mapped to the root of the pattern.
     */
    private final Set<Node> found;

    /**
     * Constructor.
     * @param tree The syntax tree in which patterns will be searched
     */
    Matcher(final Tree tree) {
        this.root = tree.getRoot();
        this.found = new HashSet<>();
    }

    /**
     * Matches the tree and the pattern.
     * @param pattern Root node of the pattern
     * @return Actions extracted from the pattern, applicable as a result of matching
     *  to the nodes of the tree
     */
    ActionList match(final Pattern pattern) {
        final DepthFirstWalker deep = new DepthFirstWalker(this.root);
        final PatternNode head = pattern.getRoot();
        final List<Node> preset = deep.findAll(
            node -> node.getTypeName().equals(head.getTypeName())
                && node.getData().equals(head.getData())
        );
        final Matched matched = new Matched();
        for (final Node node : preset) {
            final Matched applicants = new Matched();
            final boolean matches = Matcher.checkNode(node, head, applicants);
            if (matches) {
                this.found.add(node);
                matched.merge(applicants);
            }
        }
        return matched;
    }

    /**
     * Returns the set of nodes mapped to the root of the pattern.
     * @return The set of nodes found.
     *  The size of this set essentially means how many times the pattern has been applied
     */
    Set<Node> getFoundNodes() {
        return Collections.unmodifiableSet(this.found);
    }

    /**
     * Checks if the node of the original tree matches the pattern node.
     * @param node Node of the original tree
     * @param pattern Node of the difference tree (i.e. pattern)
     * @param matched Intermediate data obtained by matching subtrees
     * @return Matching result ({@code true} if matches)
     */
    private static boolean checkNode(
        final Node node, final Node pattern, final Matched matched) {
        final Node sample;
        final Action action = Action.toAction(pattern);
        if (action instanceof Replace || action instanceof Delete) {
            sample = action.getBefore();
        } else {
            sample = pattern;
        }
        boolean result = node.getTypeName().equals(sample.getTypeName());
        do {
            if (!result) {
                break;
            }
            if (pattern instanceof Hole) {
                result = matched.checkHole(((Hole) pattern).getNumber(), node.getData());
            } else {
                result = node.getData().equals(sample.getData())
                    && Matcher.matchNodeAndSample(node, sample, matched);
            }
            if (!result) {
                break;
            }
            if (action instanceof Replace) {
                matched.replaceNode(node, action.getAfter());
            } else if (action instanceof Delete) {
                matched.deleteNode(node);
            }
        } while (false);
        return result;
    }

    /**
     * Matches a node and a sample provided the types are equal, the data is equal,
     *  and the sample is not an action.
     * @param node Node of the original tree
     * @param sample Sample to match
     * @param matched Intermediate data obtained by matching subtrees
     * @return Matching result ({@code true} if matches)
     */
    private static boolean matchNodeAndSample(final Node node, final Node sample,
        final Matched matched) {
        final boolean result;
        if (node.getChildCount() == 0) {
            result = Matcher.mathEmptyNodeAndSample(node, sample, matched);
        } else {
            result = Matcher.checkChildren(node, sample, matched);
        }
        return result;
    }

    /**
     * Matches a node without children and a sample.
     * @param node Node of the original tree
     * @param sample Sample to match
     * @param matched Intermediate data obtained by matching subtrees
     * @return Matching result ({@code true} if matches)
     */
    private static boolean mathEmptyNodeAndSample(final Node node, final Node sample,
        final Matched matched) {
        boolean result = true;
        final int count = sample.getChildCount();
        Node previous = null;
        for (int index = 0; index < count; index = index + 1) {
            final Action action = Action.toAction(sample.getChild(index));
            if (action instanceof Insert) {
                final Node insertion = action.getAfter();
                matched.insertNodeAfter(insertion, node, previous);
                previous = insertion;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Checks if the children of the node of the original tree
     *  matches the children of the pattern node.
     * @param node Node of the original tree
     * @param sample Node of the difference tree (i.e. pattern)
     * @param matched Intermediate data obtained by matching subtrees
     * @return Matching result ({@code true} if matches)
     */
    private static boolean checkChildren(
        final Node node, final Node sample, final Matched matched) {
        final int left = node.getChildCount();
        final int right = sample.getChildCount();
        boolean result = false;
        final Matched applicants = new Matched();
        for (int index = 0; !result && index < left; index = index + 1) {
            result = true;
            final Iterator<Node> iterator = sample.getIteratorOverChildren();
            int offset = 0;
            Node previous = null;
            while (result && offset < right && iterator.hasNext()) {
                final Node child = iterator.next();
                final Action action = Action.toAction(child);
                if (action instanceof Insert) {
                    final Node insertion = action.getAfter();
                    applicants.insertNodeAfter(insertion, node, previous);
                    previous = insertion;
                } else if (index + offset >= left) {
                    result = false;
                } else {
                    final Node current = node.getChild(index + offset);
                    result = Matcher.checkNode(
                        current,
                        child,
                        applicants
                    );
                    previous = current;
                    offset = offset + 1;
                }
            }
        }
        if (result) {
            matched.merge(applicants);
        }
        return result;
    }
}
