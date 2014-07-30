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
package com.thesett.aima.logic.fol.prolog.builtins;

import java.util.Queue;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.search.util.backtracking.ReTraversable;
import com.thesett.common.util.Function;
import com.thesett.common.util.SimpleQueue;
import com.thesett.common.util.Sink;
import com.thesett.common.util.TraceIndenter;

/**
 * ResolutionState forms a state corresponding to a step of a proof in a resolution, where each step of the proof
 * corresponds exactly with one goal functor to be proved in that step. New disjunctive choice points will always
 * correspond to new resolution states, as will sequences of conjunctive steps.
 *
 * <p/>This interface is exposed mainly to provide a clean interface to implement {@link BuiltIn}s against the proof
 * search engine. It provides access to the goal stack, the variable bindings, the current clause-as-choice-point, a
 * unifier to perform unifications as required, and a method to create subsequent steps in the proof.
 *
 * <p/>The goal stack and variable binding sink provided by this interface should be self-undoing. That is if a
 * {@link BuiltIn} adds goals or variable bindings to them, but is later required to be back-tracked over, the built in
 * does not have to explicitly undo its additions. The state implementation is clever enough to be able to handle
 * back-tracking on its own.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide a stack of outstanding goals to be proved.
 * <tr><td> Provide a sink to insert new variable bindings onto.
 * <tr><td> Provide the current query choice-point being resolved over.
 * <tr><td> Provide a unifier to perform unifications with.
 * <tr><td> Provide an interner to translate interned symbols with.
 * <tr><td> Create a new resolution state as a choice point for a clause.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ResolutionState extends ReTraversable<ResolutionState>
{
    /**
     * Adds new resolution states as choices or a single new state to be resolved in the resolution search, as a child
     * state of this one.
     *
     * <p/>If the goal resolves against the domain, all possible matching clauses are taken with it to form a set of
     * states to choose amongst as continution states from this one. If the goal is a built-in, a single state to
     * evaluate it as a continuation frmo this state is created. Multiple calls to this method will add more states to
     * the choice set.
     *
     * @param goal The functor to create continuation states for.
     */
    public void createContinuationStatesForGoal(BuiltInFunctor goal);

    /**
     * Provides a simple interface onto the goal stack. This may be used to peek at the current goal, and remove or
     * insert a goal onto the stack.
     *
     * @return The current goal stack.
     */
    public SimpleQueue<BuiltInFunctor> getGoalStack();

    /**
     * Provides the most recent state that acted as a choice point that led to this state.
     *
     * @return The most recent state that acted as a choice point that led to this state.
     */
    public ResolutionState getLastChoicePoint();

    /**
     * Marks this state as 'cut', which means that it is effectively removed from the search and will fail.
     *
     * <p/>Note that the cut will be examined when the state becomes active, so if it has already become active then it
     * will be too late for the cut to have any effect.
     */
    public void cut();

    /**
     * Provides the choice point states that this state leads to.
     *
     * @return The choice point states that this state leads to.
     */
    public Queue<ResolutionState> getChoicePoints();

    /**
     * Provides a sink onto the variable binding stack. This may be used to add variable bindings.
     *
     * @return A sink onto the variable binding stack.
     */
    public Sink<Variable> getVariableBindings();

    /**
     * Provides the current choice point clause that this resolution state is a proof step within.
     *
     * @return The current choice point clause that this resolution state is a proof step within.
     */
    public Clause getCurrentClause();

    /**
     * Provides a function to transform functors into built-ins. When a functor is being created as a goal, this
     * function should be used to map it onto its implementing built-in.
     *
     * @return A function mapping functors to built-ins.
     */
    public Function<Functor, BuiltInFunctor> getBuiltInTransform();

    /**
     * Provides a unifier to perform any required unificiations with.
     *
     * @return The unifier for this state.
     */
    public PrologUnifier getUnifier();

    /**
     * Provides an interner to translate interned symbols with.
     *
     * @return An interner to translate interned symbols with.
     */
    public VariableAndFunctorInterner getInterner();

    /**
     * Provides a trace indenter to create indents for pretty printing execution trace statements with. This indenter
     * will be set up to increase its indent on every choice point, and decrease it on every back-tracking over a choice
     * point. Trace statements will generally not need to apply a delta to the indentation level but should stick with
     * the indentation as choice point depth scheme.
     *
     * @return A trace indenter to assist with pretty printing trace statements.
     */
    public TraceIndenter getTraceIndenter();
}
