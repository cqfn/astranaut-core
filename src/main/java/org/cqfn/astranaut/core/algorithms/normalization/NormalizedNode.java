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
package org.cqfn.astranaut.core.algorithms.normalization;

import java.util.Collections;
import java.util.Map;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.PrototypeBasedNode;
import org.cqfn.astranaut.core.base.Type;

/**
 * Represents a normalized node.
 *  A normalized node is a node without properties. All properties of the original node
 *  are extracted into separate child nodes.
 * @since 2.0.0
 */
public final class NormalizedNode implements PrototypeBasedNode {
    /**
     * The original, non-normalized node.
     */
    private final Node original;

    /**
     * Constructor.
     * @param original The original, non-normalized node.
     */
    public NormalizedNode(final Node original) {
        this.original = original;
    }

    @Override
    public Fragment getFragment() {
        return this.original.getFragment();
    }

    @Override
    public Type getType() {
        return this.original.getType();
    }

    @Override
    public String getData() {
        return this.original.getData();
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
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
    public Node getPrototype() {
        return this.original;
    }
}
