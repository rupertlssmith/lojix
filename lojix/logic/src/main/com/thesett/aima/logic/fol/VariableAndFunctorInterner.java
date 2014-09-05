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

import com.thesett.aima.attribute.impl.IdAttribute;

/**
 * VariableAndFunctorInterner provides factories to turn {@link Variable}s and {@link FunctorName}s into interned
 * values.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide symbol table for functors names.
 * <tr><td> Provide symbol table for variable names.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface VariableAndFunctorInterner
{
    /**
     * Gets the interning factory for variables in the machine.
     *
     * @return The interning factory for variables in the machine.
     */
    public IdAttribute.IdAttributeFactory<String> getVariableInterner();

    /**
     * Gets the interning factory for functors in the machine.
     *
     * @return The interning factory for functors in the machine.
     */
    public IdAttribute.IdAttributeFactory<FunctorName> getFunctorInterner();

    /**
     * Interns a functor name to an integer id. A functor is uniquely identified by a name and its arity. Two functors
     * with the same name but different arity are actually different functors.
     *
     * @param  name    The textual name of the functor.
     * @param  numArgs The number of arguments that the functor takes.
     *
     * @return An interned id for the functor.
     */
    int internFunctorName(String name, int numArgs);

    /**
     * Interns a functor name to an integer id. A functor is uniquely identified by a name and its arity. Two functors
     * with the same name but different arity are actually different functors.
     *
     * @param  name The name and arity of the functor to intern.
     *
     * @return An interned id for the functor.
     */
    int internFunctorName(FunctorName name);

    /**
     * Interns a variable name to an integer id.
     *
     * @param  name The textual name of the variable.
     *
     * @return An interned id attribute for the variable.
     */
    int internVariableName(String name);

    /**
     * De-interns a variable name as a string from its interned form.
     *
     * @param  name The interned variable name.
     *
     * @return The variable name as a string.
     */
    String getVariableName(int name);

    /**
     * De-interns a variable name as a string from its interned form.
     *
     * @param  variable The variable to get the string name of.
     *
     * @return The variable name as a string.
     */
    String getVariableName(Variable variable);

    /**
     * De-interns a functor name from its interned form.
     *
     * @param  name The interned functor name.
     *
     * @return The de-interned functor names and arity.
     */
    FunctorName getDeinternedFunctorName(int name);

    /**
     * De-internes a functor name from its intered form, and provides just the functors name without its arity.
     *
     * @param  name The interened functor name.
     *
     * @return The de-interned functors name.
     */
    String getFunctorName(int name);

    /**
     * De-internes a functor name from its intered form, and provides just the functors arity without its name.
     *
     * @param  name The interened functor name.
     *
     * @return The de-interned functors arity.
     */
    int getFunctorArity(int name);

    /**
     * De-internes a functor name from its intered form.
     *
     * @param  functor The functor to de-intern.
     *
     * @return The de-interned functor names and arity.
     */
    FunctorName getFunctorFunctorName(Functor functor);

    /**
     * De-internes a functor name from its intered form, and provides just the functors name without its arity.
     *
     * @param  functor The functor to de-intern.
     *
     * @return The de-interned functors name.
     */
    String getFunctorName(Functor functor);

    /**
     * De-internes a functor name from its intered form, and provides just the functors arity without its name.
     *
     * @param  functor The functor to de-intern.
     *
     * @return The de-interned functors arity.
     */
    int getFunctorArity(Functor functor);
}
