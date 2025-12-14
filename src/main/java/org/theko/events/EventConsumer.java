package org.theko.events;

/**
 * A functional interface representing a consumer of events of a specific type.
 * <p>
 * Event consumers are responsible for processing events that are dispatched through
 * the event system. Each consumer defines the business logic to execute when an event
 * of the specified type occurs.
 * 
 *
 * <p>
 * This interface is designed to be implemented as a lambda expression, method reference,
 * or anonymous class, making it easy to define event handling behavior concisely.
 * 
 *
 * <p>
 * <strong>Implementation Guidelines:</strong>
 * <ul>
 *   <li>Consumers should be designed to handle events efficiently as they may be
 *       called frequently in performance-critical code paths</li>
 *   <li>Consumers should generally not block or perform long-running operations
 *       synchronously to avoid blocking the event dispatch thread</li>
 *   <li>Consumers should be thread-safe if the event system processes events
 *       concurrently from multiple threads</li>
 *   <li>Consumers can call {@link Event#consume()} on the event to prevent
 *       further processing by subsequent consumers</li>
 * </ul>
 * 
 *
 * @param <E> the type of event this consumer can handle, must extend {@link Event}
 *
 * @author Theko
 * @since 1.0
 * @see Event
 * @see EventDispatcher
 */
@FunctionalInterface
public interface EventConsumer<E extends Event> {

    /**
     * Processes the specified event.
     * <p>
     * This method is invoked when an event of type {@code E} is dispatched through
     * the event system. Implementations should contain the logic to handle the event,
     * such as updating application state, triggering side effects, or propagating
     * the event to other systems.
     * 
     *
     * <p>
     * <strong>Exception Handling:</strong> Implementations should handle exceptions
     * appropriately. Uncaught exceptions may terminate the event processing pipeline
     * or be logged and swallowed by the event dispatcher, depending on the
     * implementation of the event dispatcher.
     * 
     *
     * @param event the event to be consumed, never {@code null}
     * @throws NullPointerException if the provided event is {@code null}
     * @throws RuntimeException if the consumer encounters an error during processing
     */
    void consume(E event);
}