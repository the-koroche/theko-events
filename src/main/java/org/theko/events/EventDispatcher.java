/*
 * MIT License
 * 
 * Copyright (c) 2025 Alex Soloviov (aka Theko)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.theko.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central dispatcher for events, listeners, and consumers in an event-driven system.
 * <p>
 * Responsible for:
 * <ul>
 *   <li>Registering and managing {@link Listener} instances and {@link EventConsumer}s</li>
 *   <li>Dispatching events based on type and priority ({@link ListenerPriority})</li>
 *   <li>Routing exceptions to {@link EventExceptionHandler}s</li>
 *   <li>Supporting event consumption to stop further processing</li>
 * </ul>
 *
 * <p><strong>This class is thread-safe, and dispatching events executes on a caller thread</strong> 
 *
 * @param <E> event type, extends {@link Event}
 * @param <L> listener type, extends {@link Listener}
 * @param <T> classification type for event routing
 * 
 * @see Event
 * @see Listener
 * @see EventConsumer
 * @see EventHandler
 * @see EventExceptionHandler
 * @see ListenerPriority
 * 
 * @author Theko
 * @since 1.0
 */
public class EventDispatcher<E extends Event, L extends Listener<E>, T> implements ListenersManageable<E, L, T> {

    private final EventExceptionHandler<E, L, Throwable> DEFAULT_EXCEPTION_HANDLER = new EventExceptionHandler<>() {
        @Override
        public void handle(L listener, E event, Throwable exception) {
            boolean isConsumer = listener == null; // Consumer is anonymous type, so always null

            String message = "Unhandled exception in %s, event: %s, exception: %s";
            String callerName = isConsumer ? "consumer" : listener.getClass().getSimpleName();
            message = String.format(message, callerName, event.getClass().getSimpleName(), exception.getClass().getSimpleName());

            System.err.println(message);
            exception.printStackTrace(System.err);
        }
    };
    
    /* Listener management */
    // Provides access to listener management operations for this dispatcher.
    private final ListenersManager<E, L, T> listenersManager = new ListenersManager<>();

    /* Event dispatching */
    // Mapping from event types to their corresponding handlers.
    private final Map<T, EventHandler<E, L>> eventMap = new ConcurrentHashMap<>();

    // Registered exception handlers in registration order (last registered = first called).
    private final List<ExceptionHandlerWrapper<?>> exceptionHandlers = new CopyOnWriteArrayList<>();

    /**
     * Internal wrapper class that associates an {@link EventExceptionHandler}
     * with the specific exception type it can handle.
     *
     * @param <X> the type of exception this handler can process
     */
    private class ExceptionHandlerWrapper<X extends Throwable> {

        // The wrapped exception handler instance
        final EventExceptionHandler<E, L, X> handler;
        
        // The exception type this handler can process
        final Class<X> exceptionType;

        ExceptionHandlerWrapper(EventExceptionHandler<E, L, X> handler, Class<X> exceptionType) {
            this.handler = handler;
            this.exceptionType = exceptionType;
        }

        // Checks if the given exception is of the type this handler can handle
        boolean canHandle(Throwable exception) {
            return exceptionType.isInstance(exception);
        }

        // Handles the exception using the wrapped handler with proper type casting
        @SuppressWarnings("unchecked")
        void handle(L listener, E event, Throwable exception) {
            handler.handle(listener, event, (X) exception);
        }
    }

    /**
     * Creates a new event dispatcher with the default exception handler.
     */
    public EventDispatcher() {
        addExceptionHandler(Throwable.class, DEFAULT_EXCEPTION_HANDLER);
    }

    /**
     * Replaces the current event mapping with the specified map.
     * <p>
     * This method completely replaces the existing event handler mappings with
     * the provided map. Existing mappings are cleared before adding the new ones.
     * 
     * @param map the new event mapping to use, must not be {@code null}
     * @throws NullPointerException if the provided map is {@code null}
     */
    public void setEventMap(Map<T, EventHandler<E, L>> map) {
        eventMap.clear();
        eventMap.putAll(map);
    }

    /**
     * Creates a new empty {@link EventMap} with compatible type parameters.
     * 
     * @return a new empty event map configured for this dispatcher's types
     * @see #setEventMap(Map)
     */
    public EventMap<E, L, T> createEventMap() {
        return new EventMap<E, L, T>();
    }

    /**
     * Returns the listener manager for this dispatcher.
     * 
     * @return the listener manager instance, never null
     */
    @Override
    public ListenersManager<E, L, T> getListenersManager() {
        return listenersManager;
    }

    /**
     * Registers an exception handler for a specific exception type.
     * <p>
     * If a handler already exists for the specified exception type, it is replaced
     * with the new handler. Exception handlers are invoked in reverse registration order
     * (last registered, first called) until one successfully handles the exception.
     * 
     * @param <X> the type of exception to handle
     * @param exceptionType the class object representing the exception type
     * @param handler the exception handler to register
     * @throws NullPointerException if either exceptionType or handler is {@code null}
     */
    public <X extends Throwable> void addExceptionHandler(
            Class<X> exceptionType, 
            EventExceptionHandler<E, L, X> handler) {
        
        exceptionHandlers.removeIf(wrapper -> wrapper.exceptionType.equals(exceptionType));
        exceptionHandlers.add(new ExceptionHandlerWrapper<>(handler, exceptionType));
    }

    /**
     * Dispatches an event to all registered listeners and consumers.
     * <p>
     * Listeners are processed in priority order, and event consumption is respected.
     * If an exception is thrown during event processing, it is routed to the associated exception handler.
     * If no exception handler is registered for the exception type, the stack trace is printed.
     * <p>
     * If eventType is null, then only consumers are processed.
     * 
     * @param eventType the event classification key to filter consumers by, or null for no filtering
     * @param event the event to dispatch
     * 
     * @throws NullPointerException if event is null
     */
    public void dispatch(T eventType, E event) {
        if (event == null) throw new NullPointerException("Event is null.");
        EventHandler<E, L> handler = (eventType == null) ? null : eventMap.get(eventType);

        // Process listeners in priority order
        for (L listener : listenersManager.getListeners()) {
            if (handler == null || event.isConsumed()) break;
            if (listener == null) continue;
            try {
                handler.handle(listener, event);
            } catch (Throwable ex) {
                handleException(listener, event, ex);
            }
        }

        // Process consumers for the specific event type
        for (EventConsumer<E, T> consumer : listenersManager.getConsumers(eventType)) {
            try {
                if (event.isConsumed()) break;
                if (consumer == null) continue;
                consumer.consume(eventType, event);
            } catch (Throwable ex) {
                handleException(null, event, ex);
            }
        }
    }

    /*
     * Handles an exception thrown while processing an event.
     *
     * Iterates through the list of registered exception handlers in reverse registration order
     * (last registered, first called) until one successfully handles the exception.
     * If no exception handler can handle the exception, the stack trace is printed.
     */
    private void handleException(L listener, E event, Throwable exception) {
        boolean handled = false;

        for (ExceptionHandlerWrapper<?> wrapper : exceptionHandlers) {
            if (wrapper.canHandle(exception)) {
                try {
                    wrapper.handle(listener, event, exception);
                    handled = true;
                    break;
                } catch (Throwable ex) {
                    System.err.println("Exception handler failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

        if (!handled) {
            exception.printStackTrace(System.err);
        }
    }
}