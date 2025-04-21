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

import java.util.Arrays;
import java.util.Collections;
import org.cqfn.astranaut.core.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link Position} class.
 * @since 2.0.0
 */
class PositionTest {
    @Test
    void testBaseInterface() {
        final Source source = (start, end) -> "";
        final Source another = (start, end) -> "";
        final Position first = new DefaultPosition(source, 1, 1);
        final Position second = new DefaultPosition(source, 1, 2);
        Assertions.assertTrue(second.compareTo(first) > 0);
        final Position third = new DefaultPosition(source, 2, 1);
        Assertions.assertTrue(third.compareTo(first) > 0);
        final Position alien = new DefaultPosition(another, 1, 1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> first.compareTo(alien));
    }

    @Test
    void testBounds() {
        Assertions.assertThrows(IllegalArgumentException.class, Position::bounds);
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> Position.bounds(Collections.emptyList())
        );
        final Source source = (start, end) -> "";
        final Position[] list = {
            new DefaultPosition(source, 2, 2),
            new DefaultPosition(source, 1, 1),
            new DefaultPosition(source, 4, 4),
            new DefaultPosition(source, 3, 3),
        };
        Pair<Position, Position> pair = Position.bounds(list);
        Assertions.assertEquals(1, pair.getKey().getRow());
        Assertions.assertEquals(4, pair.getValue().getRow());
        pair = Position.bounds(Arrays.asList(list));
        Assertions.assertEquals(1, pair.getKey().getRow());
        Assertions.assertEquals(4, pair.getValue().getRow());
    }
}
