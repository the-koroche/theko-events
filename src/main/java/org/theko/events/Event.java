package org.theko.events;

/**
 * Base class for all events in the event-driven system.
 * <p>
 * Represents an occurrence of interest that has happened at a specific moment in time.
 * Events are immutable data carriers that provide contextual information about what occurred.
 * Concrete subclasses should define specific event types with relevant data fields.
 * 
 *
 * <p>
 * Events support a consumption mechanism to prevent further processing by subsequent handlers
 * in the event propagation chain. Once consumed, an event should typically not be processed further.
 * 
 *
 * <p>
 * Each event instance automatically records its creation timestamp using
 * {@link System#currentTimeMillis()}, which can be used for:
 * <ul>
 *   <li>Chronological ordering of events</li>
 *   <li>Measuring processing delays and latency</li>
 *   <li>Debugging and tracing event flow</li>
 *   <li>Temporal analysis of system behavior</li>
 * </ul>
 * 
 *
 * <p><strong>Thread Safety:</strong> This class and its subclasses should be designed to be
 * thread-safe and immutable after construction. Event objects are typically shared across
 * multiple threads in an event processing system.
 *
 * @author Theko
 * @since 1.0
 * @see EventHandler
 * @see EventDispatcher
 * @see EventDispatcher#dispatch(Object, Event)
 */
public abstract class Event {

    /**
     * The creation time of this event in milliseconds since the UNIX epoch (January 1, 1970 UTC).
     * <p>
     * This value is set once during object construction and remains immutable throughout
     * the event's lifecycle. The timestamp represents when the event object was instantiated,
     * which may differ slightly from when the actual occurrence happened.
     * 
     */
    protected final long timestamp = System.currentTimeMillis();

    /**
     * Flag indicating whether this event has been consumed by an event handler.
     * <p>
     * A consumed event should not be processed by subsequent handlers in the event chain.
     * This mechanism allows handlers to indicate that they have fully handled the event
     * and no further processing is required.
     * 
     */
    private boolean isConsumed;

    /**
     * Creates a new event instance.
     */
    protected Event() {
        // Default constructor
    }

    /**
     * Returns the creation timestamp of this event.
     *
     * @return the creation timestamp in milliseconds since the UNIX epoch (00:00:00 UTC, January 1, 1970)
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Marks this event as consumed to prevent further processing.
     * <p>
     * Once an event is consumed, subsequent event handlers should typically skip processing it.
     * This method is idempotent - calling it multiple times has the same effect as calling it once.
     * 
     *
     * @apiNote Consumption is typically used for events that represent user interactions
     *          or system actions where only one handler should respond.
     */
    public void consume() {
        isConsumed = true;
    }

    /**
     * Checks whether this event has been consumed.
     *
     * @return {@code true} if this event has been marked as consumed and should not be
     *         processed further, {@code false} otherwise
     */
    public boolean isConsumed() {
        return isConsumed;
    }
}