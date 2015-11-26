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
package com.thesett.aima.logic.fol.isoprologparser;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.OpSymbol;
import com.thesett.aima.logic.fol.Term;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.util.Queue;
import com.thesett.common.util.StackQueue;

/**
 * DynamicOperatorParser is a 'deferred decision parser' that can be used to parse Prolog with dynamically defined
 * operators. Prolog operators are dynamic; they can be redefined at run-time with altered fixities, associativities and
 * priorities. This means that the grammar of prolog is not fixed and parser generator tools have a hard time coping
 * with it. A solution is to parse sequences of adjacent terms into a flat array, and then to pass this array to this
 * parser to attempt to correctly parse the sequence of terms using the currently defined operators.
 *
 * <p/>This parser maintains the {@link OperatorTable} in an ISO Prolog compliant manner. That is, it does not permit
 * the ',' operator to be redefined, it does not allow an infix and postfix operator with the same name to be
 * simultaneosly defined, priority must be between 0 and 1200 inclusive, setting a priority of 0 on an operator removes
 * it from the table. It is the responsibility of the caller to ensure that operators have valid names.
 *
 * <p/>The grammar to parse looks like:
 *
 * <pre>
 * T := t
 * T := op T
 * T := T op
 * T := T op T
 * </pre>
 *
 * Where t is a term already fully parsed, for example, a functor, an atom, a variable, an operator as an atom. 'op' is
 * an operator, which can be pre, post, or infix by its position in the different rules.
 *
 * <p/>This grammar can be parsed using a simple Shift-Reduce, or LR parser. The translation of the grammar into a
 * parsing table looks like:
 *
 * <p/>Augment the grammar with a start state and number the rules:
 *
 * <pre>
 * 0. S := T
 * 1. T := t
 * 2. T := op T
 * 3. T := T op
 * 4. T := T op T
 * </pre>
 *
 * <p/>Expand the rules into item sets by shifting a '.' down the rules:
 *
 * <p/><b>Item Set 0</b>
 *
 * <pre>
 *   S := . T
 * + T := . t
 * + T := . op T
 * + T := . T op
 * + T := . T op T
 * </pre>
 *
 * <p/><b>Item Set 1</b>
 *
 * <pre>
 *   T := t .
 * </pre>
 *
 * <p/><b>Item Set 2</b>
 *
 * <pre>
 *   T := op . T
 * + T := . t
 * + T := . op T
 * + T := . T op
 * + T := . T op T
 * </pre>
 *
 * <p/><b>Item Set 3</b>
 *
 * <pre>
 *   S := T .
 *   T := T . op
 *   T := T . op T
 * </pre>
 *
 * <p/><b>Item Set 4</b>
 *
 * <pre>
 *   T := op T .
 *   T := T . op
 *   T := T . op T
 * </pre>
 *
 * <p/><b>Item Set 5</b>
 *
 * <pre>
 *   T := T op .
 *   T := T op . T
 * + T := . t
 * + T := . op T
 * + T := . T op
 * + T := . T op T
 * </pre>
 *
 * <p/><b>Item Set 6</b>
 *
 * <pre>
 *   T := T op T .
 *   T := T . op
 *   T := T . op T
 * </pre>
 *
 * <p/>Which yields the transition table between these states of:
 *
 * <pre><p/><table rules="all" border="1"><caption>Transition Table</caption>
 * <tr><th>  <th>  t <th> op <th> T
 * <tr><td> 0<td>  1 <td>  2 <td> 3
 * <tr><td> 1<td>    <td>    <td>
 * <tr><td> 2<td>  1 <td>  2 <td> 4
 * <tr><td> 3<td>    <td>  5 <td>
 * <tr><td> 4<td>    <td>  5 <td>
 * <tr><td> 5<td>  1 <td>  2 <td> 6
 * <tr><td> 6<td>    <td>  5 <td>
 * </table></pre>
 *
 * <p/>Which results in an LR parser table of:
 *
 * <pre><p/><table rules="all" border="1"><caption>LR Parser Table</caption>
 * <tr><th>  <th colspan="3">Action           <th> Goto
 * <tr><th>  <th>  t    <th> op    <th>  $    <th> T
 * <tr><td> 0<td> s1    <td> s2    <td>       <td> 3
 * <tr><td> 1<td> r1    <td> r1    <td> r1    <td>
 * <tr><td> 2<td> s1    <td> s2    <td>       <td> 4
 * <tr><td> 3<td>       <td> s5    <td> acc   <td>
 * <tr><td> 4<td> r2    <td> s5/r2 <td> r2    <td>
 * <tr><td> 5<td> s1/r3 <td> s2/r3 <td> r3    <td> 6
 * <tr><td> 6<td> r4    <td> s5/r4 <td> r4    <td>
 * </table></pre>
 *
 * <p/>From which it can be seen that there are 4 shift-reduce conflicts. It is these conflicts that make this grammar
 * difficult to parse with generator tools. This DynamicOperatorParser uses the operator precedence, fixity and
 * associativity values to decide which way to resolve the conflicts. The 's1/r2' conflict when encountering 't' in
 * state 5 is always shifted as non-operator terms have priority 0 in Prolog. The remaining conflicts are all
 * operator/operator conflicts, and are therefore a matter of deciding which operator takes precedence; the one most
 * recently seen, in which case reduce; the one which is encountered as the next symbol, in which case shift. The
 * operators priority values are compared and the one with the lower value wins. If the priorities are equal, then the
 * associativity of the next operator is used to decide, if it binds left then reduce, binds right then shift, if it is
 * non-associative then the choice is ambigious so an error is reported.
 *
 * <p/>The parser is further complicated by the fact that operators can be overloaded in Prolog. This means that each
 * {@link CandidateOpSymbol} encountered can map onto multiple {@link OpSymbol} definitions and a choice as to which one
 * is used must be made in the context of the position in which is encountered. Consider the following sequences of
 * symbols when and operator/operator conflict is encountered:
 *
 * <pre>
 * alpha OpA beta OpB ...
 * </pre>
 *
 * <p/>Where alpha and beta are sequences of symbols, possibly empty. Now consider for the empty or non-empty values of
 * alpha and beta, what fixities of operator are possible for OpA and OpB. Note that OpA and OpB must be prefix,
 * postfix, or infix and cannot be an constant atom, because operators as constant atoms must be bracketed in ISO
 * Prolog, and will therefore have already been resolved from a {@link CandidateOpSymbol} to an {@link OpSymbol}.
 *
 * <pre><p/><table><caption>Possibly Fixity Combinations</caption>
 * <tr><td> alpha = not_empty, beta = not_empty <td> (OpA = in, OpB = post) or (OpA = in, OpB = in)
 * <tr><td> alpha = empty, beta = not_empty <td> (OpA = pre, OpB = post) or (OpA = in, OpB = in)
 * <tr><td> alpha = not_empty, beta = empty <td> (OpA = post, OpB = in) or (OpA = post, OpB = post) or (OpA = in, OpB = pre)
 * <tr><td> alpha = empty, beta = empty     <td> (OpA = pre, OpB = pre)
 * </table></pre>
 *
 * <p/>ISO Prolog does not permit a postfix and infix operator with the same name to be defined simultaneously, which
 * reduces the above table to only one possible operator combination in each case.
 *
 * <p/>The evaluation of the fixity combinations, and operator precedence to decide conflicts can be seen in the
 * {@link ResolveAction#apply()} method.
 *
 * <p/>Error reporting is added to the LR parser by filling the blank transitions in the parse tables with error actions
 * with custom error messages specific to the empty transition.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Add new operators to the table following ISO Prolog rules.
 * <tr><td> Find all candidate operators matching a given name.
 * <tr><td> Parse sequences of terms and candidate operators using ISO Prolog rules.
 *     <td> {@link Term}, {@link CandidateOpSymbol}, {@link OpSymbol}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DynamicOperatorParser implements OperatorTable
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(DynamicOperatorParser.class.getName()); */

    /** Encodes the possible symbols that this parser accepts. */
    private enum Symbol
    {
        /** A term which is not a {@link CandidateOpSymbol}. It may be a previously resolved {@link OpSymbol} though. */
        Term,

        /** A symbol which is a {@link CandidateOpSymbol} to be resovled onto an {@link OpSymbol}. */
        Op,

        /** A final symbol to complete the sequence. */
        Final
    }

    /** Defines the action to shift to state 1. */
    private final ShiftAction s1 = new ShiftAction(1);

    /** Defines the action to shift to state 2. */
    private final ShiftAction s2 = new ShiftAction(2);

    /** Defines the action to shift to state 5. */
    private final ShiftAction s5 = new ShiftAction(5);

    /** Defines the action to resolve using rule 1. */
    private final ReduceAction r1 = new ReduceAction(1);

    /** Defines the action to resolve using rule 2. */
    private final ReduceAction r2 = new ReduceAction(2);

    /** Defines the action to resolve using rule 3. */
    private final ReduceAction r3 = new ReduceAction(3);

    /** Defines the action to resolve using rule 4. */
    private final ReduceAction r4 = new ReduceAction(4);

    /** Defines an error message when the final symbol is encountered in the start state. */
    private final ErrorAction e1 = new ErrorAction("Term sequence cannot be empty.");

    /** Defines an error message when nothing is specified to apply an operator to. */
    private final ErrorAction e2 = new ErrorAction("Something expected after operator.");

    /** Defines an error message when two adjacement terms no seperated by an operator are encounterd. */
    private final ErrorAction e3 = new ErrorAction("Cannot have two adjacent non-operator terms.");

    /**
     * Holds the LR parser action table, that describes which action to perform when encountering a given symbol in a
     * given state. The first index to the two dimensional array is the state number, the second is the {@link Symbol}s
     * ordinal value. Note that there are three operator/operator shift/reduce conflicts in this table.
     */
    private final Action[][] actionTable =
        new Action[][]
        {
            { s1, s2, e1 },
            { r1, r1, r1 },
            { s1, s2, e2 },
            { e3, s5, new Accept() },
            { r2, new ResolveAction(s5, r2), r2 },
            { s1, new ResolveAction(s2, r3), r3 },
            { r4, new ResolveAction(s5, r4), r4 }
        };

    /** Holds the LR parser goto table, that describes the state to transition to after resolving a rule. */
    private final Integer[] gotoTable = new Integer[] { 3, null, 4, null, null, 6, null };

    /** Holds the rules to apply when resolving. */
    private final Action[] rules = { null, new Rule1(), new Rule2(), new Rule3(), new Rule4() };

    /** Holds the parser state stack. */
    private final Queue<Integer> stack = new StackQueue<Integer>();

    /** Holds the parsers current state. */
    private int state;

    /** Holds the parsers current position within the input sequence of terms. */
    private int position;

    /** Holds the output stack onto which terms are placed pending their consumption by rule reductions. */
    private final Queue<Term> outputStack = new StackQueue<Term>();

    /** Holds the current next term on the input sequence. */
    private Term nextTerm;

    /** Holds the table of defined operators by name and fixity. */
    private final Map<String, EnumMap<OpSymbol.Fixity, OpSymbol>> operators =
        new HashMap<String, EnumMap<OpSymbol.Fixity, OpSymbol>>();

    /**
     * Parses a flat list of terms, which are literals, variables, functors, or operators into a tree in such a way that
     * the operators associativity and precendence is obeyed.
     *
     * @param  terms A flat list of terms possibly containing operators, to be parsed.
     *
     * @return The functor at the root of the sequence of terms parsed into an abstract syntax tree.
     *
     * @throws SourceCodeException If the list of terms does not form a valid syntactical construction under the current
     *                             set of defined operators.
     */
    public Functor parseOperators(Term[] terms) throws SourceCodeException
    {
        // Initialize the parsers state.
        stack.offer(0);
        state = 0;
        position = 0;
        nextTerm = null;

        // Consume the terms from left to right.
        for (position = 0; position <= terms.length;)
        {
            Symbol nextSymbol;

            // Decide what the next symbol to parse is; candidate op, term or final.
            if (position < terms.length)
            {
                nextTerm = terms[position];

                if (nextTerm instanceof CandidateOpSymbol)
                {
                    nextSymbol = Symbol.Op;
                }
                else
                {
                    nextSymbol = Symbol.Term;
                }
            }
            else
            {
                nextSymbol = Symbol.Final;
            }

            // Look in the action table to find the action associated with the current symbol and state.
            Action action = actionTable[state][nextSymbol.ordinal()];

            // Apply the action.
            action.apply();
        }

        return (Functor) outputStack.poll();
    }

    /**
     * Sets the priority and associativity of a named operator in this table. This method may be used to remove
     * operators by some implementations, through a special setting of the priority value. A priority value of zero will
     * remove any existing operator matching the fixity of the one specified (that is pre, or post/infix). To be
     * accepted, the operator must have a priority between 0 and 1200 inclusive, and can only be a postfix operator when
     * an infix is not already defined with the same name, and similarly for infix operators when a postfix operator is
     * already defined.
     *
     * @param name          The interned name of the operator to set in the table.
     * @param textName      The text name of the operator to set in the table.
     * @param priority      The priority of the operator. Zero removes the operator.
     * @param associativity The associativity of the operator.
     */
    public void setOperator(int name, String textName, int priority, OpSymbol.Associativity associativity)
    {
        // Check that the name of the operator is valid.

        // Check that the priority of the operator is valid.
        if ((priority < 0) || (priority > 1200))
        {
            throw new IllegalArgumentException("Operator priority must be between 0 and 1200 inclusive.");
        }

        OpSymbol opSymbol = new OpSymbol(name, textName, associativity, priority);

        // Consult the defined operators to see if there are any already defined that match the name of the
        // new definition, otherwise a map of operators by fixity needs to be created.
        EnumMap<OpSymbol.Fixity, OpSymbol> operatorMap = operators.get(textName);

        // Check if the priority is non-zero in which case the operator is being added or redefined.
        if (priority > 0)
        {
            if (operatorMap == null)
            {
                operatorMap = new EnumMap<OpSymbol.Fixity, OpSymbol>(OpSymbol.Fixity.class);
                operators.put(textName, operatorMap);
            }

            // Check if the operators fixity to see if further rules regarding simultaneous definition of post and
            // infix operators need to be applied.
            if (opSymbol.isPostfix())
            {
                // Postfix, so check if an infix definition already exists, which is not allowed.
                if (operatorMap.containsKey(OpSymbol.Fixity.In))
                {
                    throw new IllegalArgumentException(
                        "Cannot define a postfix operator when an infix one with the same name already exists.");
                }
            }
            else if (opSymbol.isInfix())
            {
                // Infix, so check if a postfix definition already exists, which is not allowed.
                if (operatorMap.containsKey(OpSymbol.Fixity.Post))
                {
                    throw new IllegalArgumentException(
                        "Cannot define an infix operator when an postfix one with the same name already exists.");
                }
            }

            // Add the operator to the table replacing any previous definition of the same fixity.
            operatorMap.put(opSymbol.getFixity(), opSymbol);
        }
        else
        {
            // The priority is zero, in which case the operator is to be removed.
            if ((operatorMap != null) && opSymbol.isPrefix())
            {
                // Remove it from the prefix table, if it exists there.
                operatorMap.remove(OpSymbol.Fixity.Pre);
            }
            else if ((operatorMap != null) && (opSymbol.isPostfix() || opSymbol.isInfix()))
            {
                // Remove it from the postfix/infix table, if it exists there.
                operatorMap.remove(OpSymbol.Fixity.Post);
                operatorMap.remove(OpSymbol.Fixity.In);
            }
        }
    }

    /**
     * Checks the operator table for all possible operators matching a given name.
     *
     * @param  name The interned name of the operator to find.
     *
     * @return An array of matching operators, or <tt>null</tt> if none can be found.
     */
    public EnumMap<OpSymbol.Fixity, OpSymbol> getOperatorsMatchingNameByFixity(String name)
    {
        return operators.get(name);
    }

    /**
     * Prints the current state of this parser as a string, mainly for debugging purposes.
     *
     * @return The current state of this parser as a string.
     */
    public String toString()
    {
        return "DynamicOperatorsParser: [ state = " + state + ", nextTerm = " + nextTerm + " stack = " + stack +
            " outputStack = " + outputStack + " ]";
    }

    /**
     * Checks if a candidate operator symbol can have one of the specified fixities, and resolve it to an oeprator with
     * that fixity if so. If it cannot be resolved an exception is raised.
     *
     * @param  candidate The candidate operator symbol to resolve.
     * @param  fixities  The possible fixities to resolve the symbol to.
     *
     * @return The candidate operator resolved to an actual operator.
     *
     * @throws SourceCodeException If the candidate operator cannot be resolved.
     */
    protected static OpSymbol checkAndResolveToFixity(CandidateOpSymbol candidate, OpSymbol.Fixity... fixities)
        throws SourceCodeException
    {
        OpSymbol result = null;

        for (OpSymbol.Fixity fixity : fixities)
        {
            result = candidate.getPossibleOperators().get(fixity);

            if (result != null)
            {
                break;
            }
        }

        if (result == null)
        {
            throw new SourceCodeException("Operator " + candidate + " must be one of " + Arrays.toString(fixities) +
                ", but does not have the required form.", null, null, null, candidate.getSourceCodePosition());
        }

        return result;
    }

    /**
     * A base class for defining parsers actions and rule reductions from.
     */
    private abstract static class Action
    {
        /**
         * Applies a parser action; shift, reduce, resolve or accept.
         *
         * @throws SourceCodeException With an error location if the action cannot be performed because the input
         *                             sequence does not form a valid instance of the grammar.
         */
        public abstract void apply() throws SourceCodeException;
    }

    /**
     * Defines a shift action. Shift changes to a new state, places the new state on the stack, consumes one input
     * symbol and places the symbol on the output stack.
     */
    private class ShiftAction extends Action
    {
        /** Holds the state to shift to. */
        public int toState;

        /**
         * Creates a shift action to the specified state.
         *
         * @param toState The state to shift to.
         */
        private ShiftAction(int toState)
        {
            this.toState = toState;
        }

        /**
         * Performs a shift action. Changes to a new state, places the new state on the stack, consumes one input symbol
         * and places the symbol on the output stack.
         */
        public void apply()
        {
            state = toState;
            stack.offer(state);
            position++;
            outputStack.offer(nextTerm);
        }
    }

    /**
     * Defines a reduce action. Reduce consumes states from the state stack, and symbols from the output stack in number
     * equal to the number of symbols on the right hand side of the rule that is being reduced by. It then shifts to the
     * state given by the goto table from the state left on top of the state stack. In this implementation the
     * consumption of symbols and states from the stacks is delegated to a {@link Action} implementation.
     */
    private class ReduceAction extends Action
    {
        /** Holds the rule number to reduce by. */
        public int ruleNum;

        /**
         * Creates a reduce action to reduce by the specified rule.
         *
         * @param rule The rule number to reduce by.
         */
        private ReduceAction(int rule)
        {
            this.ruleNum = rule;
        }

        /**
         * Performs a reduce by the specified rule number.
         *
         * @throws SourceCodeException With an error location if the action cannot be performed because the input
         *                             sequence does not form a valid instance of the grammar.
         */
        public void apply() throws SourceCodeException
        {
            Action rule = rules[ruleNum];
            rule.apply();

            state = gotoTable[stack.peek()];
            stack.offer(state);
        }
    }

    /**
     * ResolveAction decides a shift-reduce conflict between a pair of {@link CandidateOpSymbol}s. The resolution
     * procedure is as described in the class level comment for {@link DynamicOperatorParser}.
     */
    private class ResolveAction extends Action
    {
        /** The shift action to perform if a shift resolution is chosen. */
        ShiftAction shift;

        /** The reduce action to perform if a reduce resolution is chosen. */
        ReduceAction reduce;

        /**
         * Creates a resolve action between the specified shift and reduce actions.
         *
         * @param shift  The shift action to perform if a shift resolution is chosen.
         * @param reduce The reduce action to perform if a reduce resolution is chosen.
         */
        private ResolveAction(ShiftAction shift, ReduceAction reduce)
        {
            this.shift = shift;
            this.reduce = reduce;
        }

        /**
         * Performs shift-reduce resolution between a pair of operators as describe in {@link DynamicOperatorParser}.
         *
         * @throws SourceCodeException With an error location if the action cannot be performed because the input
         *                             sequence does not form a valid instance of the grammar.
         */
        public void apply() throws SourceCodeException
        {
            // This cast should not fail, as resolve is only called when the next symbol is a candidate operator.
            CandidateOpSymbol nextCandidate = (CandidateOpSymbol) nextTerm;

            // Walk back down the output stack looking for the last symbol and checking out what other terms may
            // exist on the stack too. This evaluates the output stack, looking at the previously parsed sequence
            // of terms that led to this conflict as a sequence of the form 'alpha OpA beta OpB', where OpA is the
            // previously unresolved operator conflicting with the nextTerm, and alpha and beta are potentially
            // empty sequences of symbols. The following code works out what the previous operator is and whether
            // alpha and beta are empty. At the end of this lastCandidate cannot be null, as resolve is only called
            // when a previous candidate symbol has been encountered.
            boolean alpha = false;
            boolean beta = false;
            CandidateOpSymbol lastCandidate = null;
            int pos = 0;

            for (Term nextTerm : outputStack)
            {
                boolean isOpSymbol = (nextTerm instanceof CandidateOpSymbol);

                if ((pos == 0) && !isOpSymbol)
                {
                    beta = true;
                }
                else if ((pos == 0) && isOpSymbol)
                {
                    lastCandidate = (CandidateOpSymbol) nextTerm;
                }
                else if ((pos == 1) && beta)
                {
                    lastCandidate = (CandidateOpSymbol) nextTerm;
                }
                else if ((pos == 1) && !beta)
                {
                    alpha = true;

                    break;
                }
                else if (pos == 2)
                {
                    alpha = true;

                    break;
                }

                pos++;
            }

            if (lastCandidate == null)
            {
                throw new IllegalStateException("'lastCandidate' is null but this should not be the case as this resolve " +
                    "action is only called when a previous candidate symbol exists.");
            }

            // Based on the form of the symbol sequence preceding the next symbol, work out which combination
            // of operator fixities in uniquely possible, and resolve the candidate operators onto that combination.
            OpSymbol lastOp;
            OpSymbol nextOp;

            if (alpha && beta)
            {
                lastOp = checkAndResolveToFixity(lastCandidate, OpSymbol.Fixity.In);
                nextOp = checkAndResolveToFixity(nextCandidate, OpSymbol.Fixity.In, OpSymbol.Fixity.Post);
            }
            else if (!alpha && beta)
            {
                lastOp = checkAndResolveToFixity(lastCandidate, OpSymbol.Fixity.Pre);
                nextOp = checkAndResolveToFixity(nextCandidate, OpSymbol.Fixity.In, OpSymbol.Fixity.Post);
            }
            else if (alpha && !beta)
            {
                lastOp = checkAndResolveToFixity(lastCandidate, OpSymbol.Fixity.In, OpSymbol.Fixity.Post);

                if (lastOp.isPostfix())
                {
                    nextOp = checkAndResolveToFixity(nextCandidate, OpSymbol.Fixity.In, OpSymbol.Fixity.Post);
                }
                else
                {
                    nextOp = checkAndResolveToFixity(nextCandidate, OpSymbol.Fixity.Pre);
                }
            }
            else
            {
                lastOp = checkAndResolveToFixity(lastCandidate, OpSymbol.Fixity.Pre);
                nextOp = checkAndResolveToFixity(nextCandidate, OpSymbol.Fixity.Pre);
            }

            // Compare the priority and associativities of the conflicting operators to decide whether to shift
            // or reduce.
            int comparison = lastOp.compareTo(nextOp);

            if (comparison < 0)
            {
                reduce.apply();
            }
            else if (comparison > 0)
            {
                shift.apply();
            }
            else
            {
                if (nextOp.isLeftAssociative())
                {
                    reduce.apply();
                }
                else if (nextOp.isRightAssociative())
                {
                    shift.apply();
                }
                else
                {
                    throw new SourceCodeException("Ambiguous operator associativity. Expression requires brackets.",
                        null, null, null, nextOp.getSourceCodePosition());
                }
            }
        }
    }

    /**
     * An error handling action to be used when an unknown transition in the parser table is attempted. This action
     * creates a source code exception with the location of the error in it.
     */
    private class ErrorAction extends Action
    {
        /** Holds a custom error description. */
        private final String errorMessage;

        /**
         * Creates an error action with the specified error message.
         *
         * @param errorMessage The error message.
         */
        private ErrorAction(String errorMessage)
        {
            this.errorMessage = errorMessage;
        }

        /**
         * Applies a parser error action that will generate an exception with the location of the current parse term in
         * it.
         *
         * @throws SourceCodeException With an error location for the current parse term.
         */
        public void apply() throws SourceCodeException
        {
            throw new SourceCodeException(errorMessage, null, null, null, nextTerm.getSourceCodePosition());
        }
    }

    /**
     * Performs an accept action. This advances over the last symbol in the input sequence so that the parser
     * terminates.
     */
    private class Accept extends Action
    {
        /** Accepts the input sequence as valid. */
        public void apply()
        {
            // Advance one beyond the final position to indicate acceptance of the sentence as a valid instance of
            // the grammar.
            position++;
        }
    }

    /**
     * Implements a reduction by rule 1 of the grammar. This consumes a single state from the state stack.
     */
    private class Rule1 extends Action
    {
        /** Defines the number of symbols on the right hand side of this rule. */
        private static final int NUM_SYMBOLS_RHS = 1;

        /** Reduces by rule 1. */
        public void apply()
        {
            for (int i = 0; i < NUM_SYMBOLS_RHS; i++)
            {
                stack.poll();
            }
        }
    }

    /**
     * Implements a reduction by rule 2 of the grammar. This consumes two states from the state stack, and expects the
     * top two symbols on the output stack to be a terminal follow by an operator candidate that resolves to a prefix
     * operator.
     */
    private class Rule2 extends Action
    {
        /** Defines the number of symbols on the right hand side of this rule. */
        private static final int NUM_SYMBOLS_RHS = 2;

        /**
         * Reduces by rule 2.
         *
         * @throws SourceCodeException With an error location if the action cannot be performed because the input
         *                             sequence does not form a valid instance of the grammar.
         */
        public void apply() throws SourceCodeException
        {
            // Consume from the state stack for the number of RHS symbols.
            for (int i = 0; i < NUM_SYMBOLS_RHS; i++)
            {
                stack.poll();
            }

            // Attempt to consume a term and an operator in prefix order from the output stack.
            Term t = outputStack.poll();
            CandidateOpSymbol candidate = (CandidateOpSymbol) outputStack.poll();

            OpSymbol op = checkAndResolveToFixity(candidate, OpSymbol.Fixity.Pre);

            // Clone the operator symbol from the operator table before adding the unique source code position and
            // argument for this symbol instance.
            op = op.copySymbol();
            op.setSourceCodePosition(candidate.getSourceCodePosition());
            op.setArguments(new Term[] { t });

            // Place the fully parsed, promoted operator back onto the output stack.
            outputStack.offer(op);
        }
    }

    /**
     * Implements a reduction by rule 3 of the grammar. This consumes two states from the state stack, and expects the
     * top two symbols on the output stack to be an operator candidate that resolves to a postfix operator followed by a
     * terminal.
     */
    private class Rule3 extends Action
    {
        /** Defines the number of symbols on the right hand side of this rule. */
        private static final int NUM_SYMBOLS_RHS = 2;

        /**
         * Reduces by rule 3.
         *
         * @throws SourceCodeException With an error location if the action cannot be performed because the input
         *                             sequence does not form a valid instance of the grammar.
         */
        public void apply() throws SourceCodeException
        {
            // Consume from the state stack for the number of RHS symbols.
            for (int i = 0; i < NUM_SYMBOLS_RHS; i++)
            {
                stack.poll();
            }

            // Attempt to consume an operator and a term in postfix order from the output stack.
            CandidateOpSymbol candidate = (CandidateOpSymbol) outputStack.poll();
            Term t = outputStack.poll();

            OpSymbol op = checkAndResolveToFixity(candidate, OpSymbol.Fixity.Post);

            // Clone the operator symbol from the operator table before adding the unique source code position and
            // argument for this symbol instance.
            op = op.copySymbol();
            op.setSourceCodePosition(candidate.getSourceCodePosition());
            op.setArguments(new Term[] { t });

            outputStack.offer(op);
        }
    }

    /**
     * Implements a reduction by rule 3 of the grammar. This consumes three states from the state stack, and expects the
     * top three symbols on the output stack to be a terminal followed by ann operator candidate that resolves to a
     * infix operator followed by a terminal.
     */
    private class Rule4 extends Action
    {
        /** Defines the number of symbols on the right hand side of this rule. */
        private static final int NUM_SYMBOLS_RHS = 3;

        /**
         * Reduces by rule 4.
         *
         * @throws SourceCodeException With an error location if the action cannot be performed because the input
         *                             sequence does not form a valid instance of the grammar.
         */
        public void apply() throws SourceCodeException
        {
            // Consume from the state stack for the number of RHS symbols.
            for (int i = 0; i < NUM_SYMBOLS_RHS; i++)
            {
                stack.poll();
            }

            // Attempt to consume a term, ann operator and a term in infix order from the output stack.
            Term t1 = outputStack.poll();
            CandidateOpSymbol candidate = (CandidateOpSymbol) outputStack.poll();
            Term t2 = outputStack.poll();

            OpSymbol op = checkAndResolveToFixity(candidate, OpSymbol.Fixity.In);

            // Clone the operator symbol from the operator table before adding the unique source code position and
            // argument for this symbol instance.
            // Note that the order of the arguments is swapped here, because they come off the stack backwards.
            op = op.copySymbol();
            op.setSourceCodePosition(candidate.getSourceCodePosition());
            op.setArguments(new Term[] { t2, t1 });

            outputStack.offer(op);
        }
    }
}
