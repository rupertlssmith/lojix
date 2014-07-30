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
package com.thesett.aima.search.util;

import com.thesett.aima.search.Operator;

/**
 * OperatorImpl provides a simple implementation of the {@link Operator} interface that allows any object type to be
 * encapsulated as an operator. The equality and hashCode methods of the operator delegate to the encapsulated object.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encapsulate an object as an operator.
 * <tr><td> Delegate hashCode and equals to the encapsulated object.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class OperatorImpl<O> implements Operator<O>
{
    /** Holds the encapsulated object operator. */
    private O op;

    /**
     * Encapsulates an object as an operator. The equality and hashCode methods of the operator delegate to this
     * encapsulated object.
     *
     * @param op The object to encapsulate.
     */
    public OperatorImpl(O op)
    {
        // Keep the operator.
        this.op = op;
    }

    /**
     * Returns the underlying object operator.
     *
     * @return The underlying object operator.
     */
    public O getOp()
    {
        return op;
    }

    /**
     * Calculates a hash code based on the underlying object operator.
     *
     * @return A hash code based on the underlying object operator.
     */
    public int hashCode()
    {
        return op.hashCode();
    }

    /**
     * Determines equality based on the underlying object operator.
     *
     * @param  comp The operator to compare to.
     *
     * @return <tt>true</tt> if the comparator is an operator equal to this one.
     */
    public boolean equals(Object comp)
    {
        return (comp instanceof OperatorImpl) && getOp().equals(((OperatorImpl) comp).getOp());
    }

    /**
     * Runs toString on the underlying object operator.
     *
     * @return A string representation of the operator.
     */
    public String toString()
    {
        return op.toString();
    }
}
