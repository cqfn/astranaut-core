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
package org.cqfn.astranaut.core;

import java.util.Objects;

/**
 * This class contains information about the node being inserted as a child of another node.
 *
 * @since 1.1.0
 */
public final class Insertion {
    /**
     * Node being inserted.
     */
    private final Node inserted;

    /**
     * Parent node into which the child node will be inserted.
     */
    private final Node into;

    /**
     * Child node after which to insert.
     */
    private final Node after;

    /**
     * Constructor.
     * @param inserted Node being inserted
     * @param into Parent node into which the child node will be inserted
     * @param after Child node after which to insert
     */
    public Insertion(final Node inserted, final Node into, final Node after) {
        this.inserted = Objects.requireNonNull(inserted);
        this.into = into;
        this.after = after;
    }

    /**
     * Another constructor.
     * @param inserted Node being inserted
     * @param after Child node after which to insert
     */
    public Insertion(final Node inserted, final Node after) {
        this(inserted, null, Objects.requireNonNull(after));
    }

    /**
     * Returns node being inserted.
     * @return A node
     */
    public Node getNode() {
        return this.inserted;
    }

    /**
     * Returns parent node into which the child node will be inserted.
     * @return A node
     */
    public Node getInto() {
        return this.into;
    }

    /**
     * Returns child node after which to insert.
     * @return A node
     */
    public Node getAfter() {
        return this.after;
    }
}
