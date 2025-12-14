package org.theko.events;

/**
 * Consumes events of a specific type.
 * <p>
 * Implementations process dispatched events and may call {@link Event#consume()} 
 * to stop further handling.
 *
 * @param <E> event type
 *
 * @author Theko
 * @since 1.0
 * @see Event
 * @see EventDispatcher
 */
@FunctionalInterface
public interface EventConsumer<E extends Event> {

    /** Processes the given event. */
    void consume(E event);
}