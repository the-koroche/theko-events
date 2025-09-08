package org.theko.events;

import java.util.HashMap;

/**
 * A specialized map that associates event classification types with their corresponding
 * event handlers in the event dispatch system.
 * <p>
 * {@code EventMap} extends {@link HashMap} to provide a type-safe mapping between
 * event classification types ({@code T}) and the {@link EventHandler} instances that
 * know how to deliver events of type {@code E} to listeners of type {@code L}.
 * </p>
 *
 * <p>
 * This class serves as the central registry for event routing configuration, enabling
 * the event system to efficiently dispatch events to the appropriate listener methods
 * based on their classification type.
 * </p>
 *
 * <p>
 * <strong>Type Parameters:</strong>
 * <ul>
 *   <li>{@code E} - The specific type of {@link Event} being handled</li>
 *   <li>{@code L} - The type of {@link Listener} that will receive the events</li>
 *   <li>{@code T} - The classification type used to categorize and route events</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Usage Example:</strong>
 * <pre>{@code
 * EventMap<ResourceEvent, ResourceListener, ResourceEventType> eventMap = new EventMap<>();
 * 
 * // Register event handlers using method references
 * eventMap.put(ResourceEventType.OPENED, ResourceListener::onOpened);
 * eventMap.put(ResourceEventType.CLOSED, ResourceListener::onClosed);
 * eventMap.put(ResourceEventType.MODIFIED, ResourceListener::onModified);
 * 
 * // Later, during event dispatch:
 * EventHandler<ResourceListener, ResourceEvent> handler = eventMap.get(eventType);
 * if (handler != null) {
 *     handler.handle(listener, event);
 * }
 * }</pre>
 * </p>
 *
 * <p>
 * <strong>Implementation Notes:</strong>
 * <ul>
 *   <li>Inherits all HashMap operations including null key support (if allowed by type {@code T})</li>
 *   <li>Provides O(1) average time complexity for get and put operations</li>
 *   <li>Not thread-safe by default; concurrent access requires external synchronization</li>
 *   <li>Typically used as a building block within {@link EventDispatcher} implementations</li>
 * </ul>
 * </p>
 *
 * @param E the type of event being handled, must extend {@link Event}
 * @param L the type of listener that will receive the events, must extend {@link Listener}
 * @param T the classification type used for event routing
 *
 * @author Theko
 * @since 1.0
 * @see HashMap
 * @see EventHandler
 * @see Listener
 * @see Event
 */
public class EventMap<E extends Event, L extends Listener<E, T>, T> extends HashMap<T, EventHandler<L, E>> {
}