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
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.visualizer.DotGenerator;
import org.cqfn.astranaut.core.utils.visualizer.ImageRender;
import org.cqfn.astranaut.core.utils.visualizer.WrongFileExtension;

/**
 * Visualizer of trees.
 * @since 1.0.2
 */
public final class TreeVisualizer {
    /**
     * The tree to be visualized.
     */
    private final Tree tree;

    /**
     * Constructor.
     * @param tree The tree to be visualized
     */
    public TreeVisualizer(final Tree tree) {
        this.tree = tree;
    }

    /**
     * Renders a DOT text of the tree, renders an image from it and
     *  saves it to the specified file.
     * @param file A file of the AST visualization
     * @throws IOException If an error during input or output actions occurs
     * @throws WrongFileExtension If an error during visualization occurs
     */
    public void visualize(final File file) throws IOException, WrongFileExtension {
        final DotGenerator render = new DotGenerator(this.tree);
        final String dot = render.generate();
        final ImageRender image = new ImageRender(dot);
        image.render(file);
    }
}
