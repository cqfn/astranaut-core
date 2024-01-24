/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.utils.visualizer.DotRender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link DotRender} class.
 *
 * @since 1.0.2
 */
class DotRenderTest {
    /**
     * Directory with test resources.
     */
    private static final String DIR = "src/test/resources/visualizer/";

    /**
     * Test for a single node.
     *
     * @throws IOException If input stream cannot be read from a DOT file
     */
    @Test
    void testSingleNode() throws IOException {
        final Node root = this.createNode("SingleNode");
        final String expected = new FilesReader(DIR.concat("testSingleNode.dot")).readAsString();
        final String result = this.renderDot(root);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test for a single node with data.
     *
     * @throws IOException If input stream cannot be read from a DOT file
     */
    @Test
    void testSingleNodeWithData() throws IOException {
        final Node root = this.createNode("Node", "data");
        final String expected = new FilesReader(
            DotRenderTest.DIR.concat("testSingleNodeWithData.dot")
        ).readAsString();
        final String result = this.renderDot(root);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test for a node with children.
     * The tree depth is 2.
     *
     * @throws IOException If input stream cannot be read from a DOT file
     */
    @Test
    void testNodeWithChildren() throws IOException {
        final List<Node> children = new LinkedList<>();
        children.add(this.createNode("ChildNode0"));
        children.add(this.createNode("ChildNode1", "value"));
        children.add(this.createNode("ChildNode2"));
        final Node root = this.createNode("RootNode", children);
        final String expected = new FilesReader(
            DotRenderTest.DIR.concat("testNodeWithChildren.dot")
        ).readAsString();
        final String result = this.renderDot(root);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test for a node with children.
     * The tree depth is 4.
     *
     * @throws IOException If input stream cannot be read from a DOT file
     */
    @Test
    void testNodeWithNestedChildren() throws IOException {
        final List<Node> thirdl = new LinkedList<>();
        thirdl.add(this.createNode("Child4"));
        final Node secondch = this.createNode("Child2", thirdl);
        final List<Node> secondl = new LinkedList<>();
        secondl.add(secondch);
        secondl.add(this.createNode("Child3"));
        final Node firstch = this.createNode("Child0", secondl);
        final List<Node> firstl = new LinkedList<>();
        firstl.add(firstch);
        firstl.add(this.createNode("Child1"));
        final Node root = this.createNode("Root", firstl);
        final String expected = new FilesReader(
            DotRenderTest.DIR.concat("testNodeWithNestedChildren.dot")
        ).readAsString();
        final String result = this.renderDot(root);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test for a node that contain a null child node.
     *
     * @throws IOException If input stream cannot be read from a DOT file
     */
    @Test
    void testNodeWithNullChild() throws IOException {
        final List<Node> children = new LinkedList<>();
        children.add(this.createNode("Child"));
        children.add(EmptyTree.INSTANCE);
        final Node root = this.createNode("TestNode", children);
        final String expected = new FilesReader(
            DotRenderTest.DIR.concat("testNodeWithNullChild.dot")
        ).readAsString();
        final String result = this.renderDot(root);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Renders DOT text from a node.
     *
     * @param node A tree to be converted
     */
    private String renderDot(final Node node) {
        final DotRender render = new DotRender(node);
        return render.render();
    }

    /**
     * Creates a node without data and children.
     * @param name The type name
     * @return A new node
     */
    private Node createNode(final String name) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(name);
        return ctor.createNode();
    }

    /**
     * Creates a node with data.
     * @param name The type name
     * @param data The data as string
     * @return A new node
     */
    private Node createNode(final String name, final String data) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(name);
        ctor.setData(data);
        return ctor.createNode();
    }

    /**
     * Creates a node with children.
     * @param name The type name
     * @param children The list of children
     * @return A new node
     */
    private Node createNode(final String name, final List<Node> children) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(name);
        ctor.setChildrenList(children);
        return ctor.createNode();
    }
}
