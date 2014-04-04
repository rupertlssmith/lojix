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
package com.thesett.aima.logic.fol.wam;

import com.thesett.aima.logic.fol.OpSymbol;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.TermVisitor;
import com.thesett.aima.logic.fol.compiler.DefaultTraverser;
import com.thesett.aima.logic.fol.compiler.TermWalker;
import com.thesett.aima.search.util.backtracking.DepthFirstBacktrackingSearch;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * TermWalkers is a helper class that supplies various types of searched over {@link Term} abstract syntax trees.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a walkers over term trees. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TermWalkers
{
    /** Predicate matching conjunction and disjunction operators. */
    public static final UnaryPredicate<Term> CONJ_DISJ_OP_SYMBOL_PREDICATE =
        new UnaryPredicate<Term>()
        {
            public boolean evaluate(Term term)
            {
                return (term instanceof OpSymbol) &&
                    (((OpSymbol) term).getTextName().equals(";") || ((OpSymbol) term).getTextName().equals(","));
            }
        };

    /**
     * Provides a simple depth first walk over a term.
     *
     * @param  visitor The visitor to apply to each term.
     *
     * @return A simple depth first walk over a term.
     */
    public static TermWalker simpleWalker(TermVisitor visitor)
    {
        DepthFirstBacktrackingSearch<Term, Term> search = new DepthFirstBacktrackingSearch<Term, Term>();
        TermWalker walker = new TermWalker(search, new DefaultTraverser(), visitor);

        return walker;
    }

    /**
     * Provides a depth first walk over a term, visiting only when a goal predicate matches.
     *
     * @param  visitor The visitor to apply to each term.
     *
     * @return A depth first walk over a term, visiting only when a goal predicate matches.
     */
    public static TermWalker goalWalker(UnaryPredicate<Term> unaryPredicate, TermVisitor visitor)
    {
        TermWalker walker = simpleWalker(visitor);
        walker.setGoalPredicate(unaryPredicate);

        return walker;
    }

    /**
     * Provides a walk over a term, that finds all conjunction and disjunction operators.
     *
     * @param  visitor The visitor to apply to each term.
     *
     * @return A walk over a term, that finds all conjunction and disjunction operators.
     */
    public static TermWalker conjunctionAndDisjunctionOpSymbolWalker(TermVisitor visitor)
    {
        return goalWalker(CONJ_DISJ_OP_SYMBOL_PREDICATE, visitor);
    }
}
