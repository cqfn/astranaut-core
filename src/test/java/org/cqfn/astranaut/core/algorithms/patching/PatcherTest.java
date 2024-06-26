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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.core.algorithms.DiffTreeBuilder;
import org.cqfn.astranaut.core.algorithms.PatternBuilder;
import org.cqfn.astranaut.core.base.*;
import org.cqfn.astranaut.core.example.green.Addition;
import org.cqfn.astranaut.core.example.green.ExpressionStatement;
import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.cqfn.astranaut.core.example.green.SimpleAssignment;
import org.cqfn.astranaut.core.example.green.Variable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link Patcher} class.
 *
 * @since 1.1.5
 */
class PatcherTest {
    @Test
    void patchingByPatternThatDoesNotMatch() {
        final Tree source = Tree.createDraft("A(B,C,D)");
        final PatternNode pattern = new PatternNode(
            new DiffNode(
                DraftNode.create("D")
            )
        );
        final Patcher patcher = new DefaultPatcher();
        final Tree result = patcher.patch(source, pattern);
        Assertions.assertTrue(source.deepCompare(result));
    }

    @Test
    void patchPatternWithInsertion() {
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
        final PatternNode pattern = new PatternNode(builder.getDiffTree().getRoot());
        final Tree tree = Tree.createDraft("X(Y,A(B),Z)");
        final Patcher patcher = new DefaultPatcher();
        final Tree result = patcher.patch(tree, pattern);
        final Tree expected = Tree.createDraft("X(Y,A(B,C),Z)");
        Assertions.assertTrue(expected.deepCompare(result));
    }

    @Test
    void patchPatternWithReplacement() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D)", nodes);
        final DiffTreeBuilder builder = new DiffTreeBuilder(prepattern);
        builder.replaceNode(nodes.get("B").iterator().next(), DraftNode.create("C"));
        final PatternNode pattern = new PatternNode(builder.getDiffTree().getRoot());
        final Tree tree = Tree.createDraft("X(Y,A(B,D),Z)");
        final Patcher patcher = new DefaultPatcher();
        final Tree result = patcher.patch(tree, pattern);
        final Tree expected = Tree.createDraft("X(Y,A(C,D),Z)");
        Assertions.assertTrue(expected.deepCompare(result));
    }

    @Test
    void patchPatternWithDeletion() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("A(B, D)", nodes);
        final DiffTreeBuilder builder = new DiffTreeBuilder(prepattern);
        builder.deleteNode(nodes.get("B").iterator().next());
        final PatternNode pattern = new PatternNode(builder.getDiffTree().getRoot());
        final Tree tree = Tree.createDraft("X(Y,A(B,D),Z)");
        final Patcher patcher = new DefaultPatcher();
        final Tree result = patcher.patch(tree, pattern);
        final Tree expected = Tree.createDraft("X(Y,A(D),Z)");
        Assertions.assertTrue(expected.deepCompare(result));
    }

    @Test
    void patchPatternWithHole() {
        Builder ctor = new Variable.Constructor();
        ctor.setData("a");
        Node first = ctor.createNode();
        ctor = new IntegerLiteral.Constructor();
        ctor.setData("1");
        Node second = ctor.createNode();
        ctor = new Addition.Constructor();
        ctor.setChildrenList(Arrays.asList(first, second));
        Node addition = ctor.createNode();
        ctor = new Variable.Constructor();
        ctor.setData("x");
        Node variable = ctor.createNode();
        ctor = new SimpleAssignment.Constructor();
        ctor.setChildrenList(Arrays.asList(variable, addition));
        Node assignment = ctor.createNode();
        ctor = new ExpressionStatement.Constructor();
        ctor.setChildrenList(Collections.singletonList(assignment));
        final Node stmt = ctor.createNode();
        final Patcher patcher = new DefaultPatcher();
        final Pattern pattern = PatcherTest.createPatternWithHole();
        final Tree patched = patcher.patch(new Tree(stmt), pattern.getRoot());
        ctor = new Variable.Constructor();
        ctor.setData("a");
        first = ctor.createNode();
        ctor = new IntegerLiteral.Constructor();
        ctor.setData("2");
        second = ctor.createNode();
        ctor = new Addition.Constructor();
        ctor.setChildrenList(Arrays.asList(first, second));
        addition = ctor.createNode();
        ctor = new Variable.Constructor();
        ctor.setData("x");
        variable = ctor.createNode();
        ctor = new SimpleAssignment.Constructor();
        ctor.setChildrenList(Arrays.asList(variable, addition));
        assignment = ctor.createNode();
        ctor = new ExpressionStatement.Constructor();
        ctor.setChildrenList(Collections.singletonList(assignment));
        final Node expected = ctor.createNode();
        Assertions.assertTrue(expected.deepCompare(patched.getRoot()));
    }

    @Test
    void patchWithPatternThatDoesNotMatch() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.create("E(B,D)", nodes);
        final DiffTreeBuilder builder = new DiffTreeBuilder(prepattern);
        builder.replaceNode(nodes.get("B").iterator().next(), DraftNode.create("C"));
        final PatternNode pattern = new PatternNode(builder.getDiffTree().getRoot());
        final Tree tree = Tree.createDraft("X(Y,K(B,D),Z)");
        final Patcher patcher = new DefaultPatcher();
        final Tree result = patcher.patch(tree, pattern);
        Assertions.assertTrue(tree.deepCompare(result));
    }

    /**
     * Creates pattern with a hole.
     * @return A pattern
     */
    private static Pattern createPatternWithHole() {
        Builder ctor = new Variable.Constructor();
        ctor.setData("w");
        final Node first = ctor.createNode();
        ctor = new IntegerLiteral.Constructor();
        ctor.setData("1");
        final Node second = ctor.createNode();
        ctor = new Addition.Constructor();
        ctor.setChildrenList(Arrays.asList(first, second));
        final Node addition = ctor.createNode();
        ctor = new IntegerLiteral.Constructor();
        ctor.setData("2");
        final Node replacement = ctor.createNode();
        final DiffTreeBuilder dtbld = new DiffTreeBuilder(addition);
        dtbld.replaceNode(second, replacement);
        final PatternBuilder pbld = new PatternBuilder(dtbld.getDiffTree().getRoot());
        pbld.makeHole(first, 0);
        final Pattern pattern = pbld.getPattern();
        Assertions.assertNotNull(pattern);
        return pattern;
    }
}
