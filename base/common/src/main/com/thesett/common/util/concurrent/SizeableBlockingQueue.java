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
 * SizeableBlockingQueue is a {@link BlockingQueue} that is {@link com.thesett.common.util.Sizeable} and can provide
 * notifications whenever its size reduces enough that there is space on it, through the {@link NotifyingSizeable}
 * interace.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Provide a size bounded concurrent queue that wakes up waiting threads when space is available.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface SizeableBlockingQueue<E> extends BlockingQueue<E>, NotifyingSizeable
{
}
