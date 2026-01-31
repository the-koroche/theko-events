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

/**
 * Provides a way to obtain a {@link ListenersManager} instance.
 * <p>
 * Implementations of this interface should return a fully configured and
 * ready-to-use {@link ListenersManager} instance.
 * <p>
 * This interface is useful for injection of {@link ListenersManager} instances
 * into other classes or for providing a global {@link ListenersManager} instance
 * that can be accessed from anywhere in the application.
 *
 * @param <E> the type of event being handled, must extend {@link Event}
 * @param <L> the type of listener being managed, must extend {@link Listener}
 * @param <T> the classification type used for event routing
 *
 * @author Theko
 * @since 1.2.3
 */
public interface ListenersManagerProvider<E extends Event, L extends Listener<E, T>, T> {

    /**
     * Returns the {@link ListenersManager} instance.
     *
     * @return the listeners manager instance
     */
    ListenersManager<E, L, T> getListenersManager();
}
