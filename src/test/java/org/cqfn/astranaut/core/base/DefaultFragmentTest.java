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
package org.cqfn.astranaut.core.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link DefaultFragment} class.
 * @since 2.0.0
 */
class DefaultFragmentTest {
    @Test
    void testBaseInterface() {
        final Source source = (start, end) -> "";
        final Position begin = new DefaultPosition(source, 1, 1);
        final Position end = new DefaultPosition(source, 1, 2);
        final Fragment fragment = new DefaultFragment(begin, end);
        Assertions.assertSame(begin, fragment.getBegin());
        Assertions.assertSame(end, fragment.getEnd());
    }

    @Test
    void testAlternativeConstructors() {
        final Source source = (start, end) -> "";
        final Position[] positions = {
            new DefaultPosition(source, 4, 7),
            new DefaultPosition(source, 1, 2),
            new DefaultPosition(source, 3, 14),
            new DefaultPosition(source, 2, 1),
        };
        Fragment fragment = new DefaultFragment(positions);
        Assertions.assertEquals(1, fragment.getBegin().getRow());
        Assertions.assertEquals(4, fragment.getEnd().getRow());
        fragment = new DefaultFragment(new DefaultPosition(source, 13, 13));
        Assertions.assertEquals(13, fragment.getBegin().getRow());
        Assertions.assertEquals(13, fragment.getEnd().getRow());
    }
}
