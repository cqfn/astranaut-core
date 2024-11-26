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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.utils.Pair;

/**
 * Auxiliary structure for mapping child nodes. Contains a set of sections, i.e, subset of
 *  child nodes of one node and a corresponding but unmapped subset of child nodes of another node.
 * @since 2.0.0
 */
final class Unprocessed {
    /**
     * Sections of nodes that have not yet been processed.
     */
    private final List<Section> sections;

    /**
     * Constructor.
     * @param left First (left) node
     * @param right Second (right) node whose child nodes will be mapped with the child
     */
    Unprocessed(final ExtNode left, final ExtNode right) {
        this.sections = new LinkedList<>(
            Collections.singleton(
                new Section(left, right)
            )
        );
    }

    /**
     * Returns the first unprocessed section of nodes.
     * @return Object containing unprocessed nodes or {@code null} if no more sections
     */
    Section getFirstSection() {
        final Section section;
        if (this.sections.isEmpty()) {
            section = null;
        } else {
            section = this.sections.get(0);
        }
        return section;
    }

    /**
     * Returns the number of unprocessed sections.
     * @return Number of unprocessed sections
     */
    int getNumberOfSections() {
        return this.sections.size();
    }

    /**
     * Removes a node from the set of unprocessed nodes as mapped.
     *  This can change the number of unprocessed sections.
     * @param node The node to be deleted
     */
    void removeNode(final ExtNode node) {
        Section found = null;
        final ListIterator<Section> iterator = this.sections.listIterator();
        while (iterator.hasNext()) {
            final Section section = iterator.next();
            if (section.hasNode(node)) {
                found = section;
                break;
            }
        }
        do {
            if (found == null) {
                break;
            }
            final Section section = found.removeNode(node);
            iterator.remove();
            if (section != null) {
                iterator.add(section);
            }
        } while (false);
    }

    /**
     * Removes a node pair from the set of unprocessed nodes as mapped.
     *  The remaining nodes are redistributed into sections
     *  (see {@link Section#removeNodes(ExtNode, ExtNode)} for details.
     * @param node The child node of the left node to be deleted
     * @param corresponding The child node of the right node to be deleted,
     *  corresponding to the left node
     */
    void removeNodes(final ExtNode node, final ExtNode corresponding) {
        Section found = null;
        final ListIterator<Section> iterator = this.sections.listIterator();
        while (iterator.hasNext()) {
            final Section section = iterator.next();
            if (section.hasNode(node)) {
                found = section;
                break;
            }
        }
        do {
            if (found == null) {
                break;
            }
            iterator.remove();
            final Pair<Section, Section> pair = found.removeNodes(node, corresponding);
            if (pair.getKey() != null) {
                iterator.add(pair.getKey());
            }
            if (pair.getValue() != null) {
                iterator.add(pair.getValue());
            }
        } while (false);
    }
}
