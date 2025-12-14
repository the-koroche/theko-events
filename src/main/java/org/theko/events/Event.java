/*
 * MIT License
 * 
 * Copyright (c) 2025 Alex Soloviov (aka Theko)
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

/**
 * Base class for all events in the event-driven system.
 * <p>
 * Events are immutable data carriers that may be consumed to stop further handling.
 * 
 * @author Theko
 * @since 1.0
 */
public abstract class Event {

    /** Creation timestamp in milliseconds since UNIX epoch. */
    protected final long timestamp = System.currentTimeMillis();

    /** Flag indicating whether this event has been consumed. */
    private boolean isConsumed;

    protected Event() { }

    /** Returns the creation timestamp of this event. */
    public long getTimestamp() {
        return timestamp;
    }

    /** Marks this event as consumed to prevent further handling. */
    public void consume() {
        isConsumed = true;
    }

    /** Checks whether this event has been consumed. */
    public boolean isConsumed() {
        return isConsumed;
    }
}
