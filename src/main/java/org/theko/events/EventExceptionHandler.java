package org.theko.events;

/**
 * A functional interface for handling exceptions that occur during event processing
 * by event listeners in the event dispatch system.
 * <p>
 * {@code EventExceptionHandler} provides a mechanism to gracefully handle errors
 * and exceptions that may be thrown by {@link EventHandler} implementations or
 * listener methods during event processing. This prevents exceptions from disrupting
 * the entire event processing pipeline.
 * </p>
 *
 * <p>
 * This interface supports exception handler chaining through the {@link #andThen(EventExceptionHandler)}
 * method, allowing multiple exception handlers to be composed into a single handler
 * that processes exceptions in sequence.
 * </p>
 *
 * <p>
 * <strong>Common Use Cases:</strong>
 * <ul>
 *   <li>Logging exceptions for debugging and monitoring purposes</li>
 *   <li>Converting checked exceptions to unchecked exceptions</li>
 *   <li>Providing fallback behavior when event processing fails</li>
 *   <li>Notifying administrators or monitoring systems about critical errors</li>
 *   <li>Cleanup and resource management after failed event processing</li>
 * </ul>
 * </p>
 *
 * @param L the type of listener that was processing the event when the exception occurred
 * @param E the type of {@link Event} being processed when the exception was thrown
 * @param T the type of {@link Throwable} that this handler can process
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
     * Handles an exception that occurred during event processing.
     * <p>
     * This method is invoked when an exception is thrown by an {@link EventHandler}
     * or listener method during event processing. The implementation should define
     * the appropriate error handling strategy for the specific exception type.
     * </p>
     *
     * <p>
     * <strong>Implementation Guidelines:</strong>
     * <ul>
     *   <li>Should not throw exceptions that would disrupt the exception handling process</li>
     *   <li>Should be efficient as it may be called in error scenarios where performance matters</li>
     *   <li>Should handle null parameters gracefully if they are possible in the context</li>
     *   <li>Consider thread safety if the event system processes events concurrently</li>
     * </ul>
     * </p>
     *
     * @param listener the listener instance that was processing the event when the exception occurred,
     *                 may be {@code null} in some edge cases
     * @param event the event that was being processed when the exception was thrown, never {@code null}
     * @param exception the exception that was thrown during event processing, never {@code null}
     * @throws NullPointerException if {@code event} or {@code exception} is {@code null}
     */
    void handle(L listener, E event, T exception);

    /**
     * Returns a composed {@code EventExceptionHandler} that first processes exceptions
     * with this handler and then with the specified next handler.
     * <p>
     * The composed handler will invoke {@code this.handle()} first, followed by
     * {@code next.handle()} for the same exception. If either handler throws an exception,
     * it will be propagated to the caller and the subsequent handler will not be invoked.
     * </p>
     *
     * <p>
     * This method enables the creation of exception handler chains for layered error
     * processing strategies.
     * </p>
     *
     * @param next the handler to invoke after this handler processes the exception,
     *             must not be {@code null}
     * @return a composed handler that executes both handlers in sequence
     * @throws NullPointerException if {@code next} is {@code null}
     *
     * @apiNote This method is analogous to {@link java.util.function.Consumer#andThen(Consumer)}
     *          but adapted for exception handling with three parameters.
     *
     * <pre>{@code
     * // Example: Creating a chain of exception handlers
     * EventExceptionHandler<MyListener, MyEvent, RuntimeException> handlerChain =
     *     loggingHandler
     *         .andThen(metricsHandler)
     *         .andThen(recoveryHandler);
     * }</pre>
     */
    default EventExceptionHandler<L, E, T> andThen(EventExceptionHandler<L, E, T> next) {
        return (listener, event, exception) -> {
            this.handle(listener, event, exception);
            next.handle(listener, event, exception);
        };
    }
}