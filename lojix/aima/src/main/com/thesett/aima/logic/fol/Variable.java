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
package com.thesett.aima.logic.fol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Variable represents a variable in first order logic. A variable has a name, and can either be free (unassigned) or be
 * bound (assigned or unified against) the value of another term in the language. The pure terminology of the language
 * does not cover variable assignments or substitutions, these are part of its operational semantics. Room is made
 * available for subsitutions in this class because, as stated in the comments for {@link Term}, the purpose of these
 * definitions is to provide a minimal semantic framework for evaluating first order logic as well as providing an
 * abstract syntax tree for it.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <tr> Responsibilities
 * <tr><td> Associate a name with an optional substitution.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Variable extends BaseTerm implements Term, VariableBindingContext<Variable>, Comparable<Variable>
{
    /** A generator of unique variable id's. */
    private static AtomicInteger varId = new AtomicInteger();

    /**
     * Holds the name of the variable. This is an integer that refers to an interned string that provides the textual
     * name. An integer representation is usd for fast comparisons.
     */
    protected int name;

    /** The term against which this variable is currently unified. <tt>null</tt> when the variable is free. */
    protected Term substitution;

    /** Used to indicate that a variable is anonymous. */
    private boolean anonymous;

    /** Holds a unique id for the variable. */
    protected int id = varId.getAndIncrement();

    /**
     * Creates a new variable, the name does not have to be unique and the the variable can be free by supplying a null
     * substitution.
     *
     * @param name         The name of the variable.
     * @param substitution The substitution to create the variable with, use <tt>null</tt> for a free variable.
     * @param anonymous    <tt>true</tt> if the variable is anonymous.
     */
    public Variable(int name, Term substitution, boolean anonymous)
    {
        this.name = name;
        this.substitution = substitution;
        this.anonymous = anonymous;
    }

    /**
     * Provides this variables binding context.
     *
     * @return This variables binding context.
     */
    public VariableBindingContext<Variable> getBindingContext()
    {
        return this;
    }

    /**
     * Provides the storage cell for the specified variable. Some types of variable may defer their storage onto a
     * storage cell other than themselves, other variable types may simply return themselves as their own storage cells.
     *
     * @param  variable The variable to get the storage cell for.
     *
     * @return The storage cell where the specified variable sets its bindings.
     */
    public Variable getStorageCell(Variable variable)
    {
        return this;
    }

    /**
     * Reports whether or not this term is a number.
     *
     * @return <tt>false</tt> always.
     */
    public boolean isNumber()
    {
        Term t = getValue();

        return (t != this) && t.isNumber();
    }

    /**
     * Reports whether or not this term is a variable.
     *
     * @return <tt>true</tt> always.
     */
    public boolean isVar()
    {
        return true;
    }

    /**
     * Reports whether or not this term is constant (contains no variables). A variable is constant if it has been
     * assigned a value which is constant, and not otherwise.
     *
     * @return <tt>true</tt> if this term is constant, <tt>false</tt> otherwise.
     */
    public boolean isConstant()
    {
        Term t = getValue();

        return (t != this) && t.isConstant();
    }

    /**
     * Reports whether or not this term is compound. A variable is compound if it has been assigned a value which is
     * compound, and not otherwise.
     *
     * @return <tt>true</tt> if this term is compound, <tt>fals</tt> otherwise.
     */
    public boolean isCompound()
    {
        Term t = getValue();

        return (t != this) && t.isCompound();
    }

    /**
     * Reports whether or not this term is an atom. A variable is an atom if it has been assigned a value which is an
     * atom and not otherwise.
     *
     * @return <tt>true</tt> if this variable has been assigned atomic value, <tt>false</tt> otherwise.
     */
    public boolean isAtom()
    {
        Term t = getValue();

        return (t != this) && t.isAtom();
    }

    /**
     * Reports whether or not this term is a ground term. A variable is a ground term if it has been assigned a value
     * which is a ground term, and not otherwise.
     *
     * @return <tt>true</tt> if this variable has been assigned a ground term, <tt>false</tt> othewise.
     */
    public boolean isGround()
    {
        Term t = getValue();

        return (t != this) && t.isGround();
    }

    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable. When the variable is free, the variable term itself is returned.
     *
     * @return The term itself, or the assigned value of this variable.
     */
    public Term getValue()
    {
        Term result = this;
        Term assignment = this.substitution;

        // If the variable is assigned, loops down the chain of assignments until no more can be found. Whatever term
        // is found at the end of the chain of assignments is the value of this variable.
        while (assignment != null)
        {
            result = assignment;

            if (!assignment.isVar())
            {
                break;
            }
            else
            {
                assignment = ((Variable) assignment).substitution;
            }
        }

        return result;
    }

    /** Frees all assigned variables in the term, leaving them unnassigned. */
    public void free()
    {
        substitution = null;
    }

    /**
     * Reports wether or not this is an anonymous variable.
     *
     * @return <tt>true</tt> if this is an anonymous variable, <tt>false</tt> otherwise.
     */
    public boolean isAnonymous()
    {
        return anonymous;
    }

    /**
     * Reports whether or not this variable is bound to a value.
     *
     * @return <tt>true</tt> if this variable has been assigned a value, <tt>false</tt> otherwise.
     */
    public boolean isBound()
    {
        return substitution != null;
    }

    /**
     * Gets the value (if any) to which this variable has been assigned.
     *
     * @return The value to which this variable has been bound, or <tt>null</tt> if it is free.
     */
    public Term getSubstitution()
    {
        return substitution;
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

        substitution = termToBindTo;
    }

    /**
     * Gets the name of this variable.
     *
     * @return The name of this variable.
     */
    public int getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        if (visitor instanceof VariableVisitor)
        {
            ((VariableVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }

    /** {@inheritDoc} */
    public Variable acceptTransformer(TermTransformer transformer)
    {
        if (transformer instanceof VariableTransformer)
        {
            return ((VariableTransformer) transformer).transform(this);
        }
        else
        {
            return (Variable) super.acceptTransformer(transformer);
        }
    }

    /**
     * Compares this term for structural equality with another. Two terms are structurally equal if they are the same
     * functor with the same arguments, or are the same unbound variable, or the bound values of the left or right
     * variable operands are structurally equal. Structural equality is a stronger equality than unification and unlike
     * unification it does not produce any variable bindings. Two unified terms will always be structurally equal.
     *
     * @param  term The term to compare with this one for structural equality.
     *
     * @return <tt>true</tt> if the two terms are structurally eqaul, <tt>false</tt> otherwise.
     */
    public boolean structuralEquals(Term term)
    {
        Term comparator = term.getValue();
        Term value = getValue();

        // Check if this is an unbound variable in which case the comparator must be the same variable.
        if (value == this)
        {
            return this.equals(comparator);
        }
        else
        {
            return value.structuralEquals(comparator);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Orders variables by interned name.
     */
    public int compareTo(Variable o)
    {
        return (name > o.name) ? 1 : ((name < o.name) ? -1 : 0);
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        Term value = null;

        String textName;

        if (isAnonymous())
        {
            textName = "_" + id;
        }
        else
        {
            textName = interner.getVariableName(name);
        }

        if (printBindings)
        {
            value = getValue();
        }

        if (!printBindings || (value == this))
        {
            return textName;
        }
        else if (printVarName)
        {
            return textName + "/" + value.toString(interner, printVarName, printBindings);
        }
        else
        {
            return value.toString(interner, printVarName, printBindings);
        }
    }

    /**
     * A unique id for this variable.
     *
     * @return The unique id for this variable.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Creates a string representation of this variable, mostly used for debugging purposes.
     *
     * @return A string reprenestation of this variable.
     */
    public String toString()
    {
        return "Variable: [ name = " + name + ", substitution = " + ((substitution == null) ? "null" : substitution) +
            " ]";
    }
}
