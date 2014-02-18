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
package com.thesett.aima.logic.fol.isoprologparser;

import java.util.EnumMap;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.OpSymbol;

/**
 * A CandidateOpSymbol is a named symbol in first order logic, that can take be one of several actual symbols, depending
 * on how it is parsed. As such the CandidateOpSymbol class is a convenient place-holder that a parser can create while
 * the exact functor that an operator maps onto remains undecided. For this reason, CandidateOpSymbols are created with
 * their String names, rather than interned integer names. For example, an operator with name '-' could correspond to
 * the constant atom, '-', or to a unary minus functor, '-(X)', or a subtraction operation on two arguments, '-(X, Y)'.
 * Once the exact mapping from an operator to a functor becomes known, its interned name can be generated from its
 * String name and arity.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Associate a string name with possible symbols with the same name. <td> {@link OpSymbol}.
 * <tr><td> Provide a listing of possible symbols organized by fixity. <td> {@link OpSymbol.Fixity}.
 * <tr><td> Report whether a candidate can be pre, post or infix.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CandidateOpSymbol extends Functor
{
    /** Holds the raw text name of this operator. */
    private String name;

    /** Holds the set of possible fixities that this symbol could have, depending on the parsing context. */
    private EnumMap<OpSymbol.Fixity, OpSymbol> possibleOperators;

    /**
     * Creates a candidate operator symbol for a given text name and possible symbols.
     *
     * @param name              The text name of the candidate symbol.
     * @param possibleOperators The possible symbols that the candidate could be.
     */
    public CandidateOpSymbol(String name, EnumMap<OpSymbol.Fixity, OpSymbol> possibleOperators)
    {
        super(-1, null);

        this.name = name;
        this.possibleOperators = possibleOperators;
    }

    /**
     * Provides the raw text name of the symbol.
     *
     * @return The raw text name of the symbol.
     */
    public String getTextName()
    {
        return name;
    }

    /**
     * Provides the possible associativities of this candidate symbol.
     *
     * @return The possible associativities of this candidate symbol.
     */
    public EnumMap<OpSymbol.Fixity, OpSymbol> getPossibleOperators()
    {
        return possibleOperators;
    }

    /**
     * Determines whether this candidate symbol has a prefix form.
     *
     * @return <tt>true</tt> if this candidate symbold has a prefix form.
     */
    public boolean canBePrefix()
    {
        return possibleOperators.containsKey(OpSymbol.Fixity.Pre);
    }

    /**
     * Determines whether this candidate symbol has a postfix form.
     *
     * @return <tt>true</tt> if this candidate symbold has a postfix form.
     */
    public boolean canBePostfix()
    {
        return possibleOperators.containsKey(OpSymbol.Fixity.Post);
    }

    /**
     * Determines whether this candidate symbol has an infix form.
     *
     * @return <tt>true</tt> if this candidate symbold has an infix form.
     */
    public boolean canBeInfix()
    {
        return possibleOperators.containsKey(OpSymbol.Fixity.In);
    }

    /**
     * Prints this candidate operator symbol as a string, mainly for debugging purposes.
     *
     * @return This candidate operator symbol as a string.
     */
    public String toString()
    {
        return "CandidateOpSymbol: [ name = " + name + ", possibleOperators = " + possibleOperators + " ]";
    }
}
