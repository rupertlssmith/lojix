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
package com.thesett.aima.search;

import java.util.Iterator;

import com.thesett.aima.state.ComponentType;
import com.thesett.aima.state.State;
import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.Filterator;
import com.thesett.common.util.Function;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * TraversibleState is a base class for building traversible state spaces. It provides a general implementation of the
 * {@link #successors} method to calculate all successors of a traversible state by applying all the operators that are
 * valid in that state to generate the successor states.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Calculate all successor states <td> {@link Successor}
 * <tr><td> Provide a default goal predicate on the state space.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class TraversableState<O> implements State, Traversable<O>
{
    /**
     * Works out all the possible successors by applying all the operators returned by the
     * {@link Traversable#validOperators} method.
     *
     * @param  reverse When set, indicates that the successors should be presented in reverse order.
     *
     * @return An iterator over all the {@link Successor}s that can be reached by applying valid operators to this
     *         state.
     */
    public Iterator<Successor<O>> successors(boolean reverse)
    {
        return new Filterator<Operator<O>, Successor<O>>(validOperators(reverse),
            new Function<Operator<O>, Successor<O>>()
            {
                public Successor<O> apply(Operator operator)
                {
                    return new Successor<O>(getChildStateForOperator(operator), operator, costOf(operator));
                }
            });
    }

    /**
     * Implementations may override this to provide different default goal predicates over the search space. If no
     * default is to be provided, <tt>null</tt> may be returned.
     *
     * @return The default goal predicate, or <tt>null</tt> if no default is provided. This implementation returns <tt>
     *         null</tt>.
     */
    public UnaryPredicate getDefaultGoalPredicate()
    {
        if (this instanceof GoalState)
        {
            return new DefaultGoalPredicate();
        }
        else
        {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Method not implemented. Should create wrapped bean state on demand.
     */
    public Object getProperty(String property)
    {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Method not implemented. Should create wrapped bean state on demand.
     */
    public void setProperty(String name, Object value)
    {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Method not implemented. Should create wrapped bean state on demand.
     */
    public boolean hasProperty(String property)
    {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Method not implemented. Should create wrapped bean state on demand.
     */
    public ComponentType getComponentType()
    {
        throw new NotImplementedException();
    }

    /**
     * The default goal predicate. This predicate can be applied to {@link GoalState}s, that define a default goal for a
     * state space.
     */
    public static class DefaultGoalPredicate implements UnaryPredicate<GoalState>
    {
        /**
         * Evaluates a logical predicate.
         *
         * @param  goalState The object to test for predicate membership.
         *
         * @return <tt>true</tt> if the object is a member of the predicate, <tt>false</tt> otherwise.
         */
        public boolean evaluate(GoalState goalState)
        {
            return goalState.isGoal();
        }
    }
}
