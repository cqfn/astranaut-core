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
package org.cqfn.astranaut.core.algorithms.mapping;

import java.util.Objects;
import org.cqfn.astranaut.core.base.ExtNode;
import org.cqfn.astranaut.core.base.Insertion;
import org.cqfn.astranaut.core.base.Node;

/**
 * Insertion descriptor like {@link Insertion}, but working with extended nodes.
 * @since 2.0.0
 */
final class ExtInsertion {
    /**
     * Node being inserted.
     */
    private final ExtNode inserted;

    /**
     * Parent node into which the child node will be inserted.
     */
    private final ExtNode into;

    /**
     * Child node after which to insert.
     */
    private final ExtNode after;

    /**
     * Constructor.
     * @param inserted Node being inserted
     * @param into Parent node into which the child node will be inserted
     * @param after Child node after which to insert
     */
    ExtInsertion(final ExtNode inserted, final ExtNode into, final ExtNode after) {
        this.inserted = Objects.requireNonNull(inserted);
        this.into = into;
        this.after = after;
    }

    /**
     * Converts the descriptor to a 'classic' {@link Insertion}.
     * @return An insertion descriptor that uses non-extended nodes
     */
    public Insertion toInsertion() {
        final Node third;
        if (this.after == null) {
            third = null;
        } else {
            third = this.after.getPrototype();
        }
        return new Insertion(this.inserted.getPrototype(), this.into.getPrototype(), third);
    }
}
