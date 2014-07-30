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
 * MarkerTerm is a special term implementation that is not part of the language of L0, but that does implement the
 * {@link Term} interface. It provides default implementation of all of the methods of term, returning false for all
 * boolean methods, null for all object returning methods, and no-op for all void methods. It provides a convenient base
 * class to extend to produce special mark term implementations, in situations where non logical constructs must be
 * inserted into the abstract syntax tree. For example a functor that has arguments, but that will be filled in at a
 * later time, may use marker terms for the pending arguments.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a special marker extension point in the abstract syntax tree of fol.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Get rid of this and its sub-classes. Not needed any more as the additional fields can be placed into a symbol
 *         table instead.
 */
public class MarkerTerm extends BaseTerm
{
    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable.
     *
     * @return The term itself, or the assigned value for variables.
     */
    public Term getValue()
    {
        return null;
    }

    /** Frees all assigned variables in the term, leaving them unnassigned. */
    public void free()
    {
    }
}
