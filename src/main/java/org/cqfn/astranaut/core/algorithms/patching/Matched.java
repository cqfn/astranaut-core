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
package org.cqfn.astranaut.core.algorithms.patching;

import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.ActionList;

/**
 * Data obtained through the matching process.
 * @since 2.0.0
 */
final class Matched extends ActionList {
    /**
     * Data obtained as a result of hole matching.
     */
    private Map<Integer, String> holes;

    /**
     * Partially clones this object. The new instance will contain only information
     * about previously extracted holes, without extracted nodes or data.
     * @return A new Matched object with copied hole data
     */
    Matched fork() {
        final Matched obj = new Matched();
        if (this.holes != null) {
            obj.holes = new TreeMap<>(this.holes);
        }
        return obj;
    }

    /**
     * Combines data obtained through the matching process.
     * @param other Other data
     */
    void merge(final Matched other) {
        super.merge(other);
        if (this.holes == null) {
            this.holes = other.holes;
        } else {
            this.holes.putAll(other.holes);
        }
    }

    /**
     * Checks data matched for a hole. If a hole with the given number is seen for the first time,
     * stores the data. If it's already known, verifies that the data matches.
     * @param number Hole number
     * @param data Matched data
     * @return Checking result, {@code true} if data is accepted or matches previous,
     *  {@code false} otherwise.
     */
    boolean checkHole(final int number, final String data) {
        final boolean result;
        if (this.holes == null) {
            this.holes = new TreeMap<>();
            this.holes.put(number, data);
            result = true;
        } else if (this.holes.containsKey(number)) {
            result = this.holes.get(number).equals(data);
        } else {
            this.holes.put(number, data);
            result = true;
        }
        return result;
    }
}
