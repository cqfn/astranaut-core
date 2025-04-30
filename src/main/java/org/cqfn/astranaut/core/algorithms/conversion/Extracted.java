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
            this.data.put(number, value);
        } else {
            this.data.merge(number, value, String::concat);
        }
    }

    /**
     * Returns a combined list of nodes associated with the specified numbers.
     * <p>
     * The returned list is immutable. If no nodes are associated with the given numbers,
     * an empty list is returned.
     *
     * @param numbers The numbers for which nodes are requested.
     * @return An unmodifiable list of nodes combined from all specified numbers.
     */
    public List<Node> getNodes(final int... numbers) {
        final List<Node> result;
        if (this.children == null || numbers.length == 0) {
            result = Collections.emptyList();
        } else {
            final List<Node> list = new ArrayList<>(numbers.length);
            for (final int number : numbers) {
                final List<Node> nodes = this.children.get(number);
                if (nodes != null) {
                    list.addAll(nodes);
                }
            }
            result = Collections.unmodifiableList(list);
        }
        return result;
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
