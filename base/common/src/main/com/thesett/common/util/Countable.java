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
 * Countable represents a data structure or other container of things, which provides a count of the number of things
 * that it holds.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide a count of the number of contained elements.
 * <tr><td>Provide a check for the case where there are no contained elements.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Countable
{
    /**
     * Provides a count of the number of contained elements.
     *
     * @return A count of the number of contained elements.
     */
    int size();

    /**
     * Checks if there are no contained elements.
     *
     * @return <tt>true</tt> if there are no contained elements.
     */
    boolean isEmpty();
}
