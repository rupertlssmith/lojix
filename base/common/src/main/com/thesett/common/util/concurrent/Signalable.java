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
package com.thesett.common.util.concurrent;

/**
 * Signalable is an interface implemented by blocking resources that depend on the state of a second {@link Notifying}
 * resource to change before they can allow threads blocked on them to continue. The notifying resource may choose to
 * wake up just one thread or all threads using the {@link #signal()} and {@link #signalAll()} methods.
 *
 * <p/>For example, There may be a a filter, that prevents more than a certain number of elements being inserted upon
 * unprotected data structures, in order to avoid out of memory conditions. There may be a secondary resource, in this
 * case a data structure that does not limit how many elements can be inserted into it. Threads may block on the filter
 * when there are too many elements in the data structure, and the data structure will inform the filter when the number
 * of elements in it has reduced to a level where more may now be inserted.
 *
 * <p/>Normally data structures encapsulate their acceptance conditions and use internal blocking and notification
 * mechanisms that are not exposed to the caller. This allows a more 'exploded' data structure with pluggable acceptance
 * conditions to be developed.
 *
 * <p/>This interface may also be implemented by any resource that will accept an external signalling stimulus in order
 * to wake up blocked threads, not just {@link Notifying} resources.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr><td>Signal one thread blocked on this resource to wake up.
 * <tr><td>Signal all threads blocked on this resource to wake up.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Signalable
{
    /**
     * Signals to one waiting thread on this resource, that conditions have changed such that it may now be possible for
     * the thread to proceed. This wakes up one waiting thread, in order for it to re-check conditions.
     */
    void signal();

    /**
     * Signals to all waiting threads on this resource, that conditions have changed such that it may now be possible
     * for the thread to proceed. This wakes up all waiting threads, in order for them to re-check conditions.
     */
    void signalAll();
}
