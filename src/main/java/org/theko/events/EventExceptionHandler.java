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