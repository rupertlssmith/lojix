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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.thesett.aima.logic.fol.AllTermsVisitor;
import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.DelegatingAllTermsVisitor;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorName;
import com.thesett.aima.logic.fol.FunctorTermPredicate;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.LogicCompilerObserver;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.TermUtils;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.bytecode.BaseMachine;
import com.thesett.aima.logic.fol.compiler.PositionalContext;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverser;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverserImpl;
import com.thesett.aima.logic.fol.compiler.SymbolKeyTraverser;
import com.thesett.aima.logic.fol.compiler.TermWalker;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.REG_ADDR;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.STACK_ADDR;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet;
import com.thesett.aima.logic.fol.wam.optimizer.Optimizer;
import com.thesett.aima.logic.fol.wam.optimizer.WAMOptimizer;
import com.thesett.aima.search.QueueBasedSearchMethod;
import com.thesett.aima.search.util.Searches;
import com.thesett.aima.search.util.backtracking.DepthFirstBacktrackingSearch;
import com.thesett.aima.search.util.uninformed.BreadthFirstSearch;
import com.thesett.aima.search.util.uninformed.PostFixSearch;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.util.SizeableLinkedList;
import com.thesett.common.util.doublemaps.SymbolKey;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * WAMCompiled implements a compiler for the logical language, WAM, into a form suitable for passing to an
 * {@link WAMMachine}. The WAMMachine accepts sentences in the language that are compiled into a byte code form. The
 * byte instructions used in the compiled language are enumerated as constants in the {@link WAMInstruction} class.
 *
 * <p/>The compilation process is described in "Warren's Abstract Machine, A Tutorial Reconstruction, by Hassan
 * Ait-Kaci" and is followed as closely as possible to the WAM compiler given there. The description of the L0
 * compilation process is very clear in the text but the WAM compilation is a little ambiguous. It does not fully
 * describe the flattening process and presents some conflicting examples of register assignment. (The flattening
 * process is essentially the same as for L0, except that each argument of the outermost functor is flattened/compiled
 * independently). The register assignment process is harder to fathom, on page 22, the register assignment for p(Z,
 * h(Z,W), f(W)) is presented with the following assignment given:
 *
 * <pre>
 * A1 = Z
 * A2 = h(A1,X4)
 * A3 = f(X4)
 * X4 = W
 * </pre>
 *
 * In figure 2.9 a compilation example is given, from which it can be seen that the assignment should be:
 *
 * <pre>
 * A1 = Z (loaded from X4)
 * A2 = h(X4,X5)
 * A3 = f(X5)
 * X4 = Z
 * X5 = W
 * </pre>
 *
 * <p/>From figure 2.9 it was concluded that argument registers may only be assigned to functors. Functors can be
 * created on the heap and assigned to argument registers directly. Argument registers for variables, should be loaded
 * from a separate register assigned to the variable, that comes after the argument registers; so that a variable
 * assignment can be copied into multiple arguments, where the same variable is presented multiple times in a predicate
 * call. The register assignment process is carried out in two phases to do this, the first pass covers the argument
 * registers and the arguments of the outermost functor, only assigning to functors, the second pass continues for
 * higher numbered registers, starts again at the beginning of the arguments, and assigns to variables and functors (not
 * already assigned) as for the L0 process.
 *
 * <p/>A brief overview of the compilation process is:
 *
 * <pre><p/><ul>
 * <li>Terms to be compiled are allocated registers, breadth first, enumerating from outermost functors down to
 *    innermost atoms or variables.</li>
 * <li>The outermost functor itself is treated specially, and is not allocated to a register. Its i arguments are
 *     allocated to registers, and are additionally associated with the first i argument registers. The outermost functor
 *     is the instigator of a call, in the case of queries, or the recipient of a call, in the case of programs.
 * <li>Queries are 'flattened' by traversing each of their arguments in postfix order of their functors, then exploring
 *     the functors arguments.</li>
 * <li>Programs are 'flattened' by traversing each of their arguments breadth first, the same as for the original
 *     register allocation, then exploring the functors arguments.</li>
 * </ul></pre>
 *
 * <p/>Query terms are compiled into a sequence of instructions, that build up a representation of their argument terms,
 * to be unified, on the heap, and assigning registers to refer to those terms on the heap, then calling the matching
 * program for the query terms name and arity. Program terms are compiled into a sequence of instructions that, when run
 * against the argument registers, attempt to unify all of the arguments with the heap.
 *
 * <p/>The effect of flattening queries using a post fix ordering, is that the values of inner functors and variables
 * are loaded into registers first, before their containing functor is executed, which writes the functor and its
 * arguments onto the heap. Programs do not need to be expanded in this way, they simply match functors followed by
 * their arguments against the heap, so a breadth first traversal is all that is needed.
 *
 * <p/>Evaluating a flattened query consists of doing the following as different query tokens are encountered:
 *
 * <pre><p/><ol>
 * <li>For the outermost functor, process all arguments, then make a CALL (functor) to the matching program.
 * <li>For a register associated with an inner functor, push an STR onto the heap and copy that cell into the register.
 *     A put_struc (functor, register) instruction is created for this.</li>
 * <li>For a variable in argument position i in the outermost functor, push a REF onto the heap that refers to itself,
 *     and copy that value into that variables register, as well as argument register i. A put_var (register, register)
 *     instruction is emitted for this.
 * <li>For a register argument of an inner functor, not previously seen, push a REF onto the heap that refers to itself,
 *     and copy that cell into the register. A set_var (register) instruction is emitted for this.</li>
 * <li>For a variables in argument position i in the outermost functor, previously seen, copy its assigned register
 *     into its argument register. A put_val (register, register) instruction is emitted for this.</li>
 * <li>For a register argument previously seen, push a new cell onto the heap and copy into it the register's value.
 *     A set_val (register) instruction is emitted for this.</li>
 * </ol></pre>
 *
 * <p/>Evaluating a flattened program consists of doing the following as different program tokens are encountered:
 *
 * <pre><p/><ol>
 * <li>For the outermost functor, process all arguments, then execute a PROCEED instruction to indicate success.
 * <li>For a register associated with an inner functor, load that register with a reference to the functor. A get_struc
 *     (functor, register) instruction is created for this.</li>
 * <li>For a variable in argument position i in the outermost functor, copy its argument register into its assigned
 *     register. A get_var (register, register) instruction is emitted for this.
 * <li>For a register argument of an inner functor, not previously seen, bind that register to its argument. A
 *     unify_var (register) instruction is output for this.</li>
 * <li>For a variable in argument position i in the outermost functor, unify its assigned register with the
 *     argument register. A get_val (register, register) instruction is emitted for this.</li>
 * <li>For a register argument of an inner functor, previously seen, unify that register against the heap. A
 *     unify_val (register) instruction is emitted for this.</li>
 * </ol></pre>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Transform WAM sentences into compiled byte code.
 *     <td> {@link WAMMachine}, {@link WAMCompiledPredicate}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WAMCompiler extends BaseMachine implements LogicCompiler<Clause, WAMCompiledPredicate, WAMCompiledQuery>
{
    /** Used for debugging. */
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(WAMCompiler.class.getName());

    /** The symbol table key for allocations. */
    public static final String SYMKEY_ALLOCATION = "allocation";

    /** The symbol table key for the number of permanent variables remaining. */
    protected static final String SYMKEY_PERM_VARS_REMAINING = "perm_vars_remaining";

    /** The symbol table key for variable occurrence counts. */
    public static final String SYMKEY_VAR_OCCURRENCE_COUNT = "var_occurrence_count";

    /** The symbol table key for variable position of occurrence. */
    public static final String SYMKEY_VAR_NON_ARG = "var_non_arg";

    /** The symbol table key for functor position of occurrence. */
    public static final String SYMKEY_FUNCTOR_NON_ARG = "functor_non_arg";

    /** The symbol table key for variable introduction type. */
    public static final String SYMKEY_VARIABLE_INTRO = "variable_intro";

    /** The symbol table key for the last functor in which a variable occurs, if it is purely in argument position. */
    public static final String SYMKEY_VAR_LAST_ARG_FUNCTOR = "var_last_arg_functor";

    /** The symbol table key for predicate sources. */
    protected static final String SYMKEY_PREDICATES = "source_predicates";

    /** Enumerates the possible ways in which a variable can be introduced to a clause. */
    private enum VarIntroduction
    {
        /** Introduced by a get instruction. */
        Get,

        /** Introduced by a put instruction. */
        Put,

        /** Introduced by a set instruction */
        Set,

        /** Introduced by a unify instruction. */
        Unify
    }

    /** Holds a list of all predicates encountered in the current scope. */
    protected Queue<SymbolKey> predicatesInScope = new LinkedList<SymbolKey>();

    /** Holds the compiler output observer. */
    private LogicCompilerObserver<WAMCompiledPredicate, WAMCompiledQuery> observer;

    /** This is used to keep track of registers as they are seen. */
    private Set<Integer> seenRegisters = new TreeSet<Integer>();

    /** Used to keep track of the temporary register assignment across multiple functors within a clause. */
    protected int lastAllocatedTempReg;

    /** This is used to keep track of the number of permanent variables. */
    protected int numPermanentVars;

    /** Keeps count of the current compiler scope, to keep symbols in each scope fresh. */
    protected int scope = 0;

    /** Holds the current nested compilation scope symbol table. */
    private SymbolTable<Integer, String, Object> scopeTable;

    /** Holds the instruction optimizer. */
    private Optimizer optimizer;

    /**
     * Creates a new WAMCompiler.
     *
     * @param symbolTable The symbol table.
     * @param interner    The machine to translate functor and variable names.
     */
    public WAMCompiler(SymbolTable<Integer, String, Object> symbolTable, VariableAndFunctorInterner interner)
    {
        super(symbolTable, interner);
        optimizer = new WAMOptimizer(symbolTable, interner);
    }

    /** {@inheritDoc} */
    public void setCompilerObserver(LogicCompilerObserver<WAMCompiledPredicate, WAMCompiledQuery> observer)
    {
        this.observer = observer;
    }

    /** {@inheritDoc} */
    public void endScope() throws SourceCodeException
    {
        // Loop over all predicates in the current scope, found in the symbol table, and consume and compile them.
        for (SymbolKey predicateKey = predicatesInScope.poll(); predicateKey != null;
                predicateKey = predicatesInScope.poll())
        {
            List<Clause> clauseList = (List<Clause>) scopeTable.get(predicateKey, SYMKEY_PREDICATES);

            // Used to keep track of where within the predicate the current clause is.
            int size = clauseList.size();
            int current = 0;
            boolean multipleClauses = size > 1;

            // Used to build up the compiled predicate in.
            WAMCompiledPredicate result = null;

            for (Iterator<Clause> iterator = clauseList.iterator(); iterator.hasNext(); iterator.remove())
            {
                Clause clause = iterator.next();

                if (result == null)
                {
                    result = new WAMCompiledPredicate(clause.getHead().getName());
                }

                // Compile the single clause, adding it to the parent compiled predicate.
                compileClause(clause, result, current == 0, current >= (size - 1), multipleClauses, current);
                current++;
            }

            // Run the optimizer on the output.
            result = optimizer.apply(result);

            displayCompiledPredicate(result);
            observer.onCompilation(result);

            // Move up the low water mark on the predicates table.
            symbolTable.setLowMark(predicateKey, SYMKEY_PREDICATES);
        }

        // Clear up the symbol table, and bump the compilation scope up by one.
        symbolTable.clearUpToLowMark(SYMKEY_PREDICATES);
        scopeTable = null;
        scope++;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Compiles a sentence into a binary form, that provides a Java interface into the compiled structure.
     *
     * <p/>The clausal sentence may be a query, or a program statement. If it is a query, it is compiled immediately. If
     * it is a clause, it is retained against the predicate which it forms part of, and compiled on the
     * {@link #endScope()} method is invoked.
     */
    public void compile(Sentence<Clause> sentence) throws SourceCodeException
    {
        /*log.fine("public WAMCompiledClause compile(Sentence<Term> sentence = " + sentence + "): called");*/

        // Extract the clause to compile from the parsed sentence.
        Clause clause = sentence.getT();
        initialiseSymbolTable(clause);

        // Classify the sentence to compile by the different sentence types in the language.
        if (clause.isQuery())
        {
            compileQuery(clause);
        }
        else
        {
            // Initialise a nested symbol table for the current compilation scope, if it has not already been.
            if (scopeTable == null)
            {
                scopeTable = symbolTable.enterScope(scope);
            }

            // Check in the symbol table, if a compiled predicate with name matching the program clause exists, and if
            // not create it.
            SymbolKey predicateKey = scopeTable.getSymbolKey(clause.getHead().getName());
            List<Clause> clauseList = (List<Clause>) scopeTable.get(predicateKey, SYMKEY_PREDICATES);

            if (clauseList == null)
            {
                clauseList = new LinkedList<Clause>();
                scopeTable.put(predicateKey, SYMKEY_PREDICATES, clauseList);
                predicatesInScope.offer(predicateKey);
            }

            // Add the clause to compile to its parent predicate for compilation at the end of the current scope.
            clauseList.add(clause);
        }
    }

    /**
     * Compiles a program clause, and adds its instructions to a compiled predicate.
     *
     * @param  clause            The source clause to compile.
     * @param  compiledPredicate The predicate to add instructions to.
     * @param  isFirst           <tt>true</tt> iff the clause is the first in the predicate.
     * @param  isLast            <tt>true</tt> iff the clause is the last in the predicate.
     * @param  multipleClauses   <tt>true</tt> iff the predicate contains >1 clause.
     * @param  clauseNumber      The position of the clause within the predicate.
     *
     * @throws SourceCodeException If there is an error in the source code preventing its compilation.
     */
    private void compileClause(Clause clause, WAMCompiledPredicate compiledPredicate, boolean isFirst, boolean isLast,
        boolean multipleClauses, int clauseNumber) throws SourceCodeException
    {
        // Used to build up the compiled clause in.
        WAMCompiledClause result = new WAMCompiledClause(compiledPredicate);

        // Check if the clause to compile is a fact (no body).
        boolean isFact = clause.getBody() == null;

        // Check if the clause to compile is a chain rule, (one called body).
        boolean isChainRule = (clause.getBody() != null) && (clause.getBody().length == 1);

        // Used to keep track of registers as they are seen during compilation. The first time a variable is seen,
        // a variable is written onto the heap, subsequent times its value. The first time a functor is seen,
        // its structure is written onto the heap, subsequent times it is compared with.
        seenRegisters = new TreeSet<Integer>();

        // This is used to keep track of the next temporary register available to allocate.
        lastAllocatedTempReg = findMaxArgumentsInClause(clause);

        // This is used to keep track of the number of permanent variables.
        numPermanentVars = 0;

        // These are used to generate pre and post instructions for the clause, for example, for the creation and
        // clean-up of stack frames.
        SizeableLinkedList<WAMInstruction> preFixInstructions = new SizeableLinkedList<WAMInstruction>();
        SizeableLinkedList<WAMInstruction> postFixInstructions = new SizeableLinkedList<WAMInstruction>();

        // Find all the free non-anonymous variables in the clause.
        Set<Variable> freeVars = TermUtils.findFreeNonAnonymousVariables(clause);
        Set<Integer> freeVarNames = new TreeSet<Integer>();

        for (Variable var : freeVars)
        {
            freeVarNames.add(var.getName());
        }

        // Allocate permanent variables for a program clause. Program clauses only use permanent variables when really
        // needed to preserve variables across calls.
        allocatePermanentProgramRegisters(clause);

        // Gather information about the counts and positions of occurrence of variables and constants within the clause.
        gatherPositionAndOccurrenceInfo(clause);

        // Labels the entry point to each choice point.
        FunctorName fn = interner.getFunctorFunctorName(clause.getHead());
        WAMLabel entryLabel = new WAMLabel(fn, clauseNumber);

        // Label for the entry point to the next choice point, to backtrack to.
        WAMLabel retryLabel = new WAMLabel(fn, clauseNumber + 1);

        // Create choice point instructions for the clause, depending on its position within the containing predicate.
        // The choice point instructions are only created when a predicate is built from multiple clauses, as otherwise
        // there are no choices to be made.
        if (isFirst && !isLast && multipleClauses)
        {
            // try me else.

            preFixInstructions.add(new WAMInstruction(entryLabel, WAMInstructionSet.TryMeElse, retryLabel));
        }
        else if (!isFirst && !isLast && multipleClauses)
        {
            // retry me else.
            preFixInstructions.add(new WAMInstruction(entryLabel, WAMInstructionSet.RetryMeElse, retryLabel));
        }
        else if (isLast && multipleClauses)
        {
            // trust me.
            preFixInstructions.add(new WAMInstruction(entryLabel, WAMInstructionSet.TrustMe));
        }

        // Generate the prefix code for the clause.
        // Rules may chain multiple, so require stack frames to preserve registers across calls.
        // Facts are always leafs so can use the global continuation point register to return from calls.
        // Chain rules only make one call, so also do not need a stack frame.
        if (!(isFact || isChainRule))
        {
            // Allocate a stack frame at the start of the clause.
            /*log.fine("ALLOCATE " + numPermanentVars);*/
            preFixInstructions.add(new WAMInstruction(WAMInstructionSet.Allocate));
        }

        result.addInstructions(preFixInstructions);

        // Compile the clause head.
        Functor expression = clause.getHead();

        SizeableLinkedList<WAMInstruction> instructions = compileHead(expression);
        result.addInstructions(expression, instructions);

        // Compile all of the conjunctive parts of the body of the clause, if there are any.
        if (!isFact)
        {
            Functor[] expressions = clause.getBody();

            for (int i = 0; i < expressions.length; i++)
            {
                expression = expressions[i];

                // The 'isFirstBody' parameter is only set to true, when this is the first functor of a rule.
                instructions = compileBody(expression, i == 0);

                // Generate the call or tail-call instructions, followed by the call address, which is f_n of the
                // called program.
                if (i == (expressions.length - 1))
                {
                    // Deallocate the stack frame at the end of the clause, but prior to calling the last
                    // body predicate.
                    // This is not required for chain rules, as they do not need a stack frame.
                    if (!isChainRule)
                    {
                        instructions.add(new WAMInstruction(WAMInstructionSet.Deallocate));
                    }

                    instructions.add(new WAMInstruction(WAMInstructionSet.Execute,
                            interner.getFunctorFunctorName(expression)));
                }
                else
                {
                    int permVarsRemaining =
                        (Integer) symbolTable.get(expression.getSymbolKey(), SYMKEY_PERM_VARS_REMAINING);

                    instructions.add(new WAMInstruction(WAMInstructionSet.Call, (byte) (permVarsRemaining & 0xff),
                            interner.getFunctorFunctorName(expression)));
                }

                result.addInstructions(expression, instructions);
            }
        }

        // Generate the postfix code for the clause. Rules may chain, so require stack frames.
        // Facts are always leafs so can use the global continuation point register to return from calls.
        if (isFact)
        {
            /*log.fine("PROCEED");*/
            postFixInstructions.add(new WAMInstruction(WAMInstructionSet.Proceed));
        }

        result.addInstructions(postFixInstructions);
    }

    /**
     * Compiles a clause as a query. The clause should have no head, only a body.
     *
     * @param  clause The clause to compile as a query.
     *
     * @throws SourceCodeException If there is an error in the source code preventing its compilation.
     */
    private void compileQuery(Clause clause) throws SourceCodeException
    {
        // Used to build up the compiled result in.
        WAMCompiledQuery result;

        // A mapping from top stack frame slots to interned variable names is built up in this.
        // This is used to track the stack positions that variables in a query are assigned to.
        Map<Byte, Integer> varNames = new TreeMap<Byte, Integer>();

        // Used to keep track of registers as they are seen during compilation. The first time a variable is seen,
        // a variable is written onto the heap, subsequent times its value. The first time a functor is seen,
        // its structure is written onto the heap, subsequent times it is compared with.
        seenRegisters = new TreeSet<Integer>();

        // This is used to keep track of the next temporary register available to allocate.
        lastAllocatedTempReg = findMaxArgumentsInClause(clause);

        // This is used to keep track of the number of permanent variables.
        numPermanentVars = 0;

        // These are used to generate pre and post instructions for the clause, for example, for the creation and
        // clean-up of stack frames.
        SizeableLinkedList<WAMInstruction> preFixInstructions = new SizeableLinkedList<WAMInstruction>();
        SizeableLinkedList<WAMInstruction> postFixInstructions = new SizeableLinkedList<WAMInstruction>();

        // Find all the free non-anonymous variables in the clause.
        Set<Variable> freeVars = TermUtils.findFreeNonAnonymousVariables(clause);
        Set<Integer> freeVarNames = new TreeSet<Integer>();

        for (Variable var : freeVars)
        {
            freeVarNames.add(var.getName());
        }

        // Allocate permanent variables for a query. In queries all variables are permanent so that they are preserved
        // on the stack upon completion of the query.
        allocatePermanentQueryRegisters(clause, varNames);

        // Gather information about the counts and positions of occurrence of variables and constants within the clause.
        gatherPositionAndOccurrenceInfo(clause);

        result = new WAMCompiledQuery(varNames, freeVarNames);

        // Generate the prefix code for the clause. Queries require a stack frames to hold their environment.
        /*log.fine("ALLOCATE " + numPermanentVars);*/
        preFixInstructions.add(new WAMInstruction(WAMInstructionSet.AllocateN, REG_ADDR,
                (byte) (numPermanentVars & 0xff)));

        result.addInstructions(preFixInstructions);

        // Compile all of the conjunctive parts of the body of the clause, if there are any.
        Functor[] expressions = clause.getBody();

        for (int i = 0; i < expressions.length; i++)
        {
            Functor expression = expressions[i];

            // The 'isFirstBody' parameter is only set to true, when this is the first functor of a rule, which it
            // never is for a query.
            SizeableLinkedList<WAMInstruction> instructions = compileBody(expression, false);

            // Generate the call instructions, followed by the call address, which is f_n of the called program.
            WAMInstruction instruction =
                new WAMInstruction(WAMInstructionSet.Call, (byte) (numPermanentVars & 0xff),
                    interner.getFunctorFunctorName(expression));
            instructions.add(instruction);

            result.addInstructions(expression, instructions);
        }

        // Generate the postfix code for the clause.
        /*log.fine("DEALLOCATE");*/
        postFixInstructions.add(new WAMInstruction(WAMInstructionSet.Suspend));
        postFixInstructions.add(new WAMInstruction(WAMInstructionSet.Deallocate));

        result.addInstructions(postFixInstructions);

        // Run the optimizer on the output.
        result = optimizer.apply(result);

        displayCompiledQuery(result);

        observer.onQueryCompilation(result);
    }

    /**
     * Examines all top-level functors within a clause, including any head and body, and determines which functor has
     * the highest number of arguments.
     *
     * @param  clause The clause to determine the highest number of arguments within.
     *
     * @return The highest number of arguments within any top-level functor in the clause.
     */
    private int findMaxArgumentsInClause(Clause clause)
    {
        int result = 0;

        Functor head = clause.getHead();

        if (head != null)
        {
            result = head.getArity();
        }

        Functor[] body = clause.getBody();

        if (body != null)
        {
            for (int i = 0; i < body.length; i++)
            {
                int arity = body[i].getArity();
                result = (arity > result) ? arity : result;
            }
        }

        return result;
    }

    /**
     * Runs a symbol key traverser over the clause to be compiled, to ensure that all of its terms and sub-terms have
     * their symbol keys initialised.
     *
     * @param clause The clause to initialise the symbol keys of.
     */
    private void initialiseSymbolTable(Clause clause)
    {
        // Run the symbol key traverser over the clause, to ensure that all terms have their symbol keys correctly
        // set up.
        SymbolKeyTraverser symbolKeyTraverser = new SymbolKeyTraverser(interner, symbolTable, null);
        symbolKeyTraverser.setContextChangeVisitor(symbolKeyTraverser);

        TermWalker symWalker =
            new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), symbolKeyTraverser, symbolKeyTraverser);
        symWalker.walk(clause);
    }

    /**
     * Compiles the head of a clause into an instruction listing in WAM.
     *
     * @param  expression The clause head to compile.
     *
     * @return A listing of the instructions for the clause head in the WAM instruction set.
     */
    private SizeableLinkedList<WAMInstruction> compileHead(Functor expression)
    {
        // Used to build up the results in.
        SizeableLinkedList<WAMInstruction> instructions = new SizeableLinkedList<WAMInstruction>();

        // Allocate argument registers on the body, to all functors as outermost arguments.
        // Allocate temporary registers on the body, to all terms not already allocated.
        allocateArgumentRegisters(expression);
        allocateTemporaryRegisters(expression);

        // Program instructions are generated in the same order as the registers are assigned, the postfix
        // ordering used for queries is not needed.
        QueueBasedSearchMethod<Term, Term> outInSearch = new BreadthFirstSearch<Term, Term>();
        outInSearch.reset();
        outInSearch.addStartState(expression);

        Iterator<Term> treeWalker = Searches.allSolutions(outInSearch);

        // Skip the outermost functor.
        treeWalker.next();

        // Allocate argument registers on the body, to all functors as outermost arguments.
        // Allocate temporary registers on the body, to all terms not already allocated.

        // Keep track of processing of the arguments to the outermost functor as get_val and get_var instructions
        // need to be output for variables encountered in the arguments only.
        int numOutermostArgs = expression.getArity();

        for (int j = 0; treeWalker.hasNext(); j++)
        {
            Term nextTerm = treeWalker.next();

            /*log.fine("nextTerm = " + nextTerm);*/

            // For each functor encountered: get_struc.
            if (nextTerm.isFunctor())
            {
                Functor nextFunctor = (Functor) nextTerm;
                int allocation = (Integer) symbolTable.get(nextFunctor.getSymbolKey(), SYMKEY_ALLOCATION);

                byte addrMode = (byte) ((allocation & 0xff00) >> 8);
                byte address = (byte) (allocation & 0xff);

                // Ouput a get_struc instruction, except on the outermost functor.
                /*log.fine("GET_STRUC " + interner.getFunctorName(nextFunctor) + "/" + nextFunctor.getArity() +
                    ((addrMode == REG_ADDR) ? ", X" : ", Y") + address);*/

                WAMInstruction instruction =
                    new WAMInstruction(WAMInstructionSet.GetStruc, addrMode, address,
                        interner.getFunctorFunctorName(nextFunctor), nextFunctor);
                instructions.add(instruction);

                // For each argument of the functor.
                int numArgs = nextFunctor.getArity();

                for (int i = 0; i < numArgs; i++)
                {
                    Term nextArg = nextFunctor.getArgument(i);
                    allocation = (Integer) symbolTable.get(nextArg.getSymbolKey(), SYMKEY_ALLOCATION);
                    addrMode = (byte) ((allocation & 0xff00) >> 8);
                    address = (byte) (allocation & 0xff);

                    /*log.fine("nextArg = " + nextArg);*/

                    // If it is register not seen before: unify_var.
                    // If it is register seen before: unify_val.
                    if (!seenRegisters.contains(allocation))
                    {
                        /*log.fine("UNIFY_VAR " + ((addrMode == REG_ADDR) ? "X" : "Y") + address);*/

                        seenRegisters.add(allocation);

                        instruction = new WAMInstruction(WAMInstructionSet.UnifyVar, addrMode, address, nextArg);

                        // Record the way in which this variable was introduced into the clause.
                        symbolTable.put(nextArg.getSymbolKey(), SYMKEY_VARIABLE_INTRO, VarIntroduction.Unify);
                    }
                    else
                    {
                        // Check if the variable is 'local' and use a local instruction on the first occurrence.
                        VarIntroduction introduction =
                            (VarIntroduction) symbolTable.get(nextArg.getSymbolKey(), SYMKEY_VARIABLE_INTRO);

                        if (isLocalVariable(introduction, addrMode))
                        {
                            log.fine("UNIFY_LOCAL_VAL " + ((addrMode == REG_ADDR) ? "X" : "Y") + address);

                            instruction =
                                new WAMInstruction(WAMInstructionSet.UnifyLocalVal, addrMode, address, nextArg);

                            symbolTable.put(nextArg.getSymbolKey(), SYMKEY_VARIABLE_INTRO, null);
                        }
                        else
                        {
                            /*log.fine("UNIFY_VAL " + ((addrMode == REG_ADDR) ? "X" : "Y") + address);*/

                            instruction = new WAMInstruction(WAMInstructionSet.UnifyVal, addrMode, address, nextArg);
                        }
                    }

                    instructions.add(instruction);
                }
            }
            else if (j < numOutermostArgs)
            {
                Variable nextVar = (Variable) nextTerm;
                int allocation = (Integer) symbolTable.get(nextVar.getSymbolKey(), SYMKEY_ALLOCATION);
                byte addrMode = (byte) ((allocation & 0xff00) >> 8);
                byte address = (byte) (allocation & 0xff);

                WAMInstruction instruction;

                // If it is register not seen before: get_var.
                // If it is register seen before: get_val.
                if (!seenRegisters.contains(allocation))
                {
                    /*log.fine("GET_VAR " + ((addrMode == REG_ADDR) ? "X" : "Y") + address + ", A" + j);*/

                    seenRegisters.add(allocation);

                    instruction = new WAMInstruction(WAMInstructionSet.GetVar, addrMode, address, (byte) (j & 0xff));

                    // Record the way in which this variable was introduced into the clause.
                    symbolTable.put(nextVar.getSymbolKey(), SYMKEY_VARIABLE_INTRO, VarIntroduction.Get);
                }
                else
                {
                    /*log.fine("GET_VAL " + ((addrMode == REG_ADDR) ? "X" : "Y") + address + ", A" + j);*/

                    instruction = new WAMInstruction(WAMInstructionSet.GetVal, addrMode, address, (byte) (j & 0xff));
                }

                instructions.add(instruction);
            }
        }

        return instructions;
    }

    /**
     * Compiles the body of a clause into an instruction listing in WAM.
     *
     * @param  expression  The clause body to compile.
     * @param  isFirstBody <tt>true</tt> iff this is the first body of a program clause.
     *
     * @return A listing of the instructions for the clause body in the WAM instruction set.
     */
    private SizeableLinkedList<WAMInstruction> compileBody(Functor expression, boolean isFirstBody)
    {
        // Used to build up the results in.
        SizeableLinkedList<WAMInstruction> instructions = new SizeableLinkedList<WAMInstruction>();

        // Allocate argument registers on the body, to all functors as outermost arguments.
        // Allocate temporary registers on the body, to all terms not already allocated.
        /*if (!isFirstBody)
        {
            lastAllocatedRegister = 0;
        }*/

        allocateArgumentRegisters(expression);
        allocateTemporaryRegisters(expression);

        // Loop over all of the arguments to the outermost functor.
        int numOutermostArgs = expression.getArity();

        for (int j = 0; j < numOutermostArgs; j++)
        {
            Term nextOutermostArg = expression.getArgument(j);
            int allocation = (Integer) symbolTable.get(nextOutermostArg.getSymbolKey(), SYMKEY_ALLOCATION);

            byte addrMode = (byte) ((allocation & 0xff00) >> 8);
            byte address = (byte) (allocation & 0xff);

            // On the first occurrence of a variable output a put_var.
            // On a subsequent variable occurrence output a put_val.
            if (nextOutermostArg.isVar() && !seenRegisters.contains(allocation))
            {
                seenRegisters.add(allocation);

                // The variable has been moved into an argument register.
                //varNames.remove((byte) allocation);
                //varNames.put((byte) j, ((Variable) nextOutermostArg).getName());

                /*log.fine("PUT_VAR " + ((addrMode == REG_ADDR) ? "X" : "Y") + address + ", A" + j);*/

                WAMInstruction instruction =
                    new WAMInstruction(WAMInstructionSet.PutVar, addrMode, address, (byte) (j & 0xff));
                instructions.add(instruction);

                // Record the way in which this variable was introduced into the clause.
                symbolTable.put(nextOutermostArg.getSymbolKey(), SYMKEY_VARIABLE_INTRO, VarIntroduction.Put);
            }
            else if (nextOutermostArg.isVar())
            {
                // Check if this is the last body functor in which this variable appears, it does so only in argument
                // position, and this is the first occurrence of these conditions. In which case, an unsafe put is to
                // be used.
                if (isLastBodyTermInArgPositionOnly((Variable) nextOutermostArg, expression) &&
                        (addrMode == WAMInstruction.STACK_ADDR))
                {
                    log.fine("PUT_UNSAFE_VAL " + ((addrMode == REG_ADDR) ? "X" : "Y") + address + ", A" + j);

                    WAMInstruction instruction =
                        new WAMInstruction(WAMInstructionSet.PutUnsafeVal, addrMode, address, (byte) (j & 0xff));
                    instructions.add(instruction);

                    symbolTable.put(nextOutermostArg.getSymbolKey(), SYMKEY_VAR_LAST_ARG_FUNCTOR, null);
                }
                else
                {
                    /*log.fine("PUT_VAL " + ((addrMode == REG_ADDR) ? "X" : "Y") + address + ", A" + j);*/

                    WAMInstruction instruction =
                        new WAMInstruction(WAMInstructionSet.PutVal, addrMode, address, (byte) (j & 0xff));
                    instructions.add(instruction);
                }
            }

            // When a functor is encountered, output a put_struc.
            else if (nextOutermostArg.isFunctor())
            {
                Functor nextFunctorArg = (Functor) nextOutermostArg;

                // Heap cells are to be created in an order such that no heap cell can appear before other cells that it
                // refers to. A postfix traversal of the functors in the term to compile is used to achieve this, as
                // child functors in a head will be visited first.
                // Walk over the query term in post-fix order, picking out just the functors.
                QueueBasedSearchMethod<Term, Term> postfixSearch = new PostFixSearch<Term, Term>();
                postfixSearch.reset();
                postfixSearch.addStartState(nextFunctorArg);
                postfixSearch.setGoalPredicate(new FunctorTermPredicate());

                Iterator<Term> treeWalker = Searches.allSolutions(postfixSearch);

                // For each functor encountered: put_struc.
                while (treeWalker.hasNext())
                {
                    Functor nextFunctor = (Functor) treeWalker.next();
                    allocation = (Integer) symbolTable.get(nextFunctor.getSymbolKey(), SYMKEY_ALLOCATION);
                    addrMode = (byte) ((allocation & 0xff00) >> 8);
                    address = (byte) (allocation & 0xff);

                    // Ouput a put_struc instuction, except on the outermost functor.
                    /*log.fine("PUT_STRUC " + interner.getFunctorName(nextFunctor) + "/" + nextFunctor.getArity() +
                        ((addrMode == REG_ADDR) ? ", X" : ", Y") + address);*/

                    WAMInstruction instruction =
                        new WAMInstruction(WAMInstructionSet.PutStruc, addrMode, address,
                            interner.getDeinternedFunctorName(nextFunctor.getName()), nextFunctor);
                    instructions.add(instruction);

                    // For each argument of the functor.
                    int numArgs = nextFunctor.getArity();

                    for (int i = 0; i < numArgs; i++)
                    {
                        Term nextArg = nextFunctor.getArgument(i);
                        allocation = (Integer) symbolTable.get(nextArg.getSymbolKey(), SYMKEY_ALLOCATION);
                        addrMode = (byte) ((allocation & 0xff00) >> 8);
                        address = (byte) (allocation & 0xff);

                        // If it is new variable: set_var or put_var.
                        // If it is variable or functor already seen: set_val or put_val.
                        if (nextArg.isVar() && !seenRegisters.contains(allocation))
                        {
                            seenRegisters.add(allocation);

                            /*log.fine("SET_VAR " + ((addrMode == REG_ADDR) ? "X" : "Y") + address);*/
                            instruction = new WAMInstruction(WAMInstructionSet.SetVar, addrMode, address, nextArg);

                            // Record the way in which this variable was introduced into the clause.
                            symbolTable.put(nextArg.getSymbolKey(), SYMKEY_VARIABLE_INTRO, VarIntroduction.Set);
                        }
                        else
                        {
                            // Check if the variable is 'local' and use a local instruction on the first occurrence.
                            VarIntroduction introduction =
                                (VarIntroduction) symbolTable.get(nextArg.getSymbolKey(), SYMKEY_VARIABLE_INTRO);

                            if (isLocalVariable(introduction, addrMode))
                            {
                                log.fine("SET_LOCAL_VAL " + ((addrMode == REG_ADDR) ? "X" : "Y") + address);

                                instruction =
                                    new WAMInstruction(WAMInstructionSet.SetLocalVal, addrMode, address, nextArg);

                                symbolTable.put(nextArg.getSymbolKey(), SYMKEY_VARIABLE_INTRO, null);
                            }
                            else
                            {
                                /*log.fine("SET_VAL " + ((addrMode == REG_ADDR) ? "X" : "Y") + address);*/
                                instruction = new WAMInstruction(WAMInstructionSet.SetVal, addrMode, address, nextArg);
                            }
                        }

                        instructions.add(instruction);
                    }
                }
            }
        }

        return instructions;
    }

    /**
     * Checks if a variable is appearing within the last body functor in which it occurs, and only does so within
     * argument position.
     *
     * @param  var  The variable to check.
     * @param  body The current body functor being processed.
     *
     * @return <tt>true</tt> iff this is the last body functor that the variable appears in and does so only in argument
     *         position.
     */
    private boolean isLastBodyTermInArgPositionOnly(Variable var, Functor body)
    {
        return body == symbolTable.get(var.getSymbolKey(), SYMKEY_VAR_LAST_ARG_FUNCTOR);
    }

    /**
     * For a predicate of arity n, the first n registers are used to receive its arguments in.
     *
     * <p/>Non-variable arguments appearing directly within a functor are allocated to argument registers. This means
     * that they are directly referenced from the argument registers that pass them to predicate calls, or directly
     * referenced from the argument registers that are used to read them as call arguments.
     *
     * <p/>Variables appearing as functor arguments are not allocated in this way, but are kept in registers with
     * positions higher than the number of arguments (see the {@link #allocateTemporaryRegisters(Functor)} method for
     * the allocation of temporary registers, and {@link #allocatePermanentProgramRegisters(Clause)} and
     * {@link #allocatePermanentQueryRegisters(Clause, Map)} for the allocation of permanent registers on the stack).
     * The reason for this, is that a variable can appear multiple times in an expression; it may not always be the same
     * argument. Variables are assigned to other registers, then copied into the argument registers as needed.
     *
     * <p/>Argument registers are allocated by argument position within the functor. This means that gaps will be left
     * in the numbering for variables to be copied in as needed.
     *
     * @param expression The clause head functor to allocate argument registers to.
     */
    private void allocateArgumentRegisters(Functor expression)
    {
        // Assign argument registers to functors appearing directly in the argument of the outermost functor.
        // Variables are never assigned directly to argument registers.
        int reg = 0;

        for (; reg < expression.getArity(); reg++)
        {
            Term term = expression.getArgument(reg);

            if (term instanceof Functor)
            {
                /*log.fine("X" + lastAllocatedRegister + " = " + interner.getFunctorFunctorName((Functor) term));*/

                int allocation = (reg & 0xff) | (REG_ADDR << 8);
                symbolTable.put(term.getSymbolKey(), SYMKEY_ALLOCATION, allocation);
            }
        }
    }

    /**
     * Allocates terms within a functor expression to registers. The outermost functor itself is not assigned to a
     * register in WAM (only in l0). Functors already directly assigned to argument registers will not be re-assigned by
     * this. Variables as arguments will be assigned but not as argument registers.
     *
     * @param expression The expression to walk over.
     */
    private void allocateTemporaryRegisters(Functor expression)
    {
        // Need to assign registers to the whole syntax tree, working in from the outermost functor. The outermost
        // functor itself is not assigned to a register in l3 (only in l0). Functors already directly assigned to
        // argument registers will not be re-assigned by this, variables as arguments will be assigned.
        QueueBasedSearchMethod<Term, Term> outInSearch = new BreadthFirstSearch<Term, Term>();
        outInSearch.reset();
        outInSearch.addStartState(expression);

        Iterator<Term> treeWalker = Searches.allSolutions(outInSearch);

        // Discard the outermost functor from the variable allocation.
        treeWalker.next();

        // For each term encountered: set X++ = term.
        while (treeWalker.hasNext())
        {
            Term term = treeWalker.next();

            if (symbolTable.get(term.getSymbolKey(), SYMKEY_ALLOCATION) == null)
            {
                int allocation = (lastAllocatedTempReg++ & 0xff) | (REG_ADDR << 8);
                symbolTable.put(term.getSymbolKey(), SYMKEY_ALLOCATION, allocation);
            }
        }
    }

    /**
     * Allocates stack slots where need to the variables in a program clause. The algorithm here is fairly complex.
     *
     * <p/>A clause head and first body functor are taken together as the first unit, subsequent clause body functors
     * are taken as subsequent units. A variable appearing in more than one unit is said to be permanent, and must be
     * stored on the stack, rather than a register, otherwise the register that it occupies may be overwritten by calls
     * to subsequent units. These variable are called permanent, which really means that they are local variables on the
     * call stack.
     *
     * <p/>In addition to working out which variables are permanent, the variables are also ordered by reverse position
     * of last body of occurrence, and assigned to allocation slots in this order. The number of permanent variables
     * remaining at each body call is also calculated and recorded against the body functor in the symbol table using
     * column {@link #SYMKEY_PERM_VARS_REMAINING}. This allocation ordering of the variables and the count of the number
     * remaining are used to implement environment trimming.
     *
     * @param clause The clause to allocate registers for.
     */
    private void allocatePermanentProgramRegisters(Clause clause)
    {
        // A bag to hold variable occurrence counts in.
        Map<Variable, Integer> variableCountBag = new HashMap<Variable, Integer>();

        // A mapping from variables to the body number in which they appear last.
        Map<Variable, Integer> lastBodyMap = new HashMap<Variable, Integer>();

        // Get the occurrence counts of variables in all clauses after the initial head and first body grouping.
        // In the same pass, pick out which body variables last occur in.
        if ((clause.getBody() != null))
        {
            for (int i = clause.getBody().length - 1; i >= 1; i--)
            {
                Set<Variable> groupVariables = TermUtils.findFreeVariables(clause.getBody()[i]);

                // Add all their counts to the bag and update their last occurrence positions.
                for (Variable variable : groupVariables)
                {
                    Integer count = variableCountBag.get(variable);
                    variableCountBag.put(variable, (count == null) ? 1 : (count + 1));

                    if (!lastBodyMap.containsKey(variable))
                    {
                        lastBodyMap.put(variable, i);
                    }
                }
            }
        }

        // Get the set of variables in the head and first clause body argument.
        Set<Variable> firstGroupVariables = new HashSet<Variable>();

        if (clause.getHead() != null)
        {
            Set<Variable> headVariables = TermUtils.findFreeVariables(clause.getHead());
            firstGroupVariables.addAll(headVariables);
        }

        if ((clause.getBody() != null) && (clause.getBody().length > 0))
        {
            Set<Variable> firstArgVariables = TermUtils.findFreeVariables(clause.getBody()[0]);
            firstGroupVariables.addAll(firstArgVariables);
        }

        // Add their counts to the bag, and set their last positions of occurrence as required.
        for (Variable variable : firstGroupVariables)
        {
            Integer count = variableCountBag.get(variable);
            variableCountBag.put(variable, (count == null) ? 1 : (count + 1));

            if (!lastBodyMap.containsKey(variable))
            {
                lastBodyMap.put(variable, 0);
            }
        }

        // Sort the variables by reverse position of last occurrence.
        List<Map.Entry<Variable, Integer>> lastBodyList =
            new ArrayList<Map.Entry<Variable, Integer>>(lastBodyMap.entrySet());
        Collections.sort(lastBodyList, new Comparator<Map.Entry<Variable, Integer>>()
            {
                public int compare(Map.Entry<Variable, Integer> o1, Map.Entry<Variable, Integer> o2)
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

        // Holds counts of permanent variable last appearances against the body in which they last occur.
        int[] permVarsRemainingCount = new int[(clause.getBody() != null) ? clause.getBody().length : 0];

        // Search the count bag for all variable occurrences greater than one, and assign them to stack slots.
        // The variables are examined by reverse position of last occurrence, to ensure that later variables
        // are assigned to lower permanent allocation slots for environment trimming purposes.
        for (Map.Entry<Variable, Integer> entry : lastBodyList)
        {
            Variable variable = entry.getKey();
            Integer count = variableCountBag.get(variable);
            int body = entry.getValue();

            if ((count != null) && (count > 1))
            {
                log.fine("Variable " + variable + " is permanent, count = " + count);

                int allocation = (numPermanentVars++ & (0xff)) | (STACK_ADDR << 8);
                symbolTable.put(variable.getSymbolKey(), SYMKEY_ALLOCATION, allocation);

                permVarsRemainingCount[body]++;
            }
        }

        // Roll up the permanent variable remaining counts from the counts of last position of occurrence and
        // store the count of permanent variables remaining against the body.
        int permVarsRemaining = 0;

        for (int i = permVarsRemainingCount.length - 1; i >= 0; i--)
        {
            symbolTable.put(clause.getBody()[i].getSymbolKey(), SYMKEY_PERM_VARS_REMAINING, permVarsRemaining);
            permVarsRemaining += permVarsRemainingCount[i];
        }
    }

    /**
     * Allocates stack slots to all free variables in a query clause.
     *
     * <p/>At the end of processing a query its variable bindings are usually printed. For this reason all free
     * variables in a query are marked as permanent variables on the call stack, to ensure that they are preserved.
     *
     * @param clause   The clause to allocate registers for.
     * @param varNames A map of permanent variables to variable names to record the allocations in.
     */
    private void allocatePermanentQueryRegisters(Clause clause, Map<Byte, Integer> varNames)
    {
        // Allocate local variable slots for all variables in a query.
        QueryRegisterAllocatingVisitor allocatingVisitor =
            new QueryRegisterAllocatingVisitor(symbolTable, varNames, null);

        PositionalTermTraverser positionalTraverser = new PositionalTermTraverserImpl();
        positionalTraverser.setContextChangeVisitor(allocatingVisitor);

        TermWalker walker =
            new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), positionalTraverser, allocatingVisitor);

        walker.walk(clause);
    }

    /**
     * Gather information about variable counts and positions of occurrence of constants and variable within a clause.
     *
     * @param clause The clause to check the variable occurrence and position of occurrence within.
     */
    private void gatherPositionAndOccurrenceInfo(Clause clause)
    {
        PositionalTermTraverser positionalTraverser = new PositionalTermTraverserImpl();
        PositionAndOccurrenceVisitor positionAndOccurrenceVisitor =
            new PositionAndOccurrenceVisitor(interner, symbolTable, positionalTraverser);
        positionalTraverser.setContextChangeVisitor(positionAndOccurrenceVisitor);

        TermWalker walker =
            new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), positionalTraverser,
                positionAndOccurrenceVisitor);

        walker.walk(clause);
    }

    /**
     * Determines whether a variable is local, that is, it may only exist on the stack. When variables are introduced
     * into clauses, the way in which they are introduced is recorded using the {@link VarIntroduction} enum. When a
     * variable is being written to the heap for the first time, this check may be used to see if a 'local' variant of
     * an instruction is needed, in order to globalize the variable on the heap.
     *
     * <p/>The conditions for a variable being deemed local are:
     *
     * <ul>
     * <li>For a permanent variable, not initialized in this clause with set_var or unify_var instruction.</li>
     * <li>For a temporary variable, not initialized in this clause with set_var or unify_var or put_var instruction.
     * </li>
     * </ul>
     *
     * @param  introduction The type of instruction that introduced the variable into the clause.
     * @param  addrMode     The addressing mode of the variable, permanent variables are stack allocated.
     *
     * @return <tt>true</tt> iff the variable is unsafe.
     */
    private boolean isLocalVariable(VarIntroduction introduction, byte addrMode)
    {
        if (WAMInstruction.STACK_ADDR == addrMode)
        {
            return (introduction == VarIntroduction.Get) || (introduction == VarIntroduction.Put);
        }
        else
        {
            return introduction == VarIntroduction.Get;
        }
    }

    /**
     * Pretty prints a compiled predicate.
     *
     * @param predicate The compiled predicate to pretty print.
     */
    private void displayCompiledPredicate(WAMCompiledPredicate predicate)
    {
        // Pretty print the clause.
        StringBuffer result = new StringBuffer();

        WAMCompiledTermsPrintingVisitor displayVisitor =
            new WAMCompiledPredicatePrintingVisitor(interner, symbolTable, result);

        PositionalTermTraverser positionalTraverser = new PositionalTermTraverserImpl();
        displayVisitor.setPositionalTraverser(positionalTraverser);
        positionalTraverser.setContextChangeVisitor(displayVisitor);

        TermWalker walker =
            new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), positionalTraverser, displayVisitor);

        walker.walk(predicate);

        log.fine(result.toString());
    }

    /**
     * Pretty prints a compiled query.
     *
     * @param query The compiled query to pretty print.
     */
    private void displayCompiledQuery(WAMCompiledQuery query)
    {
        // Pretty print the clause.
        StringBuffer result = new StringBuffer();

        WAMCompiledTermsPrintingVisitor displayVisitor =
            new WAMCompiledQueryPrintingVisitor(interner, symbolTable, result);

        PositionalTermTraverser positionalTraverser = new PositionalTermTraverserImpl();
        displayVisitor.setPositionalTraverser(positionalTraverser);
        positionalTraverser.setContextChangeVisitor(displayVisitor);

        TermWalker walker =
            new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), positionalTraverser, displayVisitor);

        walker.walk(query);

        log.fine(result.toString());
    }

    /**
     * QueryRegisterAllocatingVisitor visits named variables in a query, and if they are not already allocated to a
     * permanent stack slot, allocates them one. All named variables in queries are stack allocated, so that they are
     * preserved on the stack at the end of the query. Anonymous variables in queries are singletons, and not included
     * in the query results, so can be temporary.
     */
    public class QueryRegisterAllocatingVisitor extends DelegatingAllTermsVisitor
    {
        /** The symbol table. */
        private final SymbolTable<Integer, String, Object> symbolTable;

        /** Holds a map of permanent variables to variable names to record the allocations in. */
        private final Map<Byte, Integer> varNames;

        /**
         * Creates a query variable allocator.
         *
         * @param symbolTable The symbol table.
         * @param varNames    A map of permanent variables to variable names to record the allocations in.
         * @param delegate    The term visitor that this delegates to.
         */
        public QueryRegisterAllocatingVisitor(SymbolTable<Integer, String, Object> symbolTable,
            Map<Byte, Integer> varNames, AllTermsVisitor delegate)
        {
            super(delegate);
            this.symbolTable = symbolTable;
            this.varNames = varNames;
        }

        /**
         * {@inheritDoc}
         *
         * <p/>Allocates unallocated variables to stack slots.
         */
        public void visit(Variable variable)
        {
            if (symbolTable.get(variable.getSymbolKey(), SYMKEY_ALLOCATION) == null)
            {
                if (variable.isAnonymous())
                {
                    log.fine("Query variable " + variable + " is temporary.");

                    int allocation = (lastAllocatedTempReg++ & (0xff)) | (REG_ADDR << 8);
                    symbolTable.put(variable.getSymbolKey(), SYMKEY_ALLOCATION, allocation);
                    varNames.put((byte) allocation, variable.getName());
                }
                else
                {
                    log.fine("Query variable " + variable + " is permanent.");

                    int allocation = (numPermanentVars++ & (0xff)) | (STACK_ADDR << 8);
                    symbolTable.put(variable.getSymbolKey(), SYMKEY_ALLOCATION, allocation);
                    varNames.put((byte) allocation, variable.getName());
                }
            }

            super.visit(variable);
        }
    }

    /**
     * PositionAndOccurrenceVisitor visits a clause to gather information about the positions in which components of the
     * clause appear.
     *
     * <p/>For variables, the following information is gathered:
     *
     * <ol>
     * <li>A count of the number of times the variable occurs in the clause (singleton detection).</li>
     * <li>A flag indicating that variable only ever appears in non-argument positions.</li>
     * <li>The last functor body in the clause in which a variable appears, provided it only does so in argument
     * position.</li>
     * </ol>
     *
     * <p/>For constants, the following information is gathered:
     *
     * <ol>
     * <li>A flag indicating the constant only ever appears in non-argument positions.</li>
     * </ol>
     */
    public class PositionAndOccurrenceVisitor extends BasePositionalVisitor
    {
        /** Holds the current top-level body functor. <tt>null</tt> when traversing the head. */
        private Functor topLevelBodyFunctor;

        /**
         * Creates a positional visitor.
         *
         * @param interner    The name interner.
         * @param symbolTable The compiler symbol table.
         * @param traverser   The positional context traverser.
         */
        public PositionAndOccurrenceVisitor(VariableAndFunctorInterner interner,
            SymbolTable<Integer, String, Object> symbolTable, PositionalTermTraverser traverser)
        {
            super(interner, symbolTable, traverser);
        }

        /**
         * {@inheritDoc}
         *
         * <p/>Counts variable occurrences and detects if the variable ever appears in an argument position.
         */
        protected void enterVariable(Variable variable)
        {
            // Initialize the count to one or add one to an existing count.
            Integer count = (Integer) symbolTable.get(variable.getSymbolKey(), SYMKEY_VAR_OCCURRENCE_COUNT);
            count = (count == null) ? 1 : (count + 1);
            symbolTable.put(variable.getSymbolKey(), SYMKEY_VAR_OCCURRENCE_COUNT, count);

            /*log.fine("Variable " + variable + " has count " + count + ".");*/

            // Get the nonArgPosition flag, or initialize it to true.
            Boolean nonArgPositionOnly = (Boolean) symbolTable.get(variable.getSymbolKey(), SYMKEY_VAR_NON_ARG);
            nonArgPositionOnly = (nonArgPositionOnly == null) ? true : nonArgPositionOnly;

            // Clear the nonArgPosition flag is the variable occurs in an argument position.
            nonArgPositionOnly = inTopLevelFunctor() ? false : nonArgPositionOnly;
            symbolTable.put(variable.getSymbolKey(), SYMKEY_VAR_NON_ARG, nonArgPositionOnly);

            /*log.fine("Variable " + variable + " nonArgPosition is " + nonArgPositionOnly + ".");*/

            // If in an argument position, record the parent body functor against the variable, as potentially being
            // the last one it occurs in, in a purely argument position.
            // If not in an argument position, clear any parent functor recorded against the variable, as this current
            // last position of occurrence is not purely in argument position.
            if (inTopLevelFunctor())
            {
                symbolTable.put(variable.getSymbolKey(), SYMKEY_VAR_LAST_ARG_FUNCTOR, topLevelBodyFunctor);
            }
            else
            {
                symbolTable.put(variable.getSymbolKey(), SYMKEY_VAR_LAST_ARG_FUNCTOR, null);
            }
        }

        /**
         * {@inheritDoc}
         *
         * <p/>Checks if a constant ever appears in an argument position.
         *
         * <p/>Sets the 'inTopLevelFunctor' flag, whenever the traversal is directly within a top-level functors
         * arguments. This is set at the end, so that subsequent calls to this will pick up the state of this flag at
         * the point immediately below a top-level functor.
         */
        protected void enterFunctor(Functor functor)
        {
            /*log.fine("Functor: " + functor.getName() + " <- " + symbolTable.getSymbolKey(functor.getName()));*/

            // Only check position of occurrence for constants.
            if (functor.getArity() == 0)
            {
                // Get the nonArgPosition flag, or initialize it to true.
                Boolean nonArgPositionOnly = (Boolean) symbolTable.get(functor.getName(), SYMKEY_FUNCTOR_NON_ARG);
                nonArgPositionOnly = (nonArgPositionOnly == null) ? true : nonArgPositionOnly;

                // Clear the nonArgPosition flag is the variable occurs in an argument position.
                nonArgPositionOnly = inTopLevelFunctor() ? false : nonArgPositionOnly;
                symbolTable.put(functor.getName(), SYMKEY_FUNCTOR_NON_ARG, nonArgPositionOnly);

                /*log.fine("Constant " + functor + " nonArgPosition is " + nonArgPositionOnly + ".");*/
            }

            // Keep track of the current top-level body functor.
            if (traverser.isTopLevel() && !traverser.isInHead())
            {
                topLevelBodyFunctor = functor;
            }
        }

        /**
         * Checks if the current position is immediately within a top-level functor.
         *
         * @return <tt>true</tt> iff the current position is immediately within a top-level functor.
         */
        private boolean inTopLevelFunctor()
        {
            PositionalContext parentContext = traverser.getParentContext();

            return (parentContext != null) ? parentContext.isTopLevel() : false;
        }
    }
}
