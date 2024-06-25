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

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This test suite tests the {@link Tree} class.
 *
 * @since 2.0.0
 */
class TreeTest {
    @Test
    void testBaseInterface() {
        final String language = "quenya";
        final String description = "A(B, C)";
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("A");
        ctor.addChild(DraftNode.create("B"));
        ctor.addChild(new TestNode("C", language));
        final Tree tree = new Tree(ctor.createNode());
        Assertions.assertEquals(description, tree.getRoot().toString());
        Assertions.assertEquals(description, tree.toString());
        Assertions.assertEquals(language, tree.getLanguage());
    }

    /**
     * Node implementation for testing purposes.
     *
     * @since 2.0.0
     */
    private static final class TestNode implements Node {
        /**
         * The type of the node.
         */
        private final Type type;

        /**
         * Constructor.
         * @param name The name of the type
         * @param language The name of the programming language
         */
        private TestNode(final String name, final String language) {
            this.type = new TestType(name, language);
        }

        @Override
        public Fragment getFragment() {
            return EmptyFragment.INSTANCE;
        }

        @Override
        public Type getType() {
            return this.type;
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
        public String toString() {
            return this.type.getName();
        }
    }

    /**
     * Type implementation for testing purposes.
     *
     * @since 2.0.0
     */
    private static final class TestType implements Type {
        /**
         * The name of the type.
         */
        private final String name;

        /**
         * The name of the programming language from which the source code, used to construct
         *  this tree, was written.
         */
        private final String language;

        /**
         * Constructor.
         * @param name The name of the type
         * @param language The name of the programming language
         */
        private TestType(final String name, final String language) {
            this.name = name;
            this.language = language;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getHierarchy() {
            return Collections.singletonList(this.getName());
        }

        @Override
        public String getProperty(final String property) {
            final String result;
            if (property.equals("language")) {
                result = this.language;
            } else {
                result = "";
            }
            return result;
        }

        @Override
        public Builder createBuilder() {
            return null;
        }
    }
}
