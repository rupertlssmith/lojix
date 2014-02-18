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
 * A filterator, filters an iterator to produce another iterator by applying a mapping function onto the elements of the
 * source iterator. If the mapping function returns <tt>null</tt> for any element of the source iterator, it is filtered
 * out and not included in the generated iterator. The generated iterator consists of the elements in the source
 * iterator mapped by the mapping function.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilties<th>Collaborations
 * <tr><td>Map and filter over an iterator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Filterator<S, T> extends SequenceIterator<T> implements Iterable<T>
{
    /** Holds the source iterator to be filtered. */
    Iterator<S> source;

    /** Holds the mapping and filtering functions. */
    Function<S, T> mapping;

    /**
     * Creates a filterator over the specifed iterator, using the given mapping function.
     *
     * @param source  The source iterator to map and filter.
     * @param mapping The mapping and filtering function.
     */
    public Filterator(Iterator<S> source, Function<S, T> mapping)
    {
        this.source = source;
        this.mapping = mapping;
    }

    /**
     * Generates the next element in the sequence.
     *
     * @return The next element from the sequence if one is available, or <tt>null</tt> if the sequence is complete.
     */
    public T nextInSequence()
    {
        T result = null;

        // Loop until a filtered element is found, or the source iterator is exhausted.
        while (source.hasNext())
        {
            S next = source.next();
            result = mapping.apply(next);

            if (result != null)
            {
                break;
            }
        }

        return result;
    }

    /**
     * Presents this filterator as an iterable.
     *
     * @return This filterator as an iterable.
     */
    public Iterator<T> iterator()
    {
        return this;
    }
}
