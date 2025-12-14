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