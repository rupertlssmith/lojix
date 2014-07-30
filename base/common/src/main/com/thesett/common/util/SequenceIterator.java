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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * SequenceIterator is a helper class for writing iterators over lazily generated sequences. This follows a common
 * pattern, different from an iterator over a collection of a known size. This is because the next element in the
 * sequence must be generated before knowing whether or not there is next element. The pattern that this base class
 * follows is to generate and cache the next element when the {@link #hasNext()} method is called, and to consume and
 * return the cached element when the {@link #next()} method is called. The {@link #next()} method will generate the
 * next element in the sequence, if it has not already been created and cached.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Turn a lazily generated sequence into an iterator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class SequenceIterator<E> implements Iterator<E>
{
    /** Caches the next lazily generated solution, when it has already been asked for by {@link #hasNext}. */
    private E nextSolution = null;

    /** Used to indicate that the sequence has been exhausted. */
    private boolean searchExhausted = false;

    /**
     * Generates the next element in the sequence.
     *
     * @return The next element from the sequence if one is available, or <tt>null</tt> if the sequence is complete.
     */
    public abstract E nextInSequence();

    /**
     * Checks if a sequnce has more elements, caching any generated as a result of the check.
     *
     * @return <tt>true</tt> if there are more elements, <tt>false</tt> if not.
     */
    public boolean hasNext()
    {
        boolean hasNext;

        try
        {
            nextInternal();
            hasNext = true;
        }
        catch (NoSuchElementException e)
        {
            // Exception noted so can be ignored, no such element means no more elements, so 'hasNext' is false.
            e = null;

            hasNext = false;
        }

        return hasNext;
    }

    /**
     * Gets the next element from the sequence if one is available. The difference between this method and
     * {@link #nextInSequence} is that this method consumes any cached solution, so subsequent calls advance onto
     * subsequent solutions.
     *
     * @return The next solution from the search space if one is available.
     *
     * @throws NoSuchElementException If no solutions are available.
     */
    public E next()
    {
        // Consume the next element in the sequence, if one is available.
        E result = nextInternal();
        nextSolution = null;

        return result;
    }

    /**
     * Removes from the underlying collection the last element returned by the iterator (optional operation). This
     * method can be called only once per call to <tt>next</tt>. The behavior of an iterator is unspecified if the
     * underlying collection is modified while the iteration is in progress in any way other than by calling this
     * method.
     *
     * @throws UnsupportedOperationException The <tt>remove</tt> operation is not generally supported by lazy sequences.
     */
    public void remove()
    {
        throw new UnsupportedOperationException("Lazy sequences, in general, do not support removal.");
    }

    /**
     * Gets the next element from the sequence, the cached one if one has already been generated, or creating and
     * caching a new one if not. If the cached element from a previous call has not been consumed, then subsequent calls
     * to this method will not advance the iterator.
     *
     * @return The next solution from the search space if one is available.
     *
     * @throws NoSuchElementException If no solutions are available.
     */
    private E nextInternal()
    {
        // Check if the search space is already known to be empty.
        if (searchExhausted)
        {
            throw new NoSuchElementException("Sequence exhausted.");
        }

        // Check if the next soluation has already been cached, because of a call to hasNext.
        if (nextSolution != null)
        {
            return nextSolution;
        }

        // Otherwise, generate the next solution, if possible.
        nextSolution = nextInSequence();

        // Check if the solution was null, which indicates that the search space is exhausted.
        if (nextSolution == null)
        {
            // Raise a no such element exception to signal the iterator cannot continue.
            throw new NoSuchElementException("Seqeuence exhausted.");
        }
        else
        {
            return nextSolution;
        }
    }
}
