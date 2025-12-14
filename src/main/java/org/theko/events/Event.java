package org.theko.events;

/**
 * Base class for all events in the event-driven system.
 * <p>
 * Events are immutable data carriers that may be consumed to stop further handling.
 * 
 * @author Theko
 * @since 1.0
 */
public abstract class Event {

    /** Creation timestamp in milliseconds since UNIX epoch. */
    protected final long timestamp = System.currentTimeMillis();

    /** Flag indicating whether this event has been consumed. */
    private boolean isConsumed;

    protected Event() { }

    /** Returns the creation timestamp of this event. */
    public long getTimestamp() {
        return timestamp;
    }

    /** Marks this event as consumed to prevent further handling. */
    public void consume() {
        isConsumed = true;
    }

    /** Checks whether this event has been consumed. */
    public boolean isConsumed() {
        return isConsumed;
    }
}
