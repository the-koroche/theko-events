package org.theko.events;

/**
 * A generic listener interface for handling events with associated types.
 * <p>
 * Listeners provide a mechanism to respond to events based on both the event type
 * and an additional classification type. This allows for more granular event handling
 * where the same event class can be processed differently depending on context.
 * </p>
 *
 * <p>
 * This interface provides a default no-op implementation of {@link #onEvent(Object, Event)}
 * to allow subclasses to override only the event types they are interested in handling.
 * </p>
 *
 * <p>
 * <strong>Typical Usage Patterns:</strong>
 * <ul>
 *   <li>Handling different categories of the same event type</li>
 *   <li>Routing events based on application-specific context</li>
 *   <li>Creating hierarchical or categorized event processing systems</li>
 *   <li>Implementing state-dependent event handling</li>
 * </ul>
 * </p>
 *
 * @param E the type of event this listener can handle, must extend {@link Event}
 * @param T the classification type used to categorize or route events
 *
 * @author Theko
 * @since 1.0
 * @see Event
 * @see EventConsumer
 */
public interface Listener<E extends Event, T> {

    /**
     * Called when an event of the specified type occurs.
     * <p>
     * This method is invoked by the event system when an event matching both
     * the event type {@code E} and classification type {@code T} is dispatched.
     * The default implementation does nothing, allowing implementations to
     * override only the event types they need to handle.
     * </p>
     *
     * <p>
     * <strong>Implementation Notes:</strong>
     * <ul>
     *   <li>Implementations should be efficient as this method may be called frequently</li>
     *   <li>Consider using {@link Event#consume()} to prevent further processing if appropriate</li>
     *   <li>Handle exceptions within the method to avoid disrupting the event processing pipeline</li>
     *   <li>The method should be thread-safe if events are processed concurrently</li>
     * </ul>
     * </p>
     *
     * @param type the classification type that categorizes this event instance,
     *             used for routing or contextual processing. May be {@code null}
     *             depending on the event system implementation.
     * @param event the event that occurred, never {@code null}
     * @throws NullPointerException if the provided event is {@code null}
     * @throws RuntimeException if the listener encounters an unexpected error
     */
    default void onEvent(T type, E event) { }
}