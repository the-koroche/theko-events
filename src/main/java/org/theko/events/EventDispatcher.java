package org.theko.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A comprehensive event dispatcher that manages event routing, listener registration,
 * and exception handling in an event-driven system.
 * <p>
 * The {@code EventDispatcher} is the central component responsible for:
 * <ul>
 *   <li>Registering and managing {@link Listener} instances and {@link EventConsumer}s</li>
 *   <li>Dispatching events to appropriate handlers based on event type and priority</li>
 *   <li>Managing execution order through {@link ListenerPriority} levels</li>
 *   <li>Providing robust exception handling through {@link EventExceptionHandler}s</li>
 *   <li>Supporting event consumption to prevent further processing</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Event Processing Flow:</strong>
 * <ol>
 *   <li>Events are dispatched via {@link #dispatch(Object, Event)}</li>
 *   <li>The dispatcher looks up the appropriate {@link EventHandler} for the event type</li>
 *   <li>Listeners are processed in priority order (HIGHEST to LOW)</li>
 *   <li>Event consumers for the specific event type are processed in priority order</li>
 *   <li>If an event is consumed, further processing is stopped</li>
 *   <li>Exceptions are routed to registered exception handlers</li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety:</strong> This class is not thread-safe by default. Concurrent
 * access requires external synchronization. Consider using {@code synchronized} blocks
 * or concurrent collections if used in multi-threaded environments.
 * </p>
 *
 * @param <E> the type of event being dispatched, must extend {@link Event}
 * @param <L> the type of listener that can receive events, must extend {@link Listener}
 * @param <T> the classification type used for event routing and mapping
 *
 * @author Theko
 * @since 1.0
 * @see Event
 * @see Listener
 * @see EventConsumer
 * @see EventHandler
 * @see EventExceptionHandler
 * @see ListenerPriority
 */
public class EventDispatcher<E extends Event, L extends Listener<E, T>, T> {

    /**
     * Registry of listeners grouped by their priority level.
     * Listeners with higher priority are processed first.
     */
    private final Map<ListenerPriority, List<L>> listeners = new EnumMap<>(ListenerPriority.class);
    
    /**
     * Registry of event consumers grouped by their priority level.
     * Consumers are wrapped to associate them with their event type.
     */
    private final Map<ListenerPriority, List<EventConsumerWrapper>> consumers = new EnumMap<>(ListenerPriority.class);
    
    /**
     * Mapping from original event consumers to their wrapper instances
     * for efficient removal and lookup operations.
     */
    private final Map<EventConsumer<E>, EventConsumerWrapper> consumerWrappers = new HashMap<>();
    
    /**
     * Mapping from event types to their corresponding event handlers.
     * Defines how events of specific types should be delivered to listeners.
     */
    private final Map<T, EventHandler<L, E>> eventMap = new HashMap<>();
    
    /**
     * Registry of exception handlers for processing errors that occur
     * during event dispatch to listeners or consumers.
     */
    private final List<ExceptionHandlerWrapper<?>> exceptionHandlers = new ArrayList<>();

    /**
     * Internal wrapper class that associates an {@link EventConsumer} with its
     * specific event type for efficient filtering during event dispatch.
     */
    private class EventConsumerWrapper {
        /** The wrapped event consumer instance */
        final EventConsumer<E> consumer;
        
        /** The event type this consumer is registered for */
        final T eventType;

        /**
         * Creates a new wrapper for the specified event consumer and type.
         *
         * @param consumer the event consumer to wrap
         * @param eventType the event type this consumer handles
         */
        EventConsumerWrapper(EventConsumer<E> consumer, T eventType) {
            this.consumer = consumer;
            this.eventType = eventType;
        }

        /**
         * Delegates event consumption to the wrapped consumer.
         *
         * @param event the event to consume
         */
        void consume(E event) {
            consumer.consume(event);
        }
    }

    /**
     * Internal wrapper class that associates an {@link EventExceptionHandler}
     * with the specific exception type it can handle.
     *
     * @param <EX> the type of exception this handler can process
     */
    private class ExceptionHandlerWrapper<EX extends Throwable> {
        /** The wrapped exception handler instance */
        final EventExceptionHandler<L, E, EX> handler;
        
        /** The exception type this handler can process */
        final Class<EX> exceptionType;

        /**
         * Creates a new wrapper for the specified exception handler and type.
         *
         * @param handler the exception handler to wrap
         * @param exceptionType the exception type this handler can process
         */
        ExceptionHandlerWrapper(EventExceptionHandler<L, E, EX> handler, Class<EX> exceptionType) {
            this.handler = handler;
            this.exceptionType = exceptionType;
        }

        /**
         * Checks if this handler can process the given exception.
         *
         * @param exception the exception to check
         * @return {@code true} if this handler can process the exception, {@code false} otherwise
         */
        boolean canHandle(Throwable exception) {
            return exceptionType.isInstance(exception);
        }

        /**
         * Handles the exception using the wrapped handler with proper type casting.
         *
         * @param listener the listener that was processing the event, may be {@code null}
         * @param event the event that was being processed
         * @param exception the exception that occurred
         */
        @SuppressWarnings("unchecked")
        void handle(L listener, E event, Throwable exception) {
            handler.handle(listener, event, (EX) exception);
        }
    }

    /**
     * Replaces the current event mapping with the specified map.
     * <p>
     * This method completely replaces the existing event handler mappings with
     * the provided map. Existing mappings are cleared before adding the new ones.
     * </p>
     *
     * @param map the new event mapping to use, must not be {@code null}
     * @throws NullPointerException if the provided map is {@code null}
     */
    public void setEventMap(Map<T, EventHandler<L, E>> map) {
        eventMap.clear();
        eventMap.putAll(map);
    }

    /**
     * Creates and returns a new {@link EventMap} instance configured with the same
     * type parameters as this event dispatcher.
     * <p>
     * This factory method provides a convenient way to create event maps that are
     * type-compatible with this dispatcher. The returned {@code EventMap} can be
     * populated with event handlers and then applied to this dispatcher using
     * {@link #setEventMap(Map)}.
     * </p>
     *
     * <p>
     * <strong>Typical Usage:</strong>
     * <pre>{@code
     * EventDispatcher<MyEvent, MyListener, MyEventType> dispatcher = new EventDispatcher<>();
     * EventMap<MyEvent, MyListener, MyEventType> eventMap = dispatcher.createEventMap();
     * 
     * // Populate the event map with handlers
     * eventMap.put(MyEventType.OPEN, MyListener::onOpen);
     * eventMap.put(MyEventType.CLOSE, MyListener::onClose);
     * eventMap.put(MyEventType.UPDATE, MyListener::onUpdate);
     * 
     * // Apply the configured event map to the dispatcher
     * dispatcher.setEventMap(eventMap);
     * }</pre>
     * </p>
     *
     * <p>
     * The returned {@code EventMap} is empty and must be populated with event handlers
     * before use. This method is particularly useful when building complex event mapping
     * configurations that require multiple setup steps.
     * </p>
     *
     * @return a new, empty {@link EventMap} instance configured with the same type
     *         parameters {@code <E, L, T>} as this event dispatcher
     *
     * @see EventMap
     * @see #setEventMap(Map)
     * @see EventHandler
     *
     * @apiNote This method does not affect the current event mapping of the dispatcher.
     *          The returned map is independent and must be explicitly set using
     *          {@link #setEventMap(Map)} to become active.
     */
    public EventMap<E, L, T> createEventMap() {
        return new EventMap<E, L, T>();
    }

    /**
     * Registers a listener with the specified priority level.
     * <p>
     * Listeners with higher priority will receive events before listeners with lower priority.
     * Multiple listeners with the same priority are processed in registration order.
     * </p>
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
        addListener(ListenerPriority.NORMAL, listener);
    }

    /**
     * Registers an event consumer with the specified priority level and event type.
     * <p>
     * Event consumers provide a functional alternative to full listener implementations
     * for simpler event handling scenarios.
     * </p>
     *
     * @param priority the priority level for the consumer
     * @param eventType the specific event type this consumer handles, must not be {@code null}
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either priority, eventTypem, or consumer is {@code null}
     */
    public void addConsumer(ListenerPriority priority, T eventType, EventConsumer<E> consumer) {
        if (consumer == null) throw new NullPointerException("Consumer is null.");
        if (eventType == null) throw new NullPointerException("Event type is null.");
        EventConsumerWrapper wrapper = new EventConsumerWrapper(consumer, eventType);
        consumerWrappers.put(consumer, wrapper);
        consumers.computeIfAbsent(priority, k -> new LinkedList<>()).add(wrapper);
    }

    /**
     * Registers an event consumer with {@link ListenerPriority#NORMAL} priority.
     *
     * @param eventType the specific event type this consumer handles, must not be {@code null}
     * @param consumer the event consumer to register, must not be {@code null}
     * @throws NullPointerException if either eventType or consumer is {@code null}
     */
    public void addConsumer(T eventType, EventConsumer<E> consumer) {
        addConsumer(ListenerPriority.NORMAL, eventType, consumer);
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
     * Removes an event consumer from all priority levels.
     *
     * @param consumer the event consumer to remove
     * @return {@code true} if the consumer was found and removed, {@code false} otherwise
     */
    public boolean removeConsumer(EventConsumer<E> consumer) {
        EventConsumerWrapper wrapper = consumerWrappers.remove(consumer);
        if (wrapper == null) return false;
        
        return consumers.values().stream()
                .filter(Objects::nonNull)
                .anyMatch(list -> list.remove(wrapper));
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
     * Checks if an event consumer is currently registered.
     *
     * @param consumer the event consumer to check
     * @return {@code true} if the consumer is registered, {@code false} otherwise
     */
    public boolean hasConsumer(EventConsumer<E> consumer) {
        return consumerWrappers.containsKey(consumer);
    }

    /**
     * Returns all registered listeners in registration order (not priority order).
     * <p>
     * The returned list is unmodifiable and reflects the current state of registered listeners.
     * </p>
     *
     * @return an unmodifiable list of all registered listeners
     */
    public List<L> getListeners() {
        return Arrays.stream(ListenerPriority.values())
                .map(priority -> listeners.getOrDefault(priority, Collections.emptyList()))
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all registered event consumers in registration order (not priority order).
     * <p>
     * The returned list is unmodifiable and reflects the current state of registered consumers.
     * </p>
     *
     * @return an unmodifiable list of all registered event consumers
     */
    public List<EventConsumer<E>> getConsumers() {
        return Arrays.stream(ListenerPriority.values())
                .map(priority -> consumers.getOrDefault(priority, Collections.emptyList()))
                .flatMap(List::stream)
                .map(wrapper -> wrapper.consumer)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Registers an exception handler for a specific exception type.
     * <p>
     * If a handler already exists for the specified exception type, it is replaced
     * with the new handler. Exception handlers are invoked in reverse registration order
     * (last registered, first called) until one successfully handles the exception.
     * </p>
     *
     * @param <EX> the type of exception to handle
     * @param exceptionType the class object representing the exception type
     * @param handler the exception handler to register
     * @throws NullPointerException if either exceptionType or handler is {@code null}
     */
    public <EX extends Throwable> void addExceptionHandler(
            Class<EX> exceptionType, 
            EventExceptionHandler<L, E, EX> handler) {
        
        exceptionHandlers.removeIf(wrapper -> wrapper.exceptionType.equals(exceptionType));
        exceptionHandlers.add(new ExceptionHandlerWrapper<>(handler, exceptionType));
    }

    /**
     * Dispatches an event to all registered listeners and consumers for the specified event type.
     * <p>
     * The dispatch process follows this sequence:
     * <ol>
     *   <li>Looks up the event handler for the specified event type</li>
     *   <li>Processes all listeners in priority order (HIGHEST to LOW)</li>
     *   <li>Processes all consumers for the specific event type in priority order</li>
     *   <li>Stops processing if the event is consumed at any point</li>
     *   <li>Routes any exceptions to registered exception handlers</li>
     * </ol>
     * </p>
     *
     * @param eventType the type classification of the event being dispatched
     * @param event the event instance to dispatch
     * @throws NullPointerException if either eventType or event is {@code null}
     */
    public void dispatch(T eventType, E event) {
        EventHandler<L, E> handler = eventMap.get(eventType);
        if (handler == null) return;

        // Process listeners in priority order
        for (L listener : getListenersInPriorityOrder()) {
            try {
                handler.handle(listener, event);
                if (event.isConsumed()) break;
            } catch (Throwable ex) {
                handleException(listener, event, ex);
            }
        }

        // Process consumers for the specific event type
        for (EventConsumerWrapper wrapper : getConsumersForEventType(eventType)) {
            try {
                wrapper.consume(event);
                if (event.isConsumed()) break;
            } catch (Throwable ex) {
                handleException(null, event, ex);
            }
        }
    }

    /**
     * Returns all listeners in descending priority order (HIGHEST first).
     *
     * @return a list of listeners ordered by priority
     */
    private List<L> getListenersInPriorityOrder() {
        return Arrays.stream(ListenerPriority.values())
                .sorted(Comparator.naturalOrder())
                .map(priority -> listeners.getOrDefault(priority, Collections.emptyList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns all consumers for a specific event type in descending priority order.
     *
     * @param eventType the event type to filter consumers by
     * @return a list of consumers for the specified event type, ordered by priority
     */
    private List<EventConsumerWrapper> getConsumersForEventType(T eventType) {
        return Arrays.stream(ListenerPriority.values())
                .sorted(Comparator.reverseOrder())
                .map(priority -> consumers.getOrDefault(priority, Collections.emptyList()))
                .flatMap(List::stream)
                .filter(wrapper -> wrapper.eventType.equals(eventType))
                .collect(Collectors.toList());
    }

    /**
     * Handles an exception that occurred during event processing by routing it
     * to the appropriate registered exception handler.
     *
     * @param listener the listener that was processing the event, may be {@code null}
     * @param event the event that was being processed
     * @param exception the exception that occurred
     */
    private void handleException(L listener, E event, Throwable exception) {
        for (ExceptionHandlerWrapper<?> wrapper : exceptionHandlers) {
            if (wrapper.canHandle(exception)) {
                try {
                    wrapper.handle(listener, event, exception);
                    break;
                } catch (Throwable ex) {
                    System.err.println("Exception handler failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
}