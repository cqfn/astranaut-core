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

import java.util.Collections;
import java.util.Map;

/**
 * The {@code NodeAndType} abstract class is a combined implementation of the {@link Node}
 *  and {@link Type} interfaces.<br/>
 *  In many scenarios, it turns out that the type of a node is unique to each node.
 *  This is often encountered when a node is a wrapper for some data that needs to be represented
 *  using the {@link Node} interface in order to be processed by standard algorithms. Typically,
 *  this situation requires the creation of two objects: a node and its corresponding type.
 *  To simplify this, we can use a special class such as {@code NodeAndType} as a template,
 *  which represents both the node and the type simultaneously. This approach allows for the
 *  "saving" of one object instantiation when creating such nodes.
 * @since 2.0.0
 */
public abstract class NodeAndType implements Node, Type {
    @Override
    public final Type getType() {
        return this;
    }

    /**
     * Returns an empty map by default. Subclasses may override this method to provide
     *  specific properties for the node. When overriding this method, ensure that the returned
     *  map is immutable or that modifications do not affect the internal state of the node.
     * @return An immutable map of properties
     */
    public Map<String, String> getProperties() {
        this.getClass();
        return Collections.emptyMap();
    }

    @Override
    public final boolean belongsToGroup(final String type) {
        return this.getHierarchy().contains(type);
    }
}
