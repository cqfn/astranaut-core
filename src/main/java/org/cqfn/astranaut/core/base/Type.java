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
import java.util.List;
import java.util.Map;

/**
 * A type of abstract syntax tree node.
 * @since 1.0
 */
public interface Type {
    /**
     * Retrieves the name of the node type.
     * @return The type name
     */
    String getName();

    /**
     * Returns the list of child types that the node type can have.
     *  Child types are represented by descriptors that specify the allowed types
     *  and their constraints. This information is used during node creation to validate
     *  the correctness of the created node.
     * @return Unmodifiable list of child descriptors
     */
    default List<ChildDescriptor> getChildTypes() {
        return Collections.emptyList();
    }

    /**
     * Returns the hierarchy of type names that the node type belongs to.
     *  The hierarchy includes the name of the type itself followed by the names of its
     *  parent types. Parent types are listed from the immediate parent to the furthest ancestor.
     * @return Unmodifiable list of type names in the hierarchy
     */
    default List<String> getHierarchy() {
        return Collections.singletonList(this.getName());
    }

    /**
     * Checks whether the type belongs to group.
     * @param type The type name
     * @return Checking result, {@code true} if the type belongs to the group
     */
    default boolean belongsToGroup(final String type) {
        return this.getHierarchy().contains(type);
    }

    /**
     * Returns an immutable set of properties.
     *  The presence of specific properties depends on the specific implementation
     *  and may vary. By default, there are no custom properties.
     * @return Immutable map of properties where keys are property names
     *  and values are property values
     */
    default Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    /**
     * Creates a new builder who builds a node of this type.
     * @return A builder.
     */
    Builder createBuilder();
}
