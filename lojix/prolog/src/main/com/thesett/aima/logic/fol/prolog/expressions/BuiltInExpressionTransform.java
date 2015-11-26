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
package com.thesett.aima.logic.fol.prolog.expressions;

import java.util.HashMap;
import java.util.Map;

import com.thesett.aima.logic.fol.BaseTermTransformer;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorName;
import com.thesett.aima.logic.fol.FunctorTransformer;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.prolog.builtins.BuiltInFunctor;
import com.thesett.common.util.ReflectionUtils;

/**
 * BuiltInTransform implements a compilation transformation over term syntax trees, that substitutes for functors that
 * map onto Prolog built-ins, an extension of the functor type that implements the built-in.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Transform functors to built in functors where appropriate.
 *     <td> {@link BuiltInFunctor}, {@link VariableAndFunctorInterner}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BuiltInExpressionTransform extends BaseTermTransformer implements FunctorTransformer
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(BuiltInExpressionTransform.class.getName()); */

    /** Holds a mapping from functor name to built-in expression implementations. */
    private final Map<FunctorName, Class<? extends BuiltInExpressionOperator>> builtInExpressions =
        new HashMap<FunctorName, Class<? extends BuiltInExpressionOperator>>();

    /** Used to extract functor names to match. */
    private final VariableAndFunctorInterner interner;

    /**
     * Initializes the built-in transformation by population the the table of mappings of functors onto their built-in
     * implementations.
     *
     * @param interner The interner to use to extract functor names to match as built-ins.
     */
    public BuiltInExpressionTransform(VariableAndFunctorInterner interner)
    {
        this.interner = interner;

        builtInExpressions.put(new FunctorName("**", 2), Exponential.class);
        builtInExpressions.put(new FunctorName("-", 2), Minus.class);
        builtInExpressions.put(new FunctorName("+", 2), Plus.class);
        builtInExpressions.put(new FunctorName("*", 2), Multiply.class);
        builtInExpressions.put(new FunctorName("/", 2), Divide.class);
        builtInExpressions.put(new FunctorName("-", 1), UMinus.class);
    }

    /**
     * Applies a built-in replacement transformation to functors. If the functor matches built-in, a
     * {@link BuiltInFunctor} is created with a mapping to the functors built-in implementation, and the functors
     * arguments are copied into this new functor. If the functor does not match a built-in, it is returned unmodified.
     *
     * @param  functor The functor to attempt to map onto a built-in.
     *
     * @return The functor umodified, or a {@link BuiltInFunctor} replacement for it.
     */
    public Functor transform(Functor functor)
    {
        FunctorName functorName = interner.getFunctorFunctorName(functor);

        if (builtInExpressions.containsKey(functorName))
        {
            Class<? extends Functor> builtInExpressionClass = builtInExpressions.get(functorName);

            return ReflectionUtils.newInstance(ReflectionUtils.getConstructor(builtInExpressionClass,
                    new Class[] { Integer.TYPE, Term[].class }),
                new Object[] { functor.getName(), functor.getArguments() });
        }
        else
        {
            return functor;
        }
    }
}
