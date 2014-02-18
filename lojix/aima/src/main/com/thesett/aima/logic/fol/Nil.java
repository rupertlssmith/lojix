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
package com.thesett.aima.logic.fol;

import java.util.Iterator;

import com.thesett.common.util.EmptyIterator;

/**
 * Nil is the empty recursive list.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide an empty iterator over the empty list.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Nil extends RecursiveList
{
    /**
     * Creates a list functor with the specified interned name and arguments.
     *
     * @param name      The functors interned name.
     * @param arguments The functors arguments.
     */
    public Nil(int name, Term[] arguments)
    {
        super(name, arguments);
    }

    /**
     * Provides a Java iterator over this recursively defined list.
     *
     * @return A Java iterator over this recursively defined list.
     */
    public Iterator<Term> iterator()
    {
        return new EmptyIterator<Term>();
    }

    /**
     * Reports whether this list is the empty list 'nil'.
     *
     * @return <tt>true</tt> if this is the empty list 'nil'.
     */
    public boolean isNil()
    {
        return true;
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        return "[]";
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return "[]";
    }
}
