package org.theko.events;

import java.util.List;

/**
 * A convenience wrapper that provides simplified access to listener management operations
 * on an underlying {@link EventDispatcher}. This class delegates all operations to the
 * wrapped dispatcher while providing a cleaner API focused specifically on listener management.
 * 
 * <p>This class is particularly useful when you want to expose only listener management
 * functionality while hiding the full event dispatch capabilities.</p>
 *
 * @param E the type of event being handled, must extend {@link Event}
 * @param L the type of listener being managed, must extend {@link Listener}
 * @param T the classification type used for event routing
 * 
 * @author Theko
 * @since 1.1
 * @see EventDispatcher
 * @see Listener
 * @see EventConsumer
 */
public class ListenersManager<E extends Event, L extends Listener<E, T>, T> {
    
    private final EventDispatcher<E, L, T> dispatcher;

    /**
     * Creates a new ListenersManager that wraps the specified event dispatcher.
     *
     * @param dispatcher the event dispatcher to delegate operations to
     * @throws NullPointerException if the dispatcher is null
     */
    public ListenersManager(EventDispatcher<E, L, T> dispatcher) {
        if (dispatcher == null) {
            throw new NullPointerException("Event dispatcher must not be null.");
        }
        this.dispatcher = dispatcher;
    }

    /**
     * Registers a listener with the specified priority.
     *
     * @param priority the listener priority
     * @param listener the listener to register
     */
    public void addListener(ListenerPriority priority, L listener) {
        dispatcher.addListener(priority, listener);
    }
    
    /**
     * Registers a listener with normal priority.
     *
     * @param listener the listener to register
     */
    public void addListener(L listener) {
        dispatcher.addListener(listener);
    } 
    
    /**
     * Removes a listener.
     *
     * @param listener the listener to remove
     * @return true if the listener was removed
     */
    public boolean removeListener(L listener) {
        return dispatcher.removeListener(listener);
    } 
    
    /**
     * Checks if a listener is registered.
     *
     * @param listener the listener to check
     * @return true if the listener is registered
     */
    public boolean hasListener(L listener) {
        return dispatcher.hasListener(listener);
    } 
    
    /**
     * Registers an event consumer with the specified priority.
     *
     * @param priority the consumer priority
     * @param eventType the event type to consume
     * @param consumer the consumer to register
     */
    public void addConsumer(ListenerPriority priority, T eventType, EventConsumer<E> consumer) {
        dispatcher.addConsumer(priority, eventType, consumer);
    } 
    
    /**
     * Registers an event consumer with normal priority.
     *
     * @param eventType the event type to consume
     * @param consumer the consumer to register
     */
    public void addConsumer(T eventType, EventConsumer<E> consumer) {
        dispatcher.addConsumer(eventType, consumer);
    } 
    
    /**
     * Removes an event consumer.
     *
     * @param consumer the consumer to remove
     * @return true if the consumer was removed
     */
    public boolean removeConsumer(EventConsumer<E> consumer) {
        return dispatcher.removeConsumer(consumer);
    } 
    
    /**
     * Checks if an event consumer is registered.
     *
     * @param consumer the consumer to check
     * @return true if the consumer is registered
     */
    public boolean hasConsumer(EventConsumer<E> consumer) {
        return dispatcher.hasConsumer(consumer);
    } 
    
    /**
     * Gets all registered listeners.
     *
     * @return a list of all listeners
     */
    public List<L> getListeners() {
        return dispatcher.getListeners();
    } 
    
    /**
     * Gets all registered event consumers.
     *
     * @return a list of all consumers
     */
    public List<EventConsumer<E>> getConsumers() {
        return dispatcher.getConsumers(); 
    }
}
