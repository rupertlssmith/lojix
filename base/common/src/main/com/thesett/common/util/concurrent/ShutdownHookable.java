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
 * Defines an interface that classes which supply shutdown hooks implement. Code that creates these classes can check if
 * they supply a shutdown hook and register these hooks when the obejct are created.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Supply a shutdown hook.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ShutdownHookable
{
    /**
     * Supplies the shutdown hook.
     *
     * @return The shut down hook.
     */
    Thread getShutdownHook();
}
