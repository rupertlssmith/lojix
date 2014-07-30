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

/**
 * Functors have string names but it is also possible that two functors with the same name can really be different
 * functors, when they have different arities. Identically named functors with different arities are really different
 * functors. For this reason the FunctorName structure exists to hold a functors uniquely identifying name and arity as
 * a compound. These values are interned, and referred to by the surrogate int parameter returned by
 * {@link Functor#getName()}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent a functors uniquely identifying its name and arity.
 * <tr><td> Provide equality checking that only considers functors with identical name and arity to be equal.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FunctorName
{
    /** Holds the functors textual name. */
    protected String name;

    /** Holds the functors arity. */
    protected int arity;

    /**
     * Creates a functor name with the specified name and arity.
     *
     * @param name  The name of the functor.
     * @param arity The arity of the functor.
     */
    public FunctorName(String name, int arity)
    {
        this.name = name;
        this.arity = arity;
    }

    /**
     * Gets the functors name.
     *
     * @return The functors name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the functors arity.
     *
     * @return The functors arity.
     */
    public int getArity()
    {
        return arity;
    }

    /**
     * Checks if this functor name is identical to another. The comparator must also be a functor name with identical
     * name and arity for this to be true.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is a functor name with identical name and arity, <tt>false<tt>otherwise.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        FunctorName that = (FunctorName) o;

        return (arity == that.arity) && !((name != null) ? (!name.equals(that.name)) : (that.name != null));
    }

    /**
     * Returns a hashcode for the functor name based on the name and arity.
     *
     * @return A hashcode that is compatible with the equals method.
     */
    public int hashCode()
    {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (31 * result) + arity;

        return result;
    }

    /**
     * Outputs the functor name as a string, used mainly for debugging purposes.
     *
     * @return The functor name.
     */
    public String toString()
    {
        return "FunctorName: [ name = " + name + ", arity = " + arity + " ]";
    }
}
