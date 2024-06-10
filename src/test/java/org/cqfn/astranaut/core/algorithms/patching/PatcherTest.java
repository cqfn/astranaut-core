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
import org.cqfn.astranaut.core.DifferenceNode;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.DifferenceTreeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link Patcher} class.
 *
 * @since 1.1.5
 */
class PatcherTest {
    @Test
    void patchPatternWithReplacement() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.createByDescription("A(B, D)", nodes);
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(prepattern);
        builder.replaceNode(nodes.get("B").iterator().next(), DraftNode.createByDescription("C"));
        final DifferenceNode pattern = builder.getRoot();
        final Node tree = DraftNode.createByDescription("X(Y,A(B,D),Z)");
        final Patcher patcher = new DefaultPatcher();
        final Node result = patcher.patch(tree, pattern);
        final Node expected = DraftNode.createByDescription("X(Y,A(C,D),Z)");
        Assertions.assertTrue(expected.deepCompare(result));
    }

    @Test
    void patchPatternWithDeletion() {
        final Map<String, Set<Node>> nodes = new TreeMap<>();
        final Node prepattern = DraftNode.createByDescription("A(B, D)", nodes);
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(prepattern);
        builder.deleteNode(nodes.get("B").iterator().next());
        final DifferenceNode pattern = builder.getRoot();
        final Node tree = DraftNode.createByDescription("X(Y,A(B,D),Z)");
        final Patcher patcher = new DefaultPatcher();
        final Node result = patcher.patch(tree, pattern);
        final Node expected = DraftNode.createByDescription("X(Y,A(D),Z)");
        Assertions.assertTrue(expected.deepCompare(result));
    }
}
