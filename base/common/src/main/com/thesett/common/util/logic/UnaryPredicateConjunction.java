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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * UnaryPredicateConjunction is a chain of unary predicates that are applied in the sequence order in which they are
 * added to the chain, to produce a new unary predicate which is the conjunction of all of its components.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Accept unary predicates to build up a predicate chain.
 * <tr><td>Allow a predicate to be removed from the chain.
 * <tr><td>Allow the whole chain to be cleared.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UnaryPredicateConjunction<T> implements UnaryPredicate<T>
{
    /** Holds the predicate chain. */
    Collection<UnaryPredicate<T>> chain = new LinkedList<UnaryPredicate<T>>();

    /**
     * Adds a new predicate to the chain.
     *
     * @param predicate A new predicate to add to the chain.
     */
    public void addPredicate(UnaryPredicate<T> predicate)
    {
        chain.add(predicate);
    }

    /**
     * Removes a predicate from the chain.
     *
     * @param predicate The predicate to remove.
     */
    public void removePredicate(UnaryPredicate<T> predicate)
    {
        chain.remove(predicate);
    }

    /** Removes all predicates from the chain. */
    public void clear()
    {
        chain.clear();
    }

    /**
     * Evaluates a logical predicate.
     *
     * @param  t The object to test for predicate membership.
     *
     * @return <tt>true</tt> if the object is a member of the predicate, <tt>false</tt> otherwise.
     */
    public boolean evaluate(T t)
    {
        // Start by assuming that the candidate will be a member of the predicate.
        boolean passed = true;

        // Loop through all predicates and fail if any one of them does.
        for (UnaryPredicate<T> predicate : chain)
        {
            if (!predicate.evaluate(t))
            {
                passed = false;

                break;
            }
        }

        return passed;
    }
}
