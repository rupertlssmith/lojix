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

import com.thesett.common.util.SequenceIterator;

/**
 * Cons is a list conjoining functor. It always takes two argument, the next list element and the remainder of this
 * list. This implementation exists purely for the sake of providing a different pretty printing method than standard
 * functors.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Pretty print a list. <td> {@link Term}.
 * <tr><td> Provide a standard Java iterator over the recursive list.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Cons extends RecursiveList
{
    /**
     * Creates a cons functor. Two arguments must be specified.
     *
     * @param name      The interned name of the cons functor.
     * @param arguments The arguments; there must be two.
     */
    public Cons(int name, Term[] arguments)
    {
        super(name, arguments);

        if (arguments.length != 2)
        {
            throw new IllegalArgumentException("Cons must always take 2 arguments.");
        }
    }

    /**
     * Reports whether this list is the empty list 'nil'.
     *
     * @return <tt>true</tt> if this is the empty list 'nil'.
     */
    public boolean isNil()
    {
        return false;
    }

    /**
     * Provides a Java iterator over this recursively defined list.
     *
     * @return A Java iterator over this recursively defined list.
     */
    public Iterator<Term> iterator()
    {
        return new ListIterator();
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        return listToString(interner, true, printVarName, printBindings);
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return arguments[0].toString() + " :: [ " + arguments[1].toString() + " ]";
    }

    /**
     * Pretty prints a term relative to the symbol namings provided by the specified interner.
     *
     * @param  interner      The interner use to provide symbol names.
     * @param  isFirst       <tt>true</tt> if this is the first cons in a list, <tt>false</tt> otherwise.
     * @param  printVarName  <tt>true</tt> if the names of bound variables should be printed, <tt>false</tt> if just the
     *                       binding without the variable name should be printed.
     * @param  printBindings <tt>true</tt> if variable binding values should be printed, <tt>false</tt> if just the
     *                       variables name without any binding should be printed.
     *
     * @return A pretty printed string containing the term.
     */
    private String listToString(VariableAndFunctorInterner interner, boolean isFirst, boolean printVarName,
        boolean printBindings)
    {
        String result = "";

        if (isFirst)
        {
            result += "[";
        }

        result += arguments[0].toString(interner, printVarName, printBindings);

        Term consArgument = arguments[1].getValue();

        if (consArgument instanceof Cons)
        {
            result += ", " + ((Cons) consArgument).listToString(interner, false, printVarName, printBindings);
        }

        if (isFirst)
        {
            result += "]";
        }

        return result;
    }

    /**
     * ListIterator provides a standard Java Iterator over a recursively defined list. This iterator follows a chain of
     * 'Cons' operators until there are no more.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th> Responsibilities <th> Collaborations
     * <tr><td> Provide a standard Java iterator over the recursive list. <td> {@link Cons}.
     * </table></pre>
     */
    protected class ListIterator extends SequenceIterator<Term>
    {
        /** Holds the next Cons operator to examine, initially set to the head of the list. */
        Cons nextListElement = Cons.this;

        /**
         * Generates the next element in the sequence. Updates the 'nextElement' field to point to the next 'cons'
         * operator in the list, if there is one.
         *
         * @return The next element from the sequence if one is available, or <tt>null</tt> if the sequence is complete.
         */
        public Term nextInSequence()
        {
            Term value = null;

            if (nextListElement != null)
            {
                value = nextListElement.getArgument(0);

                Term candidateCons = nextListElement.getArgument(1);

                if (candidateCons instanceof Cons)
                {
                    nextListElement = (Cons) candidateCons;
                }
                else
                {
                    nextListElement = null;
                }
            }

            return value;
        }
    }
}
