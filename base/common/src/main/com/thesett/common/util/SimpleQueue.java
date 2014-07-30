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
package com.thesett.common.util;

/**
 * SimpleQueue exposes Sink and a Source interface but does not extend java.util.Queue. This provides a minimalist
 * interface onto a queue, with peek, poll and offer methods but nothing else that java.util.Queue requires.
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
public interface SimpleQueue<E> extends Sink<E>, Source<E>
{
}
