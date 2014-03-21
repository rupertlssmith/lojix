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
package com.thesett.aima.logic.fol.vam.vamai;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.ClauseVisitor;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorVisitor;
import com.thesett.aima.logic.fol.IntegerType;
import com.thesett.aima.logic.fol.IntegerTypeVisitor;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableVisitor;
import com.thesett.aima.logic.fol.compiler.AnnotatingVisitor;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverser;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Atom;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Call;
import com.thesett.aima.logic.fol.vam.vamai.instructions.FirstTemp;
import com.thesett.aima.logic.fol.vam.vamai.instructions.FirstVar;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Goal;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Int;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NextTemp;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NextVar;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NoGoal;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Struct;
import com.thesett.aima.logic.fol.vam.vamai.instructions.VAMAIInstruction;
import com.thesett.common.util.doublemaps.SymbolKey;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * VAMAIInstructionGeneratingVisitor uses a {@link com.thesett.aima.logic.fol.compiler.PositionalTermTraverserImpl} to
 * provide enough positional context information about terms within a term tree, to generate VAMAI instruction codes
 * from.
 *
 * <p/>This visitor should be used with a depth first ordered search, to output the instruction codes in the correct
 * order.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Generate VAMAI instructions for a clause.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMAIInstructionGeneratingVisitor implements FunctorVisitor, VariableVisitor, ClauseVisitor,
    IntegerTypeVisitor
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(VAMAIInstructionGeneratingVisitor.class.getName()); */

    /** This is used to keep track of variables as they are seen. */
    private Set<Integer> seenVariables = new HashSet<Integer>();

    /** The basic traverser used to provide position context to visited terms. */
    private PositionalTermTraverser traverser;

    /** The name interner. */
    private VariableAndFunctorInterner interner;

    /** The symbol table. */
    private SymbolTable<Integer, String, Object> symbolTable;

    /** Used to append the generated instructions onto. */
    List<VAMAIInstruction> instructions;

    /**
     * Creates a new instruction generating visitor over term trees.
     *
     * @param interner     The name interner.
     * @param symbolTable  The symbol table for symbol fields.
     * @param traverser    The traverser to provide position context information about terms.
     * @param instructions The instruction listing to append generated instructions to.
     */
    public VAMAIInstructionGeneratingVisitor(VariableAndFunctorInterner interner,
        SymbolTable<Integer, String, Object> symbolTable, PositionalTermTraverser traverser,
        List<VAMAIInstruction> instructions)
    {
        this.traverser = traverser;
        this.interner = interner;
        this.instructions = instructions;
        this.symbolTable = symbolTable;
    }

    /** {@inheritDoc} */
    public void visit(Clause clause)
    {
        // Clauses with no bodies have no goals to execute. They are always true.
        if (traverser.isLeavingContext())
        {
            /*log.fine("c-nogoal");*/
            instructions.add(new NoGoal());
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Top-level functors in a body generate calls to their implementations as clauses. Other functor occurences
     * describe nested structures.
     *
     * <p/>Calls to goals withina clause body are terminated by the 'call' instruction, except for the last call which
     * uses a 'nogoal' instruction to continue processing the next clause, rather then a 'lastcall' instruction.
     */
    public void visit(Functor functor)
    {
        String mode = traverser.isInHead() ? "h" : "g";
        String nameArity = interner.getFunctorName(functor) + "/" + interner.getFunctorArity(functor);

        // Create instructions when visiting a functor rather than transitioning into or out of its context.
        if (!traverser.isContextChange())
        {
            if (traverser.isTopLevel() && !traverser.isInHead())
            {
                /*log.fine("c-goal " + nameArity);*/
                instructions.add(new Goal(functor.getName(), 0));
            }
            else if (functor.isAtom())
            {
                /*log.fine(mode + "-atom " + nameArity);*/
                instructions.add(new Atom(functor.getName()));
            }
            else
            {
                /*log.fine(mode + "-struct " + nameArity);*/
                instructions.add(new Struct(functor.getName()));
            }
        }

        // Upon leaving the context of a top-level functor create instructions to complete the call. The final call
        // is a 'nogoal' instruction to continue with the next clause.
        if (traverser.isLeavingContext())
        {
            if (traverser.isTopLevel() && !traverser.isInHead())
            {
                /*log.fine("c-call");*/
                instructions.add(new Call());
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Variables appearing only in the head of a clause, or in the body in fully deterministic positions can be
     * considered temporary variables. Other variables are considered as full non-temporary variables.
     *
     * <p/>The first time a variable is encountered the 'first' variant variable instruction is used to estblish is, on
     * subsequent times the 'next' variant is used.
     *
     * <p/>All anonymous variables are singular and are handled by the 'void' instruction.
     */
    public void visit(Variable variable)
    {
        // Create instructions when visiting a variable, rather than transitioning into or out of its context.
        if (!traverser.isContextChange())
        {
            String mode = traverser.isInHead() ? "h" : "g";
            SymbolKey symbolKey = variable.getSymbolKey();
            AnnotatingVisitor.VarDomain varDomain =
                (AnnotatingVisitor.VarDomain) symbolTable.get(symbolKey, AnnotatingVisitor.VARIABLE_DOMAIN);

            if (variable.isAnonymous())
            {
                /*log.fine(mode + "-void ");*/
                instructions.add(new com.thesett.aima.logic.fol.vam.vamai.instructions.Void());
            }
            else if (!seenVariables.contains(variable.getName()))
            {
                seenVariables.add(variable.getName());

                if (varDomain.isTemporary())
                {
                    /*log.fine(mode + "-fsttmp " + interner.getVariableName(variable));*/
                    instructions.add(new FirstTemp(variable.getName()));
                }
                else
                {
                    /*log.fine(mode + "-fstvar " + interner.getVariableName(variable));*/
                    instructions.add(new FirstVar(variable.getName(), (byte) 0, (short) 0, false, false));
                }
            }
            else
            {
                if (varDomain.isTemporary())
                {
                    /*log.fine(mode + "-nexttmp " + interner.getVariableName(variable));*/
                    instructions.add(new NextTemp(variable.getName()));
                }
                else
                {
                    /*log.fine(mode + "-nextvar " + interner.getVariableName(variable));*/
                    instructions.add(new NextVar(variable.getName(), (byte) 0, (short) 0, false, false));
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void visit(IntegerType literal)
    {
        // Create instructions when visiting a literal, rather than transitioning into or out of its context.
        if (!traverser.isContextChange())
        {
            String mode = traverser.isInHead() ? "h" : "g";

            /*log.fine(mode + "-int ");*/
            instructions.add(new Int(literal.intValue()));
        }
    }

    /** {@inheritDoc} */
    public void visit(Term term)
    {
    }
}
