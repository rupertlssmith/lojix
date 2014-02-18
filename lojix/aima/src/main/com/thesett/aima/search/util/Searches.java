/*
 * Copyright The Sett Ltd.
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
package com.thesett.aima.search.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.thesett.aima.search.QueueBasedSearchMethod;
import com.thesett.aima.search.SearchMethod;
import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Traversable;
import com.thesett.common.util.SequenceIterator;

/**
 * Searches provides static helper methods to simplify common operations using search methods.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide iterator over all solutions to a search space. <td> {@link QueueBasedSearchMethod}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Searches
{
    /**
     * Provides an iterator over a search method, that returns successive search solutions on demand.
     *
     * @param  <T>    The traversable state type of the search.
     * @param  method The search method to iterate over.
     *
     * @return An iterator over all solution states to the search.
     */
    public static <T> Iterator<T> allSolutions(SearchMethod<T> method)
    {
        //return new Filterator<SearchNode<O, T>, T>(allSolutionNodes(method), new ExtractSearchNode<O, T>());

        // Take a final reference to the search method to use from within the inner class.
        final SearchMethod<T> search = method;

        return new SequenceIterator<T>()
            {
                /**
                 * Generates the next element in the search.
                 *
                 * @return The next solution from the search if one is available, or <tt>null</tt> if the search is
                 *         complete.
                 */
                public T nextInSequence()
                {
                    try
                    {
                        return search.search();
                    }
                    catch (SearchNotExhaustiveException e)
                    {
                        // SearchNotExhaustiveException means that the search has completed within its designed parameters
                        // without exhausting the search space. Consequently there are no more solutions to find,
                        // the exception can be ignored and the sequence correctly terminated.
                        e = null;

                        return null;
                    }
                }
            };
    }

    /**
     * Finds the set of all goals of a search.
     *
     * @param  <T>    The traversable state type of the search.
     * @param  method The search method to find all goals of.
     *
     * @return A set of all goals found.
     */
    public static <T> Set<T> setOf(SearchMethod<T> method)
    {
        Set<T> result = new HashSet<T>();
        findAll(result, method);

        return result;
    }

    /**
     * Finds a bag of all goals of a search.
     *
     * @param  <T>    The traversable state type of the search.
     * @param  method The search method to find all goals of.
     *
     * @return A bag of all goals found.
     */
    public static <T> Collection<T> bagOf(SearchMethod<T> method)
    {
        Collection<T> result = new ArrayList<T>();
        findAll(result, method);

        return result;
    }

    /**
     * Provides an iterator over a search method, that returns successive search solutions on demand.
     *
     * @param  <O>    The operator type of the search.
     * @param  <T>    The traversable state type of the search.
     * @param  method The search method to iterate over.
     *
     * @return An iterator over all solutions to the search.
     */
    public static <O, T extends Traversable<O>> Iterator<SearchNode<O, T>> allSolutionPaths(
        QueueBasedSearchMethod<O, T> method)
    {
        // Take a final reference to the search method to use from within the inner class.
        final QueueBasedSearchMethod<O, T> search = method;

        return new SequenceIterator<SearchNode<O, T>>()
            {
                /**
                 * Generates the next element in the search.
                 *
                 * @return The next solution from the search if one is available, or <tt>null</tt> if the search is
                 *         complete.
                 */
                public SearchNode<O, T> nextInSequence()
                {
                    try
                    {
                        return search.findGoalPath();
                    }
                    catch (SearchNotExhaustiveException e)
                    {
                        // SearchNotExhaustiveException means that the search has completed within its designed parameters
                        // without exhausting the search space. Consequently there are no more solutions to find,
                        // the exception can be ignored and the sequence correctly terminated.
                        e = null;

                        return null;
                    }
                }
            };
    }

    /**
     * Finals all solutions to a search and inserts them into the specified collection.
     *
     * @param <T>    The traversable state type of the search.
     * @param result The collection to build up the results in.
     * @param method The search to run.
     */
    private static <T> void findAll(Collection<T> result, SearchMethod<T> method)
    {
        for (Iterator<T> i = allSolutions(method); i.hasNext();)
        {
            T nextSoltn = i.next();

            result.add(nextSoltn);
        }
    }
}
