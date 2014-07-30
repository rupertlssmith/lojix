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

/**
 * SubclassFilterator is a {@link Filterator} that filters the elements of a source iterator, based on whether or not
 * they are instances of a given class. Any which are instances are cast to that class, and any which are not instances
 * are exlcuded from the filter by returning <tt>null</tt>. This filter is usefull for filtering collections over a
 * super-type, when just one particular sub-type is to be extracted from that collection.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Filter super types to a particular sub-type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SubclassFilterator<S, T> extends Filterator<S, T>
{
    /**
     * Creates a subclass filterator over the specified source, casting down to the specified target class.
     *
     * @param sourceIterator The source iterator to filter.
     * @param targetClass    The target class to filter to.
     */
    public SubclassFilterator(Iterator<S> sourceIterator, final Class<T> targetClass)
    {
        super(sourceIterator, new Function<S, T>()
            {
                public T apply(S source)
                {
                    if (targetClass.isInstance(source))
                    {
                        return targetClass.cast(source);
                    }
                    else
                    {
                        return null;
                    }
                }
            });
    }
}
