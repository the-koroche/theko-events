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
 * Abstract base class for all events.
 * <p>
 * Events are immutable data carriers with a creation timestamp.
 * They support consumption via `consume()` to prevent further propagation.
 * 
 * @see EventDispatcher
 * @see EventConsumer
 * @see Listener
 * 
 * @author Theko
 * @since 1.0
 */
public abstract class Event {

    private final long timestamp;
    private volatile boolean isConsumed;

    /**
     *  Default constructor. 
     */
    protected Event() {
        timestamp = System.currentTimeMillis();
    }

    /**
     * Retrieves the creation timestamp of this event in milliseconds.
     * <p>
     * This is the time when this event was created, not when it was dispatched.
     * 
     * @return the creation timestamp of this event in milliseconds.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Marks this event as consumed.
     * <p>
     * If this event has not been consumed yet, this method sets the consumed flag to true
     * and returns true. Otherwise, it returns false.
     * <p>
     * Consuming an event prevents it from being further propagated to other listeners and consumers.
     * 
     * @return true if the event was successfully marked as consumed, false otherwise.
     */
    public boolean consume() {
        if (!isConsumed) {
            isConsumed = true;
            return true;
        }
        return false;
    }
    
    /**
     * Indicates whether this event has been consumed or not.
     * <p>
     * A consumed event will not be propagated to other listeners.
     * 
     * @return true if this event has been consumed, false otherwise.
     */
    public boolean isConsumed() {
        return isConsumed;
    }

    /**
     * Returns a string representation of this event.
     * <p>
     * The returned string will be in the format "Event{timestamp=<i>timestamp</i>, isConsumed=<i>isConsumed</i>}"
     * where <code>timestamp</code> is the creation timestamp of this event and
     * <code>isConsumed</code> indicates whether this event has been consumed or not.
     * 
     * @return a string representation of this event.
     */
    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                ", isConsumed=" + isConsumed +
                '}';
    }
}
