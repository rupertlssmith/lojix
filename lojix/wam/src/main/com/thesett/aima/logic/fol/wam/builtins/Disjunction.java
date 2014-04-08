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
package com.thesett.aima.logic.fol.wam.builtins;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorName;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.wam.compiler.DefaultBuiltIn;
import static com.thesett.aima.logic.fol.wam.compiler.InstructionCompiler.SYMKEY_PERM_VARS_REMAINING;
import com.thesett.aima.logic.fol.wam.compiler.WAMInstruction;
import com.thesett.aima.logic.fol.wam.compiler.WAMLabel;
import com.thesett.common.util.SizeableLinkedList;

/**
 * Disjunction implements the Prolog disjunction operator ';' that sets up multiple choice points potentially leading to
 * multiple solutions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Implement the disjunction operator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Disjunction extends BaseBuiltIn
{
    /**
     * Creates a cut built-in to implement the specified functor.
     *
     * @param functor        The functor to implement as a built-in.
     * @param defaultBuiltIn The default built in, for standard compilation and interners and symbol tables.
     */
    public Disjunction(Functor functor, DefaultBuiltIn defaultBuiltIn)
    {
        super(functor, defaultBuiltIn);
    }

    /** {@inheritDoc} */
    public SizeableLinkedList<WAMInstruction> compileBodyArguments(Functor functor, boolean isFirstBody)
    {
        System.out.println("Compiling disjunction.");

        SizeableLinkedList<WAMInstruction> result = new SizeableLinkedList<WAMInstruction>();
        SizeableLinkedList<WAMInstruction> instructions;

        Term[] expressions = functor.getArguments();

        FunctorName cfn = new FunctorName("continue", 0);
        WAMLabel continueLabel = new WAMLabel(cfn, 0);

        for (int i = 0; i < expressions.length; i++)
        {
            Functor expression = (Functor) expressions[i];

            boolean isFirst = i == 0;
            boolean isLast = i == (expressions.length - 1);

            // Labels the entry point to each choice point.
            //FunctorName fn = interner.getFunctorFunctorName(clause.getHead());
            FunctorName fn = new FunctorName("test", 0);
            WAMLabel entryLabel = new WAMLabel(fn, i);

            // Label for the entry point to the next choice point, to backtrack to.
            WAMLabel retryLabel = new WAMLabel(fn, i + 1);

            if (isFirst && !isLast)
            {
                // try me else.
                result.add(new WAMInstruction(entryLabel, WAMInstruction.WAMInstructionSet.TryMeElse, retryLabel));
            }
            else if (!isFirst && !isLast)
            {
                // retry me else.
                result.add(new WAMInstruction(entryLabel, WAMInstruction.WAMInstructionSet.RetryMeElse, retryLabel));
            }
            else if (isLast)
            {
                // trust me.
                result.add(new WAMInstruction(entryLabel, WAMInstruction.WAMInstructionSet.TrustMe));
            }

            Integer permVarsRemaining =
                (Integer) defaultBuiltIn.getSymbolTable().get(expression.getSymbolKey(), SYMKEY_PERM_VARS_REMAINING);

            // Select a non-default built-in implementation to compile the functor with, if it is a built-in.
            BuiltIn builtIn;

            if (expression instanceof BuiltIn)
            {
                builtIn = (BuiltIn) expression;
            }
            else
            {
                builtIn = defaultBuiltIn;
            }

            // The 'isFirstBody' parameter is only set to true, when this is the first functor of a rule.
            instructions = builtIn.compileBodyArguments(expression, false);
            result.addAll(instructions);

            // Call the body. The number of permanent variables remaining is specified for environment trimming.
            instructions = builtIn.compileBodyCall(expression, false, false, false, 0 /*permVarsRemaining*/);
            result.addAll(instructions);

            // Proceed if this disjunctive branch completes successfully. This does not need to be done for the last
            // branch, as the continuation point will come immediately after.
            if (!isLast)
            {
                result.add(new WAMInstruction(null, WAMInstruction.WAMInstructionSet.Continue, continueLabel));
            }
        }

        result.add(new WAMInstruction(continueLabel, WAMInstruction.WAMInstructionSet.NoOp));

        return result;
    }

    /** {@inheritDoc} */
    public SizeableLinkedList<WAMInstruction> compileBodyCall(Functor expression, boolean isFirstBody,
        boolean isLastBody, boolean chainRule, int permVarsRemaining)
    {
        return new SizeableLinkedList<WAMInstruction>();
    }
}
