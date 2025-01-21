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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.algorithms.DiffTreeBuilder;
import org.cqfn.astranaut.core.algorithms.PatternBuilder;
import org.cqfn.astranaut.core.base.DiffNode;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Pattern;
import org.cqfn.astranaut.core.base.PatternNode;
import org.cqfn.astranaut.core.base.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link Matcher} class.
 * @since 1.1.5
 */
class MatcherTest {
    @Test
    void findSubtreeInATree() {
        final Tree tree = Tree.createDraft("X(Y(A(B,C)),A(B,C),A(B,D))");
        final DiffNode subtree = new DiffNode(DraftNode.create("A(B,C)"));
        final Pattern pattern = new Pattern(new PatternNode(subtree));
        final Matcher matcher = new Matcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(2, found.size());
        for (final Node node : found) {
            Assertions.assertEquals("A", node.getTypeName());
        }
    }

    @Test
    void findReducedSubtreeInATree() {
        final Tree tree = Tree.createDraft("X(A(B,C(F),D,E),A(B,D,E),A(B))");
        final DiffNode subtree = new DiffNode(
            DraftNode.create("A(C(F),D)")
        );
        final Matcher matcher = new Matcher(tree);
        final Pattern pattern = new Pattern(new PatternNode(subtree));
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
        final DiffTreeBuilder builder = new DiffTreeBuilder(prepattern);
        builder.insertNode(
            new Insertion(
                DraftNode.create("C"),
                prepattern,
                nodes.get("B").iterator().next()
            )
        );
        final Pattern pattern = new Pattern(new PatternNode(builder.getDiffTree().getRoot()));
        final Tree tree = Tree.createDraft("X(Y,A(B),Z)");
        final Matcher matcher = new Matcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void findPatternWithReplacementInATree() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D)", nodes);
        final DiffTreeBuilder builder = new DiffTreeBuilder(prepattern);
        builder.replaceNode(nodes.get("B").iterator().next(), DraftNode.create("C"));
        final Pattern pattern = new Pattern(new PatternNode(builder.getDiffTree().getRoot()));
        final Tree tree = Tree.createDraft("X(Y,A(B,D),Z)");
        final Matcher matcher = new Matcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void findPatternWithDeletionInATree() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D)", nodes);
        final DiffTreeBuilder builder = new DiffTreeBuilder(prepattern);
        builder.deleteNode(nodes.get("B").iterator().next());
        final Pattern pattern = new Pattern(new PatternNode(builder.getDiffTree().getRoot()));
        final Tree tree = Tree.createDraft("X(Y,A(B,D),Z)");
        final Matcher matcher = new Matcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void findPatternWithHoleInATree() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D<\"7\">)", nodes);
        final DiffTreeBuilder dtbuilder = new DiffTreeBuilder(prepattern);
        dtbuilder.deleteNode(nodes.get("B").iterator().next());
        final PatternBuilder pbuilder = new PatternBuilder(dtbuilder.getDiffTree());
        final boolean flag = pbuilder.makeHole(nodes.get("D").iterator().next(), 0);
        Assertions.assertTrue(flag);
        final Pattern pattern = pbuilder.getPattern();
        final Tree tree = Tree.createDraft("X(Y,A(B,D<\"11\">),Z)");
        final Matcher matcher = new Matcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
        final Node node = found.iterator().next();
        Assertions.assertEquals("A", node.getTypeName());
    }

    @Test
    void matchPatternWithData() {
        final Pattern pattern = new Pattern(
            new PatternNode(
                new DiffNode(
                    DraftNode.create("A(B<\"test\">)")
                )
            )
        );
        final Tree tree = Tree.createDraft("X(Y, A(B<\"test\">), Z)");
        final Matcher matcher = new Matcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(1, found.size());
    }

    @Test
    void matchPatternThatIsTooBig() {
        final Pattern pattern = new Pattern(
            new PatternNode(
                new DiffNode(
                    DraftNode.create("A(B,C,D,E,F)")
                )
            )
        );
        final Tree tree = Tree.createDraft("X(Y,A(B,C),Z)");
        final Matcher matcher = new Matcher(tree);
        final Set<Node> found = matcher.match(pattern);
        Assertions.assertEquals(0, found.size());
    }
}
