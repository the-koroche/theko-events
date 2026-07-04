/*
 * MIT License
 *
 * Copyright (c) 2025-present Alex Soloviov (aka Theko)
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

/**
 * A specialized event dispatcher designed for dynamic, weakly-typed events.
 * <p>
 * Unlike standard strongly-typed dispatchers, this implementation uses {@link DynamicEvent}
 * as a unified event type, allowing data to be passed via key-value pairs without creating
 * explicit subclasses for each event.
 * <p>
 * Events are classified and routed using a {@link String} identifier.
 * <p>
 * Example:
 * <pre>{@code
 * DynamicEventDispatcher dispatcher = new DynamicEventDispatcher();
 *
 * dispatcher.addConsumer("message", (type, event) -> {
 * System.out.println("Message: " + event.get("message"));
 * });
 *
 * dispatcher.dispatch("message", new DynamicEvent()
 * .with("message", "Hello world!"));
 * }</pre>
 *
 * @see DynamicEvent
 * @see EventDispatcher
 * @see Listener
 */
public class DynamicEventDispatcher extends
        EventDispatcher<DynamicEvent, Listener<DynamicEvent>, String> {
}