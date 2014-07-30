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
 * BinaryPredicate is a binary predicate on objects. Objects can be thought of as members of the set defined by their
 * class. The binary predicate is a function mapping from the cross product of two class sets to the set {true, false}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Calculate membership of two objects to a predicate.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface BinaryPredicate<T, S>
{
    /**
     * Evaluates a logical predicate.
     *
     * @param  t The first candidate object for membership of the relation on two objects.
     * @param  s The second candidate object for membership of the relation on two objects.
     *
     * @return <tt>true</tt> if the two objects are memebers of a binary relation, false otherwise.
     */
    public boolean evaluate(T t, S s);
}
