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

import com.thesett.common.util.Queue;

/**
 * This BlockingQueue interface is identical to java.util.concurrent.BlockingQueue but also exposes a BlockingQueue as a
 * BlockingSink and a BlockingSource. This is very usefull when only the input or output end of a BlockingQueue is to be
 * exposed by an API; it means that the full implementation of the BlockingQueue can remain hidden.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Expose a queue as a sink.
 * <tr><td>Expose a queue as a source.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface BlockingQueue<E> extends Queue<E>, BlockingSource<E>, BlockingSink<E>
{
}
