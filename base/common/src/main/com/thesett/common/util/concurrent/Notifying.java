/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
 * Notifying is an interface that is implemented by resources that may be temporarily unavailable due to various
 * conditions, where threads may become blocked upon another {@link Signalable} resource as a result and that other
 * resource needs to be notified when conditions have favourably changed.
 *
 * <p/>For example, there may be a data structure that does not limit how many elements can be inserted into it. There
 * may be a second resource, in this example a filter, that prevents more than a certain number of elements being
 * inserted upon the unprotected data structure, in order to avoid out of memory conditions. Threads may block on the
 * filter when there are too many elements in the data structure, and the data structure needs a mechanism to inform the
 * filter when the number of elements in it has reduced to a level where more may now be inserted.
 *
 * <p/>Normally data structures encapsulate their acceptance conditions and use internal blocking and notification
 * mechanisms that are not exposed to the caller. This allows a more 'exploded' data structure with pluggable acceptance
 * conditions to be developed.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr><td>Accept a {@link Signalable} resource to notify when acceptance conditions favourable change.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Notifying
{
    /**
     * Sets a signallable resource on this condition that this condition can use to notify threads waiting on it that
     * they should wake up and recheck the condition in order to see if they can proceed.
     *
     * @param signalable The signallable resource to notify when threads may be able to proceed.
     */
    void setSignalableResource(Signalable signalable);
}
