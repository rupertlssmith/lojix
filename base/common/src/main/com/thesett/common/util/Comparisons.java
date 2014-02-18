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

import java.util.Iterator;

/**
 * Comparisons provides helper methods to compare collections of things. Intended for writing tests, hence the use of
 * error messages as return values, rather than booleans or lists or iterators of differences.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Replace error messages with lists of differences, and provide a seperate test utility for converting these
 *         into error messages. Probably involves using a Pair implementation to return pairs of differences.
 */
public class Comparisons
{
    /**
     * Walks down two iterators, comparing them element by element, using the equals method.
     *
     * @param  iterator         The first iterator to compare.
     * @param  expectedIterator The second iterator to compare.
     * @param  mapping          A mapping between the types of the iterators.
     * @param  <U>              The type of the left iterator.
     * @param  <T>              The type of the right iterator.
     *
     * @return Any mismatches encountered in a string, or the empty string if none found.
     */
    public static <T, U> String compareIterators(Iterator<U> iterator, Iterator<T> expectedIterator,
        Function<U, T> mapping)
    {
        String errorMessage = "";

        while (iterator.hasNext())
        {
            U next = iterator.next();
            T nextMapped = mapping.apply(next);
            T nextExpected = expectedIterator.next();

            if (!nextMapped.equals(nextExpected))
            {
                errorMessage += "Expecting " + nextExpected + " but got " + nextMapped;
            }
        }

        return errorMessage;
    }
}
