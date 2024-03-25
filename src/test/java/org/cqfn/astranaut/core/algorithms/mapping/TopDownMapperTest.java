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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TopDownMapper} class.
 *
 * @since 1.0
 */
class TopDownMapperTest {
    @Test
    void testIdenticalTrees() {
        final String description = "A(B(C, D))";
        final Node first = TopDownMapperTest.createTree(description);
        final Node second = TopDownMapperTest.createTree(description);
        final Mapper mapper = new TopDownMapper();
        final Mapping mapping = mapper.map(first, second);
        Assertions.assertEquals(mapping.getRight(first), second);
        Assertions.assertEquals(mapping.getLeft(second), first);
    }

    /**
     * Creates a tree based on its description. Description format: A(B,C(...),...) where 'A'
     * is the type name (may consist of only one capital letter) followed by child nodes
     * (in the same format) in parentheses separated by commas.
     * @param description Description
     * @return Root node of the tree created by description
     */
    private static Node createTree(final String description) {
        final CharacterIterator iterator = new StringCharacterIterator(description);
        return TopDownMapperTest.createNode(iterator);
    }

    /**
     * Creates a tree based on its description (recursive method).
     * @param iterator Iterator by description characters
     * @return Node of the tree with its children, created by description
     */
    private static Node createNode(final CharacterIterator iterator) {
        final char name = iterator.current();
        final Node result;
        if (name >= 'A' && name <= 'Z') {
            final DraftNode.Constructor builder = new DraftNode.Constructor();
            builder.setName(String.valueOf(name));
            final char next = iterator.next();
            if (next == '(') {
                builder.setChildrenList(TopDownMapperTest.parseChildrenList(iterator));
            }
            result = builder.createNode();
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Parses children list from description.
     * @param iterator Iterator by description characters
     * @return Children list, created by description
     */
    private static List<Node> parseChildrenList(final CharacterIterator iterator) {
        final List<Node> children = new LinkedList<>();
        char next;
        do {
            iterator.next();
            final Node child = TopDownMapperTest.createNode(iterator);
            if (child != null) {
                children.add(child);
            }
            next = iterator.current();
            assert next == ')' || next == ',' || next == ' ';
        } while (next != ')');
        return children;
    }
}
