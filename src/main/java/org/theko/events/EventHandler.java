package org.theko.events;

/**
 * Defines how a specific {@link Event} is delivered to a listener.
 * <p>
 * {@code EventHandler} is a small functional adapter used by the event system
 * to invoke the correct listener method for a given event type.
 *
 * <p>
 * The dispatcher does not know which listener method should be called.
 * It only knows the event and the listener instance. This interface
 * provides the logic that connects them.
 *
 * <pre>{@code
 * eventMap.put(EventType.OPENED, ResourceListener::onOpened);
 *
 * // Custom dispatch logic
 * eventMap.put(EventType.CLOSED, (listener, event) -> {
 *     if (event.isSuccessful()) {
 *         listener.onClosedSuccessfully(event);
 *     } else {
 *         listener.onClosedWithError(event);
 *     }
 * });
 * }</pre>
 *
 * <p><strong>Thread safety:</strong>
 * Implementations must be thread-safe if events are dispatched concurrently.
 *
 * @param <L> listener type
 * @param <E> event type
 *
 * @author Theko
 * @since 1.0
 * @see Event
 * @see EventDispatcher
 * @see EventMap
 */
@FunctionalInterface
public interface EventHandler<L, E extends Event> {
    
    /**
     * Handles the given event for the specified listener.
     * <p>
     * This method is called by the {@link EventDispatcher} during event dispatch.
     * The implementation is responsible for invoking the appropriate listener method.
     *
     * @param listener listener instance, must not be {@code null}
     * @param event event instance, must not be {@code null}
     *
     * @throws NullPointerException if {@code listener} or {@code event} is {@code null}
     * @throws RuntimeException if an error occurs while handling the event
     */
    void handle(L listener, E event);
}