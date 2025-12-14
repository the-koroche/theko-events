package org.theko.events;

import java.util.HashMap;

/**
 * A type-safe map that binds event classification keys to their handlers.
 * <p>
 * {@code EventMap} is a simple {@link HashMap} wrapper used by the event system
 * to associate an event type (key) with the logic that delivers the event
 * to a listener.
 *
 * <p>
 * It does not perform dispatching by itself — it only stores handler mappings.
 * The actual invocation is done by {@link EventDispatcher}.
 *
 * <pre>{@code
 * EventMap<ResourceEvent, ResourceListener, ResourceEventType> eventMap = new EventMap<>();
 *
 * eventMap.put(ResourceEventType.OPENED, ResourceListener::onOpened);
 * eventMap.put(ResourceEventType.CLOSED, ResourceListener::onClosed);
 *
 * EventHandler<ResourceListener, ResourceEvent> handler = eventMap.get(eventType);
 * if (handler != null) {
 *     handler.handle(listener, event);
 * }
 * }</pre>
 *
 * <p><strong>Thread safety:</strong>
 * This class is not thread-safe. Synchronization must be handled externally
 * if used concurrently.
 *
 * @param <E> event type
 * @param <L> listener type
 * @param <T> event classification key type
 *
 * @author Theko
 * @since 1.0
 * @see HashMap
 * @see EventHandler
 * @see EventDispatcher
 * @see Listener
 * @see Event
 */
public class EventMap<E extends Event, L extends Listener<E, T>, T> extends HashMap<T, EventHandler<L, E>> {

    /**
     * Creates a new empty event map.
     */
    public EventMap() {
    }
}