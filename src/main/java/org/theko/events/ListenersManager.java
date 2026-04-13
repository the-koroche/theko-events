package org.theko.events;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Central manager for listeners and event consumers.
 * <p>
 * This class is responsible for registering, unregistering, and retrieving listeners
 * and event consumers, as well as managing their priority levels.
 * 
 * @param <E> the type of event being handled, must extend {@link Event}
 * @param <L> the type of listener being managed, must extend {@link Listener}
 * @param <T> the classification type used for event routing
 * 
 * @see Listener
 * @see EventConsumer
 * @see ListenerPriority
 * 
 * @author Theko
 * @since 2.0.0
 */
public class ListenersManager<E extends Event, L extends Listener<E>, T> {

    private static final ListenerPriority DEFAULT_PRIORITY = ListenerPriority.NORMAL;

    // Listeners grouped by priority (CRITICAL to LOW).
    private final Map<ListenerPriority, List<L>> listeners = new ConcurrentHashMap<>();

    // Event consumers grouped by priority, wrapped for event type association.
    private final Map<ListenerPriority, List<EventConsumerWrapper>> consumers = new ConcurrentHashMap<>();

    // Lookup map from original consumer to wrapper, for efficient removal.
    private final Map<EventConsumer<E, T>, EventConsumerWrapper> consumerWrappers = new ConcurrentHashMap<>();
    
    /*
     * Internal wrapper class that associates an {@link EventConsumer} with its
     * specific event type.
     */
    private class EventConsumerWrapper {

        // The wrapped event consumer instance
        final EventConsumer<E, T> consumer;
        
        // The event type this consumer is registered for
        final T eventType;

        EventConsumerWrapper(EventConsumer<E, T> consumer, T eventType) {
            this.consumer = consumer;
            this.eventType = eventType;
        }
    }

    /**
     * Creates a new instance of {@link ListenersManager}.
     */
    public ListenersManager() {
    }

    /**
     * Registers a listener with the specified priority level.
     * <p>
     * Listeners with higher priority will receive events before listeners with lower priority.
     * Multiple listeners with the same priority are processed in registration order.
     * 
     * @param priority the priority level for the listener
     * @param listener the listener to register, must not be {@code null}
     * @throws NullPointerException if the listener or priority is {@code null}
     */
    public void addListener(ListenerPriority priority, L listener) {
        if (listener == null) throw new NullPointerException("Listener is null.");
        listeners.computeIfAbsent(priority, k -> new LinkedList<>()).add(listener);
    }

    /**
     * Registers a listener with {@link ListenerPriority#NORMAL} priority.
     *
     * @param listener the listener to register, must not be {@code null}
     * @throws NullPointerException if the listener is {@code null}
     */
    public void addListener(L listener) {
        addListener(DEFAULT_PRIORITY, listener);
    }

    /**
     * Checks if a listener is currently registered.
     *
     * @param listener the listener to check
     * @return {@code true} if the listener is registered, {@code false} otherwise
     */
    public boolean hasListener(L listener) {
        return listeners.values().stream()
                .filter(Objects::nonNull)
                .anyMatch(list -> list.contains(listener));
    }

    /**
     * Removes a listener from all priority levels.
     *
     * @param listener the listener to remove
     * @return {@code true} if the listener was found and removed, {@code false} otherwise
     */
    public boolean removeListener(L listener) {
        return listeners.values().stream()
                .filter(Objects::nonNull)
                .anyMatch(list -> list.remove(listener));
    }

    /**
     * Returns a sorted list of all registered listeners.
     * <p>
     * The list is sorted by the listener priority level ({@link ListenerPriority}).
     * 
     * @return a sorted list of all registered listeners
     */
    public List<L> getListeners() {
        return listeners.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Registers an event consumer with the specified priority level and event type.
     * <p>
     * Event consumers provide a functional alternative to full listener implementations
     * for simpler event handling scenarios.
     * 
     * @param priority the priority level for the consumer
     * @param eventType the specific event type this consumer handles, must not be {@code null}
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either priority, eventTypem, or consumer is {@code null}
     */
    public void addConsumer(ListenerPriority priority, T eventType, EventConsumer<E, T> consumer) {
        if (consumer == null) throw new NullPointerException("Consumer is null.");
        EventConsumerWrapper wrapper = new EventConsumerWrapper(consumer, eventType);
        consumerWrappers.put(consumer, wrapper);
        consumers.computeIfAbsent(priority, k -> new LinkedList<>()).add(wrapper);
    }

    /**
     * Registers an event consumer with the specified priority level.
     * <p>
     * The event type is not specified, so the consumer will receive all events.
     * 
     * @param priority the priority level for the consumer
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either priority or consumer is {@code null}
     */
    public void addConsumer(ListenerPriority priority, EventConsumer<E, T> consumer) {
        addConsumer(priority, null, consumer);
    }

    /**
     * Registers an event consumer with {@link ListenerPriority#NORMAL} priority.
     *
     * @param eventType the specific event type this consumer handles, must not be {@code null}
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either eventType or consumer is {@code null}
     */
    public void addConsumer(T eventType, EventConsumer<E, T> consumer) {
        addConsumer(DEFAULT_PRIORITY, eventType, consumer);
    }

    /**
     * Registers an event consumer with {@link ListenerPriority#NORMAL} priority.
     * <p>
     * The event type is not specified, so the consumer will receive all events.
     *
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if consumer is {@code null}
     */
    public void addConsumer(EventConsumer<E, T> consumer) {
        addConsumer((T) null, consumer);
    }

    /**
     * Checks if an event consumer is currently registered.
     *
     * @param consumer the event consumer to check
     * @return {@code true} if the consumer is registered, {@code false} otherwise
     */
    public boolean hasConsumer(EventConsumer<E, T> consumer) {
        return consumerWrappers.containsKey(consumer);
    }

    /**
     * Removes an event consumer from all priority levels.
     *
     * @param consumer the event consumer to remove
     * @return {@code true} if the consumer was found and removed, {@code false} otherwise
     */
    public boolean removeConsumer(EventConsumer<E, T> consumer) {
        EventConsumerWrapper wrapper = consumerWrappers.remove(consumer);
        if (wrapper == null) return false;
        
        return consumers.values().stream()
                .filter(Objects::nonNull)
                .anyMatch(list -> list.remove(wrapper));
    }

    /**
     * Returns a sorted list of all registered event consumers.
     * <p>
     * The list is sorted by the consumer priority level ({@link ListenerPriority}).
     * 
     * @return a sorted list of all registered event consumers
     */
    public List<EventConsumer<E, T>> getConsumers() {
        return consumers.keySet().stream()
                .sorted()
                .map(consumers::get)
                .flatMap(List::stream)
                .map(wrapper -> wrapper.consumer)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns a sorted list of all registered event consumers that match the given event type.
     * <p>
     * The list is sorted by the consumer priority level ({@link ListenerPriority}).
     * If the event type is {@code null}, all event consumers without an event type are returned.
     * 
     * @param eventType the event type to filter by
     * @return a sorted list of all registered event consumers that match the given event type
     */
    public List<EventConsumer<E, T>> getConsumers(T eventType) {
        if (eventType == null) return getConsumers();
        return consumers.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().stream())
                .filter(wrapper -> Objects.equals(wrapper.eventType, eventType) || wrapper.eventType == null)
                .map(wrapper -> wrapper.consumer)
                .collect(Collectors.toUnmodifiableList());
    }
}
