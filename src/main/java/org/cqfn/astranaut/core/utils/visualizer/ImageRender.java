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
package org.cqfn.astranaut.core.utils.visualizer;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Renders an image from a tree using the external Graphviz tool.
 *
 * @since 1.0.2
 */
public class ImageRender {
    /**
     * DOT text with a tree description.
     */
    private final String dot;

    /**
     * Constructor.
     *
     * @param dot The DOT file text
     */
    public ImageRender(final String dot) {
        this.dot = dot;
    }

    /**
     * Renders data a graphical format.
     * @param file A file of the tree visualization
     * @throws WrongFileExtension If a file extension is invalid
     * @throws IOException If an error during input or output actions occurs
     */
    public void render(final File file) throws WrongFileExtension, IOException {
        final Enum<Format> format = getFileExtension(file.getPath());
        final MutableGraph graph = new Parser().read(this.dot);
        Graphviz.useEngine(new GraphvizV8Engine());
        Graphviz.fromGraph(graph).render((Format) format).toFile(file);
    }

    /**
     * Get supported graphical file extension.
     * @param path A path to the file to be rendered
     * @return A file extension
     * @throws WrongFileExtension If a file extension is invalid
     */
    private static Enum<Format> getFileExtension(final String path) throws WrongFileExtension {
        final Optional<String> optional = Optional.ofNullable(path)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(path.lastIndexOf('.') + 1));
        Enum<Format> format = null;
        if (optional.isPresent()) {
            final String ext = optional.get();
            if ("png".equals(ext)) {
                format = Format.PNG;
            }
            if ("svg".equals(ext)) {
                format = Format.SVG;
            }
        }
        if (format == null) {
            throw WrongFileExtension.INSTANCE;
        }
        return format;
    }
}
