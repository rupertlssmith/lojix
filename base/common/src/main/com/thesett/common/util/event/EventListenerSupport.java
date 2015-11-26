/*
 * Copyright The Sett Ltd, 2005 to 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.common.util.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

/**
 * EventListenerSupport provides support for managing {@link EventListener}s. A list of listeners is maintained, and
 * listeners can be added/removed from the list. A snapshot of the list may also be obtained, in a thread-safe manner,
 * in order to fire property change events to the listeners.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Allow listeners </td></tr>
 * </table></pre>
 */
public class EventListenerSupport<L extends EventListener>
{
    /** The currently active set of listeners. */
    private volatile List<L> activeListeners;

    /** The definitive list of listeners. */
    private final List<L> listeners = new LinkedList<L>();

    /** A lock to use as a mutex around updates to the list of listeners. */
    private final Object listenersLock = new Object();

    /** {@inheritDoc} */
    public void addListener(L listener)
    {
        // Ensure update to the list, and rebuilding the active array happens atomically.
        synchronized (listenersLock)
        {
            listeners.add(listener);
            activeListeners = new ArrayList<L>(listeners);
        }
    }

    /** {@inheritDoc} */
    public void removeListener(L listener)
    {
        // Ensure update to the list, and rebuilding the active array happens atomically.
        synchronized (listenersLock)
        {
            listeners.remove(listener);
            activeListeners = new ArrayList<L>(listeners);
        }
    }

    /**
     * Provides an array of active listeners.
     *
     * @return An array of active listeners.
     */
    public List<L> getActiveListeners()
    {
        return activeListeners;
    }
}
