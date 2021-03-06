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
package com.thesett.common.util.logic;

/**
 * UnaryPredicate is a unary predicate on objects. The object can be thought of as a member of the set defined by its
 * class. The unary predicate is a function mapping from this set to the set { true, false }.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Determine membership of a predicate
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface UnaryPredicate<T>
{
    /**
     * Evaluates a logical predicate.
     *
     * @param  t The object to test for predicate membership.
     *
     * @return <tt>true</tt> if the object is a member of the predicate, <tt>false</tt> otherwise.
     */
    boolean evaluate(T t);
}
