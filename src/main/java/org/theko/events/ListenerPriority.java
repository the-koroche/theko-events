package org.theko.events;

/**
 * Defines priority levels for event listeners in the event processing pipeline.
 * <p>
 * Listeners with higher priority are executed before listeners with lower priority
 * when multiple listeners are registered for the same event type. This allows for
 * controlling the order of event processing, which is useful for:
 * <ul>
 *   <li>Pre-processing or validation before main handling</li>
 *   <li>Ensuring critical handlers execute first</li>
 *   <li>Implementing filtering or interception patterns</li>
 *   <li>Managing dependencies between event handlers</li>
 * </ul>
 * </p>
 *
 * <p>
 * The priority levels are ordered from highest to lowest as follows:
 * {@code HIGHEST > HIGH > NORMAL > LOW}
 * </p>
 *
 * <p>
 * <strong>Execution Order Example:</strong>
 * If multiple listeners are registered for the same event with different priorities,
 * they will be executed in this order:
 * <ol>
 *   <li>Listeners with {@code HIGHEST} priority</li>
 *   <li>Listeners with {@code HIGH} priority</li>
 *   <li>Listeners with {@code NORMAL} priority (default if not specified)</li>
 *   <li>Listeners with {@code LOW} priority</li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Note:</strong> The relative order of listeners with the same priority level
 * is implementation-defined and should not be relied upon.
 * </p>
 *
 * @author Theko
 * @since 1.0
 * @see EventDispatcher
 * @see Listener
 * @see EventConsumer
 */
public enum ListenerPriority {
    
    /**
     * The highest priority level. Listeners with this priority will be executed
     * first in the event processing chain. Typically used for:
     * <ul>
     *   <li>Critical system-level validations</li>
     *   <li>Security checks and access control</li>
     *   <li>Event preprocessing and transformation</li>
     *   <li>Logging and auditing requirements</li>
     * </ul>
     */
    HIGHEST,
    
    /**
     * High priority level. Executed after {@code HIGHEST} but before {@code NORMAL}
     * priority listeners. Suitable for:
     * <ul>
     *   <li>Important business logic validations</li>
     *   <li>Early-stage event processing</li>
     *   <li>Handlers that might consume the event to prevent further processing</li>
     * </ul>
     */
    HIGH,
    
    /**
     * The default/normal priority level. Used for most typical event handling logic.
     * Executed after {@code HIGH} priority listeners. Appropriate for:
     * <ul>
     *   <li>Main business logic processing</li>
     *   <li>Standard event handlers</li>
     *   <li>Most application-level event responses</li>
     * </ul>
     */
    NORMAL,
    
    /**
     * Low priority level. Executed last in the event processing chain. Typically used for:
     * <ul>
     *   <li>Cleanup operations</li>
     *   <li>Non-critical side effects</li>
     *   <li>Final logging or monitoring</li>
     *   <li>Handlers that should run only if event wasn't consumed earlier</li>
     * </ul>
     */
    LOW
}