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
package org.cqfn.astranaut.core.algorithms;

import java.util.Arrays;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link NodeReplacer} class.
 * @since 1.0
 */
class NodeReplacerTest {
    @Test
    void testReplacementOfRoot() {
        final Node root =
            this.createNode(
                "1",
                "",
                this.createNode(
                    "11",
                    "",
                    this.createNode(
                        "21",
                        ""
                    ),
                    this.createNode(
                        "22",
                        ""
                    )
                ),
                this.createNode("12", ""),
                this.createNode(
                    "13",
                    "",
                    this.createNode(
                        "23",
                        ""
                    ),
                    this.createNode(
                        "24",
                        ""
                    )
                )
            );
        final Node target = this.createTargetTree();
        final Pair<Node, Integer> result = new NodeReplacer().replace(root, root, target);
        Assertions.assertEquals(target, result.getKey());
        Assertions.assertEquals(-1, result.getValue());
    }

    @Test
    void testReplacementOfRootChild() {
        final Node source =
            this.createNode(
                "130",
                "",
                this.createNode(
                    "230",
                    ""
                ),
                this.createNode(
                    "240",
                    ""
                )
            );
        final Node root =
            this.createNode(
                "10",
                "",
                this.createNode(
                    "110",
                    "",
                    this.createNode(
                        "210",
                        ""
                    ),
                    this.createNode(
                        "220",
                        ""
                    )
                ),
                this.createNode("120", ""),
                source
            );
        final Node target = this.createTargetTree();
        final Pair<Node, Integer> result = new NodeReplacer().replace(root, source, target);
        Assertions.assertEquals(target, result.getKey().getChild(2));
        Assertions.assertEquals(2, result.getValue());
    }

    @Test
    void testReplacementOfRootGrandChild() {
        final Node source = this.createNode("2300", "");
        final Node left =
            this.createNode(
            "1100",
            "",
            this.createNode(
                "2100",
                ""
            ),
            this.createNode(
                "2200",
                ""
            )
        );
        final Node mid = this.createNode("1200", "");
        final Node root = this.createNode(
            "100",
            "",
            left,
            mid,
            this.createNode(
                "1300",
                "",
                source,
                this.createNode(
                    "2400",
                    ""
                )
            )
        );
        final Node target = this.createTargetTree();
        final Pair<Node, Integer> result = new NodeReplacer().replace(root, source, target);
        Assertions.assertEquals(target, result.getKey().getChild(2).getChild(0));
        Assertions.assertEquals(left, result.getKey().getChild(0));
        Assertions.assertEquals(mid, result.getKey().getChild(1));
        Assertions.assertEquals(2, result.getValue());
    }

    @Test
    void testReplacementWithoutMatch() {
        final Node root =
            this.createNode(
                "111",
                "",
                this.createNode("222", ""),
                this.createNode("333", "")
        );
        final Node source = this.createNode("444", "");
        final Node target = this.createTargetTree();
        final Pair<Node, Integer> result = new NodeReplacer().replace(root, source, target);
        Assertions.assertEquals(DummyNode.INSTANCE, result.getKey());
        Assertions.assertEquals(-1, result.getValue());
    }

    private Node createTargetTree() {
        return this.createNode(
            "2",
            "",
            this.createNode(
                "14",
                ""
            )
        );
    }

    /**
     * Creates node for test purposes.
     * @param type The type name
     * @param data The data (in a textual format)
     * @param children The list of children
     * @return A new node
     */
    private Node createNode(final String type, final String data, final Node... children) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setData(data);
        ctor.setChildrenList(Arrays.asList(children));
        return ctor.createNode();
    }
}
