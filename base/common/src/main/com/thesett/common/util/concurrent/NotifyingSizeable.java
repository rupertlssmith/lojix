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

import com.thesett.common.util.Sizeable;

/**
 * NotifyingSizeable combines together the concepts of a {@link Notifying} and {@link com.thesett.common.util.Sizeable}
 * resource. Resources implementing this interface limit their size to a bounded value, and provide notification to
 * {@link Signalable} resource whenever their size falls from a higher value to below a threshold amount. There are
 * actually two threshold values; a high water value and a low water value. Whenever the size falls below the high water
 * mark from a higher amount, one waiting thread should be signalled. Whenever the size falls below the low water mark
 * from a higher amount, all waiting threads should be signalled.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Signal one waiting thread when size falls below a high water value.
 * <tr><td>Signal all waiting threads when size falls below a low water value.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface NotifyingSizeable extends Notifying, Sizeable
{
    /**
     * Sets the high water threshold for size notifications to wake up one thread.
     *
     * @param size The high water threshold for size notifications to wake up one thread.
     */
    public void setHighWaterThreshold(long size);

    /**
     * Sets the low water threshold for size notifications to wake up all threads.
     *
     * @param size The low water threshold for size notifications to wake up all threads.
     */
    public void setLowWaterThreshold(long size);
}
