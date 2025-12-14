package org.theko.events;

/**
 * A functional interface that defines the binding between a specific listener method
 * and an event type in the event dispatch system.
 * <p>
 * {@code EventHandler} serves as a bridge that knows how to deliver a particular
 * {@link Event} to the appropriate method of a listener. This decouples the event
 * dispatch mechanism from the specific listener method signatures.
 * 
 *
 * <p>
 * This interface enables dynamic event routing by allowing the {@link EventDispatcher}
 * to invoke the correct listener method without requiring hardcoded method calls or
 * reflection-based invocation at runtime.
 * 
 *
 * <p>
 * <strong>Implementation Patterns:</strong>
 * Typically implemented using:
 * <ul>
 *   <li>Method references (e.g., {@code MyListener::onSpecificEvent})</li>
 *   <li>Lambda expressions</li>
 *   <li>Anonymous class implementations for complex routing logic</li>
 * </ul>
 * 
 *
 * <p><strong>Thread Safety:</strong> Implementations should be thread-safe if the
 * event system dispatches events concurrently from multiple threads.
 *
 * <pre>{@code
 * // Example: Registering an event handler using method reference
 * eventMap.put(ResourceEventType.OPENED, ResourceListener::onOpened);
 *
 * // Example: Using lambda for custom handling logic
 * eventMap.put(ResourceEventType.CLOSED, (listener, event) -> {
 *     if (event.isSuccessful()) {
 *         listener.onClosedSuccessfully(event);
 *     } else {
 *         listener.onClosedWithError(event);
 *     }
 * });
 * }</pre>
 *
 * @param <L> the type of listener that will receive the event notification
 * @param <E> the type of {@link Event} being handled, must extend {@link Event}
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
     * Delivers the specified event to the appropriate method of the given listener.
     * <p>
     * This method is responsible for invoking the correct listener method with
     * the provided event. The implementation should handle any necessary type
     * casting, parameter extraction, or error handling required for the invocation.
     * 
     *
     * <p>
     * <strong>Contract:</strong>
     * <ul>
     *   <li>The method must not return any value (void return type)</li>
     *   <li>The method should not throw unchecked exceptions unless absolutely necessary</li>
     *   <li>Null parameters should be handled appropriately (typically with {@link NullPointerException})</li>
     *   <li>The method should be efficient as it may be called frequently during event dispatch</li>
     * </ul>
     * 
     *
     * @param listener the listener instance to notify, must not be {@code null}
     * @param event the event to deliver to the listener, must not be {@code null}
     * @throws NullPointerException if either {@code listener} or {@code event} is {@code null}
     * @throws RuntimeException if the handler encounters an error during event delivery
     * @throws ClassCastException if the listener cannot handle the specific event type
     */
    void handle(L listener, E event);
}