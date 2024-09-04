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
package org.cqfn.astranaut.core.algorithms;

import java.util.Collections;
import java.util.Optional;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.PrototypeBasedNode;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LabeledTreeBuilder}.
 * @since 2.0.0
 */
class LabeledTreeBuilderTest {
    @Test
    void testBaseMethods() {
        final Node target = LittleTrees.createIntegerLiteral(0);
        final Node original = LittleTrees.createAddition(
            LittleTrees.createVariable("x"),
            target
        );
        final String name = "bgcolor";
        final String value = "yellow";
        final LabeledTreeBuilder builder = new LabeledTreeBuilder(new Tree(original));
        final Tree labeled = builder.build(Collections.singleton(target), name, value);
        final Optional<Node> colored = new DepthFirstWalker(labeled.getRoot())
            .findFirst(node -> node.getTypeName().equals(target.getTypeName()));
        Assertions.assertTrue(colored.isPresent());
        Assertions.assertEquals(colored.get().getProperties().get(name), value);
        final Node root = labeled.getRoot();
        Assertions.assertTrue(root instanceof PrototypeBasedNode);
        Assertions.assertSame(((PrototypeBasedNode) root).getPrototype(), original);
    }
}
