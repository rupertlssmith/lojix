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
package com.thesett.common.util;

/**
 * Sizeable allows sizes of objects to be reported for memory usage estimations. Java provides no sizeof operation
 * (other than through the profiling interface), but in many cases reasonable estimates can be provided. Sizing
 * operations may be needed, for example, in a program that decides to shift its data out to disk when it has too much
 * to hold in memory.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Report the estimated byte size of an object.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Sizeable
{
    /**
     * Calculates the size of this object in bytes.
     *
     * @return The size of this object in bytes.
     */
    long sizeof();
}
