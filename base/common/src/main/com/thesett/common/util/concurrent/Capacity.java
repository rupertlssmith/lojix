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
 * An interface implemented by data structures that have a maximum capacity.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Report the maximum capacity.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Capacity
{
    /**
     * Returns the number of elements that this data structure can ideally (in the absence of memory or resource
     * constraints) accept without blocking, or <tt>Integer.MAX_VALUE</tt> if there is no intrinsic limit.
     *
     * <p>Note that you <em>cannot</em> always tell if an attempt to <tt>add</tt> an element will succeed by inspecting
     * <tt>remainingCapacity</tt> because it may be the case that another thread is about to <tt>put</tt> or <tt>
     * take</tt> an element.
     *
     * @return The remaining capacity.
     */
    public int remainingCapacity();
}
