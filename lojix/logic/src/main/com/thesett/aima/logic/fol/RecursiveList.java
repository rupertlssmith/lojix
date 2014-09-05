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
package com.thesett.aima.logic.fol;

import java.util.Iterator;

/**
 * List is a base class for defining recursive lists. A list is either the empty list 'nil', or a list made up of
 * 'cons'ing an element onto another list. The subtypes 'cons' and 'nil' are both lists and this abstract base class
 * defines the functionality expected of a list.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a Java iterator over a recursive list.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class RecursiveList extends Functor implements Iterable<Term>
{
    /**
     * Creates a list functor with the specified interned name and arguments.
     *
     * @param name      The functors interned name.
     * @param arguments The functors arguments.
     */
    public RecursiveList(int name, Term[] arguments)
    {
        super(name, arguments);
    }

    /**
     * Provides a Java iterator over this recursively defined list.
     *
     * @return A Java iterator over this recursively defined list.
     */
    public abstract Iterator<Term> iterator();

    /**
     * Reports whether this list is the empty list 'nil'.
     *
     * @return <tt>true</tt> if this is the empty list 'nil'.
     */
    public abstract boolean isNil();
}
