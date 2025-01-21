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
package org.cqfn.astranaut.core.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for building maps.
 * @param <K> Key type
 * @param <V> Value type
 * @since 2.0.0
 */
public class MapUtils<K, V> {
    /**
     * Map being built.
     */
    private final Map<K, V> map;

    /**
     * Constructor.
     */
    public MapUtils() {
        this.map = new HashMap<>();
    }

    /**
     * Adds all mappings from the provided map into resulting map.
     * @param other Another map whose mappings will be added
     * @return This builder instance
     */
    public MapUtils<K, V> put(final Map<? extends K, ? extends V> other) {
        this.map.putAll(other);
        return this;
    }

    /**
     * Adds a mapping to resulting map.
     * @param key The key of the mapping
     * @param value The value of the mapping
     * @return This builder instance
     */
    public MapUtils<K, V> put(final K key, final V value) {
        this.map.put(key, value);
        return this;
    }

    /**
     * Constructs an unmodifiable map from the mappings added to this builder.
     * @return An unmodifiable map containing all mappings added to this builder
     */
    public Map<K, V> make() {
        return Collections.unmodifiableMap(this.map);
    }
}
