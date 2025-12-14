package org.theko.events;

/**
 * Defines execution priority for event listeners.
 * <p>
 * Listeners with higher priority are executed before lower-priority ones.
 */
public enum ListenerPriority {
    
    HIGHEST,
    HIGH,
    NORMAL,
    LOW
}