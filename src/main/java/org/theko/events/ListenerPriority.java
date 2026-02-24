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
 * Represents a priority for event listeners.
 * <p>
 * The priority determines the order in which listeners are executed: 
 * lower values are executed before higher values.
 * <p>
 * Instances are immutable and comparable. You can compare priorities
 * using {@link Comparable#compareTo(Object)}, for example to sort listeners 
 * in execution order.
 * <p>
 * Predefined standard priorities are provided as public constants:
 * {@link #CRITICAL}, {@link #HIGH}, {@link #NORMAL}, {@link #LOW}, {@link #LOWEST}.
 * Custom priorities can also be created with any non-negative integer value.
 * 
 * @author Theko
 * @since 1.0
 */
public class ListenerPriority implements Comparable<ListenerPriority> {

    /** Critical priority. Has priority value of 0 */
    public static final ListenerPriority CRITICAL = new ListenerPriority(0);

    /** High priority. Has priority value of 25 */
    public static final ListenerPriority HIGH = new ListenerPriority(25);

    /** Normal priority. Has priority value of 50 */
    public static final ListenerPriority NORMAL = new ListenerPriority(50);

    /** Low priority. Has priority value of 75 */
    public static final ListenerPriority LOW = new ListenerPriority(75);

    /** Lowest priority. Has priority value of 100 */
    public static final ListenerPriority LOWEST = new ListenerPriority(100);

    private final int priority;
    
    /**
     * Creates a new listener priority with the given priority.
     * <p>
     * Priorities with lower priorities are executed first.
     * @param priority the priority value, must be non-negative
     */
    public ListenerPriority(int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException("Priority must be non-negative.");
        }
        this.priority = priority;
    }

    /**
     * Returns the priority of this listener as an integer priority.
     * <p>
     * Lower priorities indicate higher priority (i.e. listeners with lower priorities are executed first).
     * @return the priority of this listener
     */
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(ListenerPriority o) {
        return Integer.compare(priority, o.priority);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ListenerPriority that = (ListenerPriority) obj;
        return priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(priority);
    }

    /**
     * Returns a string representation of this listener priority.
     * <p>
     * Returns one of the following strings based on the priority value:
     * <ul>
     * <li>"CRITICAL" for priority 0</li>
     * <li>"HIGH" for priority 25</li>
     * <li>"NORMAL" for priority 50</li>
     * <li>"LOW" for priority 75</li>
     * <li>"LOWEST" for priority 100</li>
     * <li>"CUSTOM(" + priority + ")" for custom priorities</li>
     * </ul>
     * @return a string representation of this listener priority
     */
    @Override
    public String toString() {
        switch(priority) {
            case 0: return "CRITICAL";
            case 25: return "HIGH";
            case 50: return "NORMAL";
            case 75: return "LOW";
            case 100: return "LOWEST";
            default: return "CUSTOM(" + priority + ")";
        }
    }
}