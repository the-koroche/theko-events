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

import java.util.List;

/**
 * An interface for classes that expose a {@link ListenersManager} instance.
 * Provides a way to manage listeners and event consumers for a class.
 * <p>
 * Example:
 * <pre>{@code
 * class MyClass implements ListenersManageable<MyEvent, MyListener, String> {
 *     private final EventDispatcher<MyEvent, MyListener, String> dispatcher = new EventDispatcher();
 * 
 *     @Override
 *     public ListenersManager<MyEvent, MyListener, String> getListenersManager() {
 *         return new ListenersManager<>(dispatcher);
 *     }
 * }
 * 
 * MyClass myClass = new MyClass();
 * // Now MyClass can automatically manage listeners:
 * myClass.addListener(listener);
 * }</pre>
 *
 * @param <E> the type of event being handled, must extend {@link Event}
 * @param <L> the type of listener being managed, must extend {@link Listener}
 * @param <T> the classification type used for event routing
 * 
 * @see ListenersManager
 * @see Listener
 * @see EventDispatcher
 *
 * @author Theko
 * @since 2.0.0
 */
public interface ListenersManageable<E extends Event, L extends Listener<E>, T> {

    /**
     * Returns the {@link ListenersManager} instance.
     *
     * @return the listeners manager instance
     */
    ListenersManager<E, L, T> getListenersManager();

    /**
     * Registers a listener with the specified priority level.
     * <p>
     * Listeners with higher priority will receive events before listeners with lower priority.
     * Multiple listeners with the same priority are processed in registration order.
     * 
     * @param priority the priority level for the listener
     * @param listener the listener to register, must not be {@code null}
     * @throws NullPointerException if the listener or priority is {@code null}
     */
    default void addListener(ListenerPriority priority, L listener) {
        getListenersManager().addListener(priority, listener);
    }

    /**
     * Registers a listener with {@link ListenerPriority#NORMAL} priority.
     *
     * @param listener the listener to register, must not be {@code null}
     * @throws NullPointerException if the listener is {@code null}
     */
    default void addListener(L listener) {
        getListenersManager().addListener(listener);
    }

    /**
     * Checks if a listener is currently registered.
     *
     * @param listener the listener to check
     * @return {@code true} if the listener is registered, {@code false} otherwise
     */
    default boolean hasListener(L listener) {
        return getListenersManager().hasListener(listener);
    }

    /**
     * Removes a listener from all priority levels.
     *
     * @param listener the listener to remove
     * @return {@code true} if the listener was found and removed, {@code false} otherwise
     */
    default boolean removeListener(L listener) {
        return getListenersManager().removeListener(listener);
    }

    /**
     * Returns a sorted list of all registered listeners.
     * <p>
     * The list is sorted by the listener priority level ({@link ListenerPriority}).
     * 
     * @return a sorted list of all registered listeners
     */
    default List<L> getListeners() {
        return getListenersManager().getListeners();
    }

    /**
     * Registers an event consumer with the specified priority level and event type.
     * <p>
     * Event consumers provide a functional alternative to full listener implementations
     * for simpler event handling scenarios.
     * 
     * @param priority the priority level for the consumer
     * @param eventType the specific event type this consumer handles, must not be {@code null}
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either priority, eventTypem, or consumer is {@code null}
     */
    default void addConsumer(ListenerPriority priority, T eventType, EventConsumer<E, T> consumer) {
        getListenersManager().addConsumer(priority, eventType, consumer);
    }

    /**
     * Registers an event consumer with the specified priority level.
     * <p>
     * The event type is not specified, so the consumer will receive all events.
     * 
     * @param priority the priority level for the consumer
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either priority or consumer is {@code null}
     */
    default void addConsumer(ListenerPriority priority, EventConsumer<E, T> consumer) {
        getListenersManager().addConsumer(priority, consumer);
    }

    /**
     * Registers an event consumer with {@link ListenerPriority#NORMAL} priority.
     *
     * @param eventType the specific event type this consumer handles, must not be {@code null}
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either eventType or consumer is {@code null}
     */
    default void addConsumer(T eventType, EventConsumer<E, T> consumer) {
        getListenersManager().addConsumer(eventType, consumer);
    }

    /**
     * Registers an event consumer with {@link ListenerPriority#NORMAL} priority.
     * <p>
     * The event type is not specified, so the consumer will receive all events.
     *
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if consumer is {@code null}
     */
    default void addConsumer(EventConsumer<E, T> consumer) {
        getListenersManager().addConsumer(consumer);
    }

    /**
     * Checks if an event consumer is currently registered.
     *
     * @param consumer the event consumer to check
     * @return {@code true} if the consumer is registered, {@code false} otherwise
     */
    default boolean hasConsumer(EventConsumer<E, T> consumer) {
        return getListenersManager().hasConsumer(consumer);
    }

    /**
     * Removes an event consumer from all priority levels.
     *
     * @param consumer the event consumer to remove
     * @return {@code true} if the consumer was found and removed, {@code false} otherwise
     */
    default boolean removeConsumer(EventConsumer<E, T> consumer) {
        return getListenersManager().removeConsumer(consumer);
    }

    /**
     * Returns a sorted list of all registered event consumers.
     * <p>
     * The list is sorted by the consumer priority level ({@link ListenerPriority}).
     * 
     * @return a sorted list of all registered event consumers
     */
    default List<EventConsumer<E, T>> getConsumers() {
        return getListenersManager().getConsumers();
    }

    /**
     * Returns a sorted list of all registered event consumers that match the given event type.
     * <p>
     * The list is sorted by the consumer priority level ({@link ListenerPriority}).
     * If the event type is {@code null}, all event consumers without an event type are returned.
     * 
     * @param eventType the event type to filter by
     * @return a sorted list of all registered event consumers that match the given event type
     */
    default List<EventConsumer<E, T>> getConsumers(T eventType) {
        return getListenersManager().getConsumers(eventType);
    }
}
