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

import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableBindingContext;

/**
 * StackVariable is a {@link Variable} that has been assigned an offset within a stack frame in which it may hold its
 * binding.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Associate a name with an optional substitution.
 * <tr><td> Allow a variable to be marked as anonymous.
 * <tr><td> Provide a position relative to a stack frame where this variable can store its binding.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StackVariable extends Variable
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(StackVariable.class.getName()); */

    /** Used to hold the stack frame offset for this variable. */
    protected int position;

    /** Holds the binding context supplier used to designate where this variables bindings are stored. */
    VariableBindingContextSupplier contextSupplier;

    /**
     * Creates a new variable, the name does not have to be unique and the the variable can be free by supplying a null
     * substitution. The variables allocated position on the stack frame must be specified.
     *
     * @param name            The name of the variable.
     * @param substitution    The substitution to create the variable with, use <tt>null</tt> for a free variable.
     * @param anonymous       Set to <tt>true</tt> if the variable is to be anonymous.
     * @param position        The variables allocated position on the stack frame.
     * @param contextSupplier The supplier of the variable binding context for this variable.
     */
    public StackVariable(int name, Term substitution, boolean anonymous, int position,
        VariableBindingContextSupplier contextSupplier)
    {
        super(name, substitution, anonymous);

        this.position = position;
        this.contextSupplier = contextSupplier;
    }

    /**
     * Provides this variables binding context.
     *
     * @return This variables binding context, or <tt>null</tt> if none has been established.
     */
    public VariableBindingContext<Variable> getBindingContext()
    {
        return contextSupplier.getBindingContext();
    }

    /**
     * Provides the storage cell for the specified variable. Some types of variable may defer their storage onto a
     * storage cell other than themselves, other variable types may simply return themselves as their own storage cells.
     *
     * @param  variable The variable to get the storage cell for.
     *
     * @return The storage cell where the specified variable sets its bindings, or <tt>null</tt> if none has been
     *         established.
     */
    public Variable getStorageCell(Variable variable)
    {
        VariableBindingContext<Variable> context = getBindingContext();

        if (context == null)
        {
            return null;
        }
        else
        {
            return context.getStorageCell(this);
        }
    }

    /**
     * Provides this variables allocated stack frame position.
     *
     * @return The stack from position of this variable.
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable. When the variable is free, the variable term itself is returned.
     *
     * @return The term itself, or the assigned value of this variable.
     */
    public Term getValue()
    {
        Variable storageCell = getStorageCell(this);

        if (storageCell == null)
        {
            return this;
        }
        else
        {
            return storageCell.getValue();
        }
    }

    /**
     * Binds this variable to the specified value.
     *
     * @param term The value to bind this variable to.
     */
    public void setSubstitution(Term term)
    {
        Term termToBindTo = term;

        // When binding against a variable, always bind to its storage cell and not the variable itself.
        if (termToBindTo instanceof Variable)
        {
            Variable variableToBindTo = (Variable) term;
            termToBindTo = variableToBindTo.getStorageCell(variableToBindTo);
        }

        getStorageCell(this).setSubstitution(termToBindTo);
    }

    /** Frees all assigned variables in the term, leaving them unnassigned. */
    public void free()
    {
        getStorageCell(this).setSubstitution(null);
    }

    /**
     * Reports whether or not this variable is bound to a value.
     *
     * @return <tt>true</tt> if this variable has been assigned a value, <tt>false</tt> otherwise.
     */
    public boolean isBound()
    {
        VariableBindingContext<Variable> context = getBindingContext();

        // The variable can only be bound if it has a binding context and is bound in that context.
        return (context != null) && context.getStorageCell(this).isBound();
    }

    /**
     * Makes a clone of the term, converting its variables to refer directly to their storage cells.
     *
     * @return A copy of this term, with entirely independent variables to the term it was copied from.
     */
    public Variable queryConversion()
    {
        /*log.fine("public Variable queryConversion(): called");*/

        return getStorageCell(this);
    }

    /**
     * Creates a string representation of this variable, mostly used for debugging purposes.
     *
     * @return A string representation of this variable.
     */
    public String toString()
    {
        return "StackVariable: [ name = " + name + ", position = " + position + ", substitution = " +
            ((isBound()) ? "instantiated" : "null") + " ]";
    }

    /**
     * Pretty prints a term relative to the symbol namings provided by the specified interner.
     *
     * @param interner      The interner use to provide symbol names.
     * @param printBindings <tt>true</tt> if variable binding values should be printed, <tt>false</tt> if just the
     *                      variables name without any binding should be printed.
     *
     * @return A pretty printed string containing the term.
     */
    /*public String toString(VariableAndFunctorInterner interner, boolean printBindings)
    {
        return toString();
    }*/
}
