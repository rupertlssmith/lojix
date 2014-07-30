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
 * This Queue interface is identical to java.util.Queue but also exposes a Queue as a Sink and a Source. This is very
 * usefull when only the input or output end of a Queue is to be exposed by an API; it means that the full
 * implementation of the Queue can remain hidden.
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
public interface Queue<E> extends java.util.Queue<E>, Sink<E>, Source<E>
{
}
