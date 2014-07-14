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
package com.thesett.aima.logic.fol.builder;

import com.thesett.aima.logic.fol.FloatLiteral;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.IntLiteral;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;

/**
 * TermBuilder is a helper class for constructing Prolog terms.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TermBuilder
{
    /** The interner used to intern all names. */
    private final VariableAndFunctorInterner interner;

    /**
     * Creates a term build using the supplied interner to intern functor and variable names.
     *
     * @param interner The interner to use.
     */
    public TermBuilder(VariableAndFunctorInterner interner)
    {
        this.interner = interner;
    }

    /**
     * Creates a source code position to be applied to the next term created.
     *
     * @param startLine   The start line position.
     * @param startColumn The start column position.
     * @param endLine     The end line position.
     * @param endColumn   The end column position.
     */
    public TermBuilder atPosition(int startLine, int startColumn, int endLine, int endColumn)
    {
        throw new UnsupportedOperationException("'atPosition' is not implemented yet.");
    }

    /**
     * Creates a functor.
     *
     * @param  name The name of the functor.
     * @param  args The functors arguments.
     *
     * @return A functor.
     */
    public Functor functor(String name, Term... args)
    {
        int internedName = interner.internFunctorName(name, args.length);

        return new Functor(internedName, args);
    }

    /**
     * Creates an atom (functor with no arguments).
     *
     * @param  name The name of the atom.
     *
     * @return An atom.
     */
    public Functor atom(String name)
    {
        int internedName = interner.internFunctorName(name, 0);

        return new Functor(internedName, null);
    }

    /**
     * Creates a variable. If the variable name begins with an underscore "_", it will be anonymous, otherwise it will
     * be named.
     *
     * @param  name The name of the variable.
     *
     * @return A variable.
     */
    public Variable var(String name)
    {
        boolean isAnonymous = name.startsWith("_");
        int internedName = interner.internVariableName(name);

        return new Variable(internedName, null, isAnonymous);
    }

    /**
     * Creates an integer.
     *
     * @param  value The value.
     *
     * @return An integer.
     */
    public IntLiteral integer(int value)
    {
        return new IntLiteral(value);
    }

    /**
     * Creates a floating point value.
     *
     * @param  value The value.
     *
     * @return A floating point number.
     */
    public FloatLiteral real(float value)
    {
        return new FloatLiteral(value);
    }
}
