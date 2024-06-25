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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.algorithms.DifferenceTreeBuilder;
import org.cqfn.astranaut.core.algorithms.PatternBuilder;
import org.cqfn.astranaut.core.base.DiffNode;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.PatternNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link PatternMatcher} class.
 *
 * @since 1.1.5
 */
class PatternMatcherTest {
    @Test
    void findSubtreeInATree() {
        final Node tree = DraftNode.create("X(Y(A(B,C)),A(B,C),A(B,D))");
        final DiffNode subtree = new DiffNode(DraftNode.create("A(B,C)"));
        final PatternNode pattern = new PatternNode(subtree);
        final PatternMatcher matcher = new PatternMatcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(2, found.size());
        for (final Node node : found) {
            Assertions.assertEquals("A", node.getTypeName());
        }
    }

    @Test
    void findReducedSubtreeInATree() {
        final Node tree = DraftNode.create("X(A(B,C(F),D,E),A(B,D,E),A(B))");
        final DiffNode subtree = new DiffNode(
            DraftNode.create("A(C(F),D)")
        );
        final PatternMatcher matcher = new PatternMatcher(tree);
        final PatternNode pattern = new PatternNode(subtree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
        Assertions.assertEquals(4, node.getChildCount());
    }

    @Test
    void findPatternWithInsertionInATree() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B)", nodes);
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(prepattern);
        builder.insertNode(
            new Insertion(
                DraftNode.create("C"),
                prepattern,
                nodes.get("B").iterator().next()
            )
        );
        final PatternNode pattern = new PatternNode(builder.getRoot());
        final Node tree = DraftNode.create("X(Y,A(B),Z)");
        final PatternMatcher matcher = new PatternMatcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void findPatternWithReplacementInATree() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D)", nodes);
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(prepattern);
        builder.replaceNode(nodes.get("B").iterator().next(), DraftNode.create("C"));
        final PatternNode pattern = new PatternNode(builder.getRoot());
        final Node tree = DraftNode.create("X(Y,A(B,D),Z)");
        final PatternMatcher matcher = new PatternMatcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void findPatternWithDeletionInATree() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D)", nodes);
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(prepattern);
        builder.deleteNode(nodes.get("B").iterator().next());
        final PatternNode pattern = new PatternNode(builder.getRoot());
        final Node tree = DraftNode.create("X(Y,A(B,D),Z)");
        final PatternMatcher matcher = new PatternMatcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void findPatternWithHoleInATree() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D<\"7\">)", nodes);
        final DifferenceTreeBuilder dtbuilder = new DifferenceTreeBuilder(prepattern);
        dtbuilder.deleteNode(nodes.get("B").iterator().next());
        final PatternBuilder pbuilder = new PatternBuilder(dtbuilder.getRoot());
        final boolean flag = pbuilder.makeHole(nodes.get("D").iterator().next(), 0);
        Assertions.assertTrue(flag);
        final PatternNode pattern = pbuilder.getRoot();
        final Node tree = DraftNode.create("X(Y,A(B,D<\"11\">),Z)");
        final PatternMatcher matcher = new PatternMatcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void matchPatternWithData() {
        final PatternNode pattern = new PatternNode(
            new DiffNode(
                DraftNode.create("A(B<\"test\">)")
            )
        );
        final Node tree = DraftNode.create("X(Y, A(B<\"test\">), Z)");
        final PatternMatcher matcher = new PatternMatcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
    }

    @Test
    void matchPatternThatIsTooBig() {
        final PatternNode pattern = new PatternNode(
            new DiffNode(
                DraftNode.create("A(B,C,D,E,F)")
            )
        );
        final Node tree = DraftNode.create("X(Y,A(B,C),Z)");
        final PatternMatcher matcher = new PatternMatcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(0, found.size());
    }
}
