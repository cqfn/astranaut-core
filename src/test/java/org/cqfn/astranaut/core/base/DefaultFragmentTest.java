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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link DefaultFragment} class.
 * @since 2.0.0
 */
class DefaultFragmentTest {
    /**
     * Fake source for testing purposes.
     */
    private static final Source SOURCE = (start, end) -> "";

    @Test
    void testBaseInterface() {
        final Position begin = new DefaultPosition(DefaultFragmentTest.SOURCE, 1, 1);
        final Position end = new DefaultPosition(DefaultFragmentTest.SOURCE, 1, 2);
        final Fragment fragment = new DefaultFragment(begin, end);
        Assertions.assertSame(begin, fragment.getBegin());
        Assertions.assertSame(end, fragment.getEnd());
    }

    @Test
    void testAlternativeConstructors() {
        final Position[] positions = {
            new DefaultPosition(DefaultFragmentTest.SOURCE, 4, 7),
            new DefaultPosition(DefaultFragmentTest.SOURCE, 1, 2),
            new DefaultPosition(DefaultFragmentTest.SOURCE, 3, 14),
            new DefaultPosition(DefaultFragmentTest.SOURCE, 2, 1),
        };
        Fragment fragment = Fragment.fromPositions(positions);
        Assertions.assertEquals(1, fragment.getBegin().getRow());
        Assertions.assertEquals(4, fragment.getEnd().getRow());
        fragment = Fragment.fromPositions(
            new DefaultPosition(DefaultFragmentTest.SOURCE, 13, 13)
        );
        Assertions.assertEquals(13, fragment.getBegin().getRow());
        Assertions.assertEquals(13, fragment.getEnd().getRow());
    }

    @Test
    void fromArrayOfPositions() {
        final Fragment first = Fragment.fromPositions();
        Assertions.assertEquals(EmptyFragment.INSTANCE, first);
        final Fragment second = Fragment.fromPositions(
            new DefaultPosition(DefaultFragmentTest.SOURCE, 1, 1)
        );
        Assertions.assertEquals(1, second.getBegin().getRow());
        Assertions.assertEquals(1, second.getEnd().getRow());
        final Fragment third = Fragment.fromPositions(
            new DefaultPosition(DefaultFragmentTest.SOURCE, 2, 1),
            new DefaultPosition(DefaultFragmentTest.SOURCE, 1, 1)
        );
        Assertions.assertEquals(1, third.getBegin().getRow());
        Assertions.assertEquals(2, third.getEnd().getRow());
    }

    @Test
    void fromListOfPositions() {
        final Fragment first = Fragment.fromPositions(Collections.emptyList());
        Assertions.assertEquals(EmptyFragment.INSTANCE, first);
        final Fragment second = Fragment.fromPositions(
            Collections.singletonList(
                new DefaultPosition(DefaultFragmentTest.SOURCE, 1, 1)
            )
        );
        Assertions.assertEquals(1, second.getBegin().getRow());
        Assertions.assertEquals(1, second.getEnd().getRow());
        final Fragment third = Fragment.fromPositions(
            Arrays.asList(
                new DefaultPosition(DefaultFragmentTest.SOURCE, 2, 1),
                new DefaultPosition(DefaultFragmentTest.SOURCE, 1, 1)
            )
        );
        Assertions.assertEquals(1, third.getBegin().getRow());
        Assertions.assertEquals(2, third.getEnd().getRow());
    }

    @Test
    void fromListOfNodes() {
        final Fragment first = Fragment.fromNodes(Collections.emptyList());
        Assertions.assertEquals(EmptyFragment.INSTANCE, first);
        final Fragment second = Fragment.fromNodes(
            Collections.singletonList(new FakeNode(1))
        );
        Assertions.assertEquals(1, second.getBegin().getRow());
        Assertions.assertEquals(1, second.getEnd().getRow());
        final Fragment third = Fragment.fromNodes(
            Arrays.asList(
                new FakeNode(3),
                new FakeNode(0),
                new FakeNode(2),
                new FakeNode(1)
            )
        );
        Assertions.assertEquals(1, third.getBegin().getRow());
        Assertions.assertEquals(3, third.getEnd().getRow());
    }

    /**
     * A fake node for test purposes, it only returns a fragment.
     * @since 2.0.0
     */
    private static final class FakeNode extends NodeAndType {
        /**
         * Row number.
         */
        private final int row;

        /**
         * Constructor.
         * @param row Row number
         */
        private FakeNode(final int row) {
            this.row = row;
        }

        @Override
        public Fragment getFragment() {
            Fragment fragment = EmptyFragment.INSTANCE;
            if (this.row != 0) {
                fragment = Fragment.fromPositions(
                    new DefaultPosition(DefaultFragmentTest.SOURCE, this.row, 1)
                );
            }
            return fragment;
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
            return null;
        }

        @Override
        public String getName() {
            return "X";
        }

        @Override
        public Builder createBuilder() {
            return null;
        }
    }
}
