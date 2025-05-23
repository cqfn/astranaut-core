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
package org.cqfn.astranaut.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.CoreException;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.NodeAndType;
import org.cqfn.astranaut.core.base.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link TreeVisualizer} class.
 * @since 1.0.2
 */
class TreeVisualizerTest {
    @Test
    void testSingleNodeVisualization(@TempDir final Path temp) {
        final Tree tree = Tree.createDraft("TestNode<\"value\">");
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        final Path img = temp.resolve("node.png");
        Assertions.assertDoesNotThrow(
            () -> {
                visualizer.visualize(new File(img.toString()));
            }
        );
    }

    @Test
    void testNullNodeVisualization(@TempDir final Path temp) {
        final Tree tree = EmptyTree.INSTANCE;
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        final Path img = temp.resolve("null.png");
        Assertions.assertDoesNotThrow(
            () -> {
                visualizer.visualize(new File(img.toString()));
            }
        );
    }

    @Test
    void testNodeVisualizationWithEncoding(@TempDir final Path temp) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("DataNode");
        ctor.setData("<va\'l&u\"e>");
        final Tree tree = new Tree(ctor.createNode());
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        final Path img = temp.resolve("data.png");
        Assertions.assertDoesNotThrow(
            () -> {
                visualizer.visualize(new File(img.toString()));
            }
        );
    }

    @Test
    void testTreeVisualization(@TempDir final Path temp) {
        final DraftNode.Constructor addition = new DraftNode.Constructor();
        addition.setName("Addition");
        final DraftNode.Constructor left = new DraftNode.Constructor();
        left.setName("IntegerLiteral");
        left.setData("2");
        final DraftNode.Constructor right = new DraftNode.Constructor();
        right.setName("DoubleLiteral");
        right.setData("3");
        addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
        final Tree tree = new Tree(addition.createNode());
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        final Path img = temp.resolve("tree.svg");
        Assertions.assertDoesNotThrow(
            () -> {
                visualizer.visualize(new File(img.toString()));
            }
        );
    }

    @Test
    void testWrongExtension(@TempDir final Path temp) {
        final Tree tree = Tree.createDraft("Exception");
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        Path img = temp.resolve("node.jpg");
        boolean oops = false;
        try {
            visualizer.visualize(new File(img.toString()));
        } catch (final CoreException exception) {
            oops = true;
            Assertions.assertNotNull(exception.getInitiator());
            Assertions.assertNotNull(exception.getErrorMessage());
        } catch (final IOException ignored) {
        }
        Assertions.assertTrue(oops);
        img = temp.resolve("node");
        oops = false;
        try {
            visualizer.visualize(new File(img.toString()));
        } catch (final CoreException | IOException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void testColoredNodeVisualization(@TempDir final Path temp) {
        final Tree tree = new Tree(new ColoredNode());
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        final Path img = temp.resolve("node.svg");
        Assertions.assertDoesNotThrow(
            () -> {
                visualizer.visualize(new File(img.toString()));
            }
        );
        final FilesReader reader = new FilesReader(img.toString());
        final String content = reader.readAsStringNoExcept();
        Assertions.assertTrue(content.contains("fill=\"yellow\""));
        Assertions.assertTrue(content.contains("stroke=\"red\""));
    }

    @Test
    void testNodeWithLongDataVisualization(@TempDir final Path temp) {
        final Tree tree = Tree.createDraft(
            "XXX<'q123456789 123456789 123456789 123456789 123456789 123456789 123456789'>"
        );
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        final Path img = temp.resolve("node.svg");
        Assertions.assertDoesNotThrow(
            () -> {
                visualizer.visualize(new File(img.toString()));
            }
        );
        final FilesReader reader = new FilesReader(img.toString());
        final String content = reader.readAsStringNoExcept();
        Assertions.assertTrue(
            content.contains(
                "q123456789 123456789 123456789 123456789 123456789 123456789..."
            )
        );
    }

    @Test
    void testNodeWithContinuousDataVisualization(@TempDir final Path temp) {
        final Tree tree = Tree.createDraft(
            "XXX<'q123456789012345678901234567890123456789012345678901234567890123456789'>"
        );
        final TreeVisualizer visualizer = new TreeVisualizer(tree);
        final Path img = temp.resolve("node.svg");
        Assertions.assertDoesNotThrow(
            () -> {
                visualizer.visualize(new File(img.toString()));
            }
        );
        final FilesReader reader = new FilesReader(img.toString());
        final String content = reader.readAsStringNoExcept();
        Assertions.assertTrue(
            content.contains(
                "q123456789012345678901234567890123456789012345678901234567890123..."
            )
        );
    }

    /**
     * Colored node for test purposes.
     * @since 2.0.0
     */
    private static final class ColoredNode extends NodeAndType {
        @Override
        public String getName() {
            return "Colored";
        }

        @Override
        public Builder createBuilder() {
            return null;
        }

        @Override
        public String getData() {
            return "";
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public Node getChild(final int index) {
            throw new IllegalStateException();
        }

        @Override
        public Map<String, String> getProperties() {
            return new MapUtils<String, String>()
                .put("color", "red")
                .put("bgcolor", "yellow")
                .make();
        }
    }
}
