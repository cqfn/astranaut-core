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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.cqfn.astranaut.core.base.Char;
import org.cqfn.astranaut.core.base.DefaultFragment;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.parsing.FileSource;
import org.cqfn.astranaut.core.utils.parsing.StringSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for package ({@link FileSource}, etc).
 * @since 2.0.0
 */
class ParserTest {
    /**
     * File containing test code for parsing.
     */
    private static final String TEST_FILE = "src/test/resources/c/simple.c";

    @Test
    void parsingFileToCharNodes() {
        final FileSource source = new FileSource(ParserTest.TEST_FILE);
        final List<Char> nodes = source.parseIntoList();
        Assertions.assertFalse(nodes.isEmpty());
        Char xxx = null;
        for (final Char node : nodes) {
            if (node.getData().equals("x")) {
                xxx = node;
                break;
            }
        }
        Assertions.assertNotNull(xxx);
        final Fragment fragment = xxx.getFragment();
        Assertions.assertEquals("x", fragment.getCode());
        Assertions.assertEquals(
            ParserTest.TEST_FILE.concat(", 4.13-4.13"),
            fragment.getPosition()
        );
    }

    @Test
    void parsingFileToTree() {
        final FileSource source = new FileSource(ParserTest.TEST_FILE);
        final Tree tree = source.parseIntoTree();
        Assertions.assertTrue(tree.getRoot().getChildCount() > 0);
    }

    @Test
    void iteratorsAndFragments() {
        final FileSource source = new FileSource(ParserTest.TEST_FILE);
        final Iterator<Char> iterator = source.iterator();
        Char first = null;
        Char second = null;
        Char third = null;
        Char last = null;
        while (iterator.hasNext()) {
            final Char node = iterator.next();
            final String data = node.getData();
            switch (data) {
                case "{":
                    first = node;
                    break;
                case "p":
                    second = node;
                    break;
                case ";":
                    third = node;
                    break;
                case "}":
                    last = node;
                    break;
                default:
                    break;
            }
        }
        Assertions.assertThrows(NoSuchElementException.class, iterator::next);
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertNotNull(third);
        Assertions.assertNotNull(last);
        Fragment fragment = new DefaultFragment(
            second.getFragment().getBegin(),
            third.getFragment().getEnd()
        );
        Assertions.assertEquals("printf(\"x\");", fragment.getCode());
        Assertions.assertEquals(
            ParserTest.TEST_FILE.concat(", 4.5-4.16"),
            fragment.getPosition()
        );
        fragment = new DefaultFragment(
            first.getFragment().getBegin(),
            last.getFragment().getEnd()
        );
        Assertions.assertEquals("{\n    printf(\"x\");\n}", fragment.getCode());
        Assertions.assertEquals(
            ParserTest.TEST_FILE.concat(", 3.13-5.1"),
            fragment.getPosition()
        );
        fragment = new DefaultFragment(
            last.getFragment().getEnd(),
            first.getFragment().getBegin()
        );
        Assertions.assertEquals("{\n    printf(\"x\");\n}", fragment.getCode());
    }

    @Test
    void parsingFromString() {
        final StringSource source = new StringSource("abc");
        Assertions.assertTrue(source.getFileName().isEmpty());
        final Tree tree = source.parseIntoTree();
        Assertions.assertEquals(3, tree.getRoot().getChildCount());
        Assertions.assertEquals(
            "1.1-1.1",
            tree.getRoot().getChild(0).getFragment().getPosition()
        );
    }
}
