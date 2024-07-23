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
package org.cqfn.astranaut.core.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link PatternNode} class.
 * @since 1.1.5
 */
class PatternNodeTest {
    @Test
    void testBaseInterface() {
        final Source source = new TestSource();
        final Position begin = new DefaultPosition(source, 1, 1);
        final Position end = new DefaultPosition(source, 1, 100);
        final Fragment fragment = new DefaultFragment(begin, end);
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setFragment(fragment);
        ctor.setName("X");
        ctor.setData("test");
        final Node node = ctor.createNode();
        final PatternNode pattern = new PatternNode(new DiffNode(node));
        Assertions.assertEquals(100, pattern.getFragment().getEnd().getColumn());
        Assertions.assertEquals("X<\"test\">", pattern.toString());
    }

    /**
     * Source implementation for test purposes.
     * @since 1.1.5
     */
    private static final class TestSource implements Source {
        @Override
        public String getFragmentAsString(final Position start, final Position end) {
            return "";
        }
    }
}
