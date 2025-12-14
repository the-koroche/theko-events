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
 * Base interface for receiving events.
 * <p>
 * {@code Listener} defines a single callback method that is invoked
 * when an event is dispatched to the listener.
 * <p>
 * Implementations may define additional handler methods that can be
 * bound to event types using {@link EventMap}.
 *
 * @param <E> event type
 * @param <T> event classification key type
 *
 * @author Theko
 * @since 1.0
 * @see Event
 * @see EventConsumer
 */
public interface Listener<E extends Event, T> {

    /**
     * Called when an event is dispatched to this listener.
     *
     * @param type event classification key (may be {@code null})
     * @param event event instance
     */
    default void onEvent(T type, E event) { }
}