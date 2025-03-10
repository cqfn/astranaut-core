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
package org.cqfn.astranaut.core.algorithms.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.Node;

/**
 * Stores extracted nodes and associated data mapped to numbers.
 *  Extraction occurs during pattern matching with a subtree. A number may correspond to
 *  either a list of nodes or a string value.
 * @since 2.0.0
 */
public final class Extracted {
    /**
     * A map storing the mapping of numbers to their associated nodes.
     * The key is the number, and the value is a list of nodes related to that number.
     */
    private Map<Integer, List<Node>> children;

    /**
     * A map storing the mapping of numbers to their string data.
     * The key is the number, and the value is the associated string data.
     */
    private Map<Integer, String> data;

    /**
     * Adds a node to the specified number.
     * @param number The number to which the node is added.
     * @param node The node to add.
     */
    public void addNode(final int number, final Node node) {
        final List<Node> list;
        do {
            if (this.children == null) {
                this.children = new TreeMap<>();
                list = new ArrayList<>(1);
                this.children.put(number, list);
                break;
            }
            list = this.children.computeIfAbsent(number, k -> new ArrayList<>(1));
        } while (false);
        list.add(node);
    }

    /**
     * Adds string data to the specified number.
     * @param number The number to which the data is added.
     * @param value The string data.
     */
    public void addData(final int number, final String value) {
        if (this.data == null) {
            this.data = new TreeMap<>();
        }
        this.data.put(number, value);
    }

    /**
     * Returns the list of nodes associated with the specified number.
     * The returned list is immutable. If no nodes are associated with the number,
     *  an empty list is returned.
     * @param number The number for which nodes are requested.
     * @return An unmodifiable list of nodes.
     */
    public List<Node> getNodes(final int number) {
        final List<Node> list;
        if (this.children == null || !this.children.containsKey(number)) {
            list = Collections.emptyList();
        } else {
            list = Collections.unmodifiableList(this.children.get(number));
        }
        return list;
    }

    /**
     * Returns the string data associated with the specified number.
     *  If no data is associated with the number, an empty string is returned.
     * @param number The number for which data is requested.
     * @return The associated string data (or an empty string if no data exists).
     */
    public String getData(final int number) {
        final String result;
        if (this.data == null) {
            result = "";
        } else {
            result = this.data.getOrDefault(number, "");
        }
        return result;
    }
}
