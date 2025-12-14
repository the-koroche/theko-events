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
 * Handles exceptions thrown during event processing.
 * Supports handler chaining via {@link #andThen(EventExceptionHandler)}.
 *
 * @param <L> listener type
 * @param <E> event type
 * @param <T> exception type
 *
 * @author Theko
 * @since 1.0
 * @see EventHandler
 * @see Event
 * @see Listener
 */
@FunctionalInterface
public interface EventExceptionHandler<L, E extends Event, T extends Throwable> {
    
    /**
     * Handles an exception thrown while processing an event.
     *
     * @param listener listener that was processing the event (may be {@code null})
     * @param event event being processed
     * @param exception thrown exception
     */
    void handle(L listener, E event, T exception);

    /**
     * Returns a composed handler that executes this handler first,
     * then the given handler.
     *
     * @param next next handler
     * @return composed exception handler
     */
    default EventExceptionHandler<L, E, T> andThen(EventExceptionHandler<L, E, T> next) {
        if (next == null) {
            throw new NullPointerException("Next handler must not be null");
        }
        return (listener, event, exception) -> {
            this.handle(listener, event, exception);
            next.handle(listener, event, exception);
        };
    }
}