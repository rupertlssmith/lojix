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
package com.thesett.aima.logic.fol.prolog;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.LogicCompilerObserver;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.TermTransformer;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.prolog.expressions.BuiltInExpressionTransform;
import com.thesett.common.parsing.SourceCodeException;

/**
 * PrologCompiler implements a translation from {@link Clause}s to {@link PrologCompiledClause}s.
 *
 * <p/>This compiler translates all free variables in domain clauses into {@link StackVariable}s with assigned storage
 * locations relative to a stack frame. The clause is then set up as the binding context supplier to the variable. When
 * evaluating against a clause a binding context accepting stack variables needs to be set up on it, binding stack
 * variables will then defer the storage of their bindings onto the stack frame location rather than the variable
 * itself. This means that the domain clause can have variable bindings relative to a particular stack frame, and
 * evaluating the domain clause in a different stack frame context will allow a different binding of its variables.
 *
 * <p/>The compiler also replaces any occurences of built-in expressions with their implementations.
 *
 * <p/>Query clauses are not transformed with stack variables. There is only one query at the top level of any execution
 * chain, so it does not need to be re-used in the way that domain clauses do.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Assign stack frame storage locations for variables.
 *     <td> {@link StackVariable}, {@link StackVariableTransform}.
 * <tr><td> Replace built in expressions with their implementations.
 *     <td> {@link BuiltInExpressionTransform}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologCompiler implements LogicCompiler<Clause, PrologCompiledClause, PrologCompiledClause>
{
    /** Used to intern variables and functors with. */
    private VariableAndFunctorInterner interner;

    /** Holds the compiler output observer. */
    private LogicCompilerObserver<PrologCompiledClause, PrologCompiledClause> observer;

    /**
     * Creates a new compiler using the specified interner.
     *
     * @param interner The interner to use.
     */
    public PrologCompiler(VariableAndFunctorInterner interner)
    {
        this.interner = interner;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Compiles a clause by replacing all free variable occurrences within domain clauses with stack storage
     * locations. The clause is set up as the binding context supplier to these variables. Query clauses are not set up
     * with stack variables, as the query can have its variables bound directly.
     */
    public void compile(Sentence<Clause> clauseSentence) throws SourceCodeException
    {
        Clause clause = clauseSentence.getT();
        PrologCompiledClause result = new PrologCompiledClause(clause.getHead(), clause.getBody());

        // Create stack frame slots for all variables in a program.
        if (!clause.isQuery())
        {
            StackVariableTransform stackVariableTransform = new StackVariableTransform(0, result);
            result = (PrologCompiledClause) result.acceptTransformer(stackVariableTransform);

            // Set the required stack frame size on the compiled clause.
            result.setStackSize(stackVariableTransform.offset);
        }

        // Apply the built-in transformation to map any built-ins to their implementations.
        TermTransformer builtInTransformation = new BuiltInExpressionTransform(interner);
        result = (PrologCompiledClause) result.acceptTransformer(builtInTransformation);

        // Return the compiled version of the clause.
        if (clause.isQuery())
        {
            observer.onQueryCompilation(result);
        }
        else
        {
            observer.onCompilation(result);
        }
    }

    /** {@inheritDoc} */
    public void setCompilerObserver(LogicCompilerObserver<PrologCompiledClause, PrologCompiledClause> observer)
    {
        this.observer = observer;
    }

    /** {@inheritDoc} */
    public void endScope()
    {
    }
}
