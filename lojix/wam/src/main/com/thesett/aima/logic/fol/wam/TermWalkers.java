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
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TermWalkers
{
    public static final UnaryPredicate<Term> CONJ_DISJ_OP_SYMBOL_PREDICATE =
        new UnaryPredicate<Term>()
        {
            public boolean evaluate(Term term)
            {
                if (term instanceof OpSymbol)
                {
                    OpSymbol opSymbol = (OpSymbol) term;

                    if (opSymbol.getTextName().equals(";"))
                    {
                        return true;
                    }
                    else if (opSymbol.getTextName().equals(","))
                    {
                        return true;
                    }
                }

                return false;
            }
        };

    public static TermWalker simpleWalker(TermVisitor visitor)
    {
        DepthFirstBacktrackingSearch<Term, Term> search = new DepthFirstBacktrackingSearch<Term, Term>();
        TermWalker walker = new TermWalker(search, new DefaultTraverser(), visitor);

        return walker;
    }

    public static TermWalker goalWalker(UnaryPredicate<Term> unaryPredicate, TermVisitor visitor)
    {
        TermWalker walker = simpleWalker(visitor);
        walker.setGoalPredicate(unaryPredicate);

        return walker;
    }

    public static TermWalker conjunctionAndDisjunctionOpSymbolWalker(TermVisitor visitor)
    {
        return goalWalker(CONJ_DISJ_OP_SYMBOL_PREDICATE, visitor);
    }
}
