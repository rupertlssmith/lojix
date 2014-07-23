/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
package com.thesett.aima.logic.fol.jpc.salt;

import java.util.List;
import java.util.logging.Logger;

import org.jpc.salt.TermBuilder;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;

public class LojixTermBuilder extends TermBuilder<Term>
{
    /** The interner used to look up and construct all names. */
    private final VariableAndFunctorInterner interner;

    /**
     * Creates a term builder with the specified interner to look up and construct interned names with.
     *
     * @param interner The interner used to look up and construct all names.
     */
    public LojixTermBuilder(VariableAndFunctorInterner interner)
    {
        this.interner = interner;
    }

    /** {@inheritDoc} */
    public Term build()
    {
        Term result;

        if (!isCompound())
        {
            result = getFunctor();
        }
        else
        {
            if (getFunctor().isAtom())
            {
                List<Term> args = getArgs();
                int arity = args.size();
                Functor functor = (Functor) getFunctor();
                int name = interner.internFunctorName(interner.getFunctorName(functor.getName()), arity);
                result = new Functor(name, args.toArray(new Term[arity]));
            }
            else
            {
                throw new RuntimeException("Invalid functor type.");
            }
        }

        return result;
    }
}
