/*
 * MIT License
 *
 * Copyright (c) 2025-present Alex Soloviov (aka Theko)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.theko.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a dynamic, weakly-typed event container that stores data as key-value pairs.
 * <p>
 * This class is designed to be used with {@link DynamicEventDispatcher} when creating
 * explicit subclasses for every single event type is impractical or unnecessary.
 * <p>
 * It provides a fluent API via the {@link #with(String, Object)} method for convenient
 * inline initialization.
 *
 * <p>
 * Example:
 * <pre>{@code
 * DynamicEvent event = new DynamicEvent()
 * .with("userId", "12345")
 * .with("action", "login");
 * }</pre>
 *
 * @see DynamicEventDispatcher
 * @see Event
 */
public class DynamicEvent extends Event {

    private final Map<String, Object> data = new HashMap<>();

    /**
     * Adds a key-value pair to the event data and returns this instance for method chaining.
     *
     * @param key   the data key
     * @param value the data value
     * @return this {@code DynamicEvent} instance
     */
    public DynamicEvent with(String key, Object value) {
        this.put(key, value);
        return this;
    }

    /**
     * Associates the specified value with the specified key in this event.
     *
     * @param key   the data key
     * @param value the data value
     */
    public void put(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this event contains no mapping for the key.
     *
     * @param key the data key
     * @return the value associated with the key, or {@code null}
     */
    public Object get(String key) {
        return data.get(key);
    }
}