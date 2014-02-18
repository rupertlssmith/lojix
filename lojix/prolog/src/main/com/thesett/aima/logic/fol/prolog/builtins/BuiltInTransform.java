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
package com.thesett.aima.logic.fol.prolog.builtins;

import java.util.HashMap;
import java.util.Map;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorName;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.common.util.Function;
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
public class BuiltInTransform implements Function<Functor, BuiltInFunctor>
{
    /** Holds a mapping from functor names to built-in implementations. */
    private Map<FunctorName, Class<? extends BuiltInFunctor>> builtIns =
        new HashMap<FunctorName, Class<? extends BuiltInFunctor>>();

    /** Used to extract functor names to match. */
    private VariableAndFunctorInterner interner;

    /**
     * Initializes the built-in transformation by population the the table of mappings of functors onto their built-in
     * implementations.
     *
     * @param interner The interner to use to extract functor names to match as built-ins.
     */
    public BuiltInTransform(VariableAndFunctorInterner interner)
    {
        this.interner = interner;

        builtIns.put(new FunctorName("true", 0), True.class);
        builtIns.put(new FunctorName("fail", 0), Fail.class);
        builtIns.put(new FunctorName("!", 0), Cut.class);
        builtIns.put(new FunctorName("=", 2), Unifies.class);
        builtIns.put(new FunctorName("\\=", 2), NonUnifies.class);
        builtIns.put(new FunctorName("is", 2), Is.class);
        builtIns.put(new FunctorName(";", 2), Disjunction.class);
        builtIns.put(new FunctorName("call", 1), Call.class);
        builtIns.put(new FunctorName(">", 2), GreaterThan.class);
        builtIns.put(new FunctorName(">=", 2), GreaterThanOrEqual.class);
        builtIns.put(new FunctorName("<", 2), LessThan.class);
        builtIns.put(new FunctorName("=<", 2), LessThanOrEqual.class);
        builtIns.put(new FunctorName("integer", 1), IntegerCheck.class);
        builtIns.put(new FunctorName("float", 1), FloatCheck.class);
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
    public BuiltInFunctor apply(Functor functor)
    {
        FunctorName functorName = interner.getFunctorFunctorName(functor);

        Class<? extends BuiltInFunctor> builtInClass;

        if (builtIns.containsKey(functorName))
        {
            builtInClass = builtIns.get(functorName);
        }
        else
        {
            builtInClass = DefaultBuiltIn.class;
        }

        return ReflectionUtils.newInstance(ReflectionUtils.getConstructor(builtInClass, new Class[] { Functor.class }),
            new Object[] { functor });
    }
}
