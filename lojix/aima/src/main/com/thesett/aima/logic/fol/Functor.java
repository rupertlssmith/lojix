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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thesett.aima.search.Operator;

/**
 * Functors are compositions of other terms, expressing membership of a set denoted by the functors name. The 'arity' of
 * a functor is the number of terms it accepts as arguments and defines the dimensions of the set that it defines. A
 * special case is functors of arity zero, which are considered to be constants or atoms of the language of first order
 * logic (or sets with only one member, if you like).
 *
 * <p/>For example, the functor, "dog", is a constant. The functor, animal(dog), is a unary predicate, partially
 * defining the set of animals by expressing that a dog is an animal. The functor, dislikes(dog, cat), is a binary
 * predicate over dogs and cats, expressing a relationship between them.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Define a named concept ranging over other terms. <td> {@link Term}.
 * <tr><td> Contain the arguments of the functor. <td> {@link Term}.
 * <tr><td> Provide structural equality matching over functors. <td> {@link Term}.
 * <tr><td> Provide quick name and arity based equality checking.
 * <tr><td> Provide an iterator over the argument of the functor for search methods.
 * <tr><td> Provide query conversion over the functors arguments. <td> {@link Term}.
 * <tr><td> Accept a term visitor to transform the functor. <td> {@link FunctorTransformer}.
 * <tr><td> Provide pretty printing to strings of the functor and its arguments. <td> {@link Term}.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Should recursively free all variables within the functor when free is called.
 * @todo   Write an empty iterator implementation for the default getChildren method on atoms.
 */
public class Functor extends BaseTerm implements Term
{
    /* private static final Logger log = Logger.getLogger(Functor.class.getName()); */

    /**
     * The name of the functor, or the set that the functor denotes. This is an integer that refers to an interned
     * object that provides the textual name and arity of the functor. An integer representation is used for fast
     * comparisons.
     */
    protected int name;

    /** Holds the functors arguments. */
    protected Term[] arguments;

    /** Holds the arity of the functor. */
    protected int arity;

    /** Holds the cached deep hash key for the functor, to speed up the {@link #deepHashKey} method. */
    protected int deepHashKey;

    /**
     * Creates a new functor with the specified arguments.
     *
     * @param name      The name of the functor.
     * @param arguments The functors arguments.
     */
    public Functor(int name, Term[] arguments)
    {
        this.name = name;
        this.arguments = arguments;
        this.arity = (arguments == null) ? 0 : arguments.length;
    }

    /**
     * Reports whether or not this term is a functor.
     *
     * @return Always <tt>true</tt>.
     */
    public boolean isFunctor()
    {
        return true;
    }

    /**
     * Reports whether or not this term is a constant (a number or a functor of arity zero).
     *
     * @return <tt>true</tt> if this functor has arity zero, <tt>false</tt> otherwise.
     */
    public boolean isConstant()
    {
        return arity == 0;
    }

    /**
     * Reports whether or not this term is compound (a functor of arity one or more).
     *
     * @return <tt>true</tt> if this funtor has arity more than zero, <tt>fals</tt> otherwise.
     */
    public boolean isCompound()
    {
        return arity > 0;
    }

    /**
     * Reports whether or not this term is an atom (a functor of arity zero).
     *
     * @return <tt>true</tt> if this functor has arity zero, <tt>false</tt> otherwise.
     */
    public boolean isAtom()
    {
        return arity == 0;
    }

    /**
     * Reports whether or not this term is a ground term. Constants (functors of arity zero) and numbers are ground
     * terms, as are functors all of the arguments of which are ground term.
     *
     * @return <tt>true</tt> if this functor is a ground term, <tt>false</tt> othewise.
     */
    public boolean isGround()
    {
        for (int i = 0; i < arity; i++)
        {
            if (!arguments[i].isGround())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Frees all assigned variables in the term, leaving them unnassigned. In the case of functors and other compund
     * structures, this recurses down into the term calling free on all argument, so that the call reaches all variables
     * in leaf positions of the term.
     */
    public void free()
    {
        if (arguments != null)
        {
            for (Term argument : arguments)
            {
                argument.free();
            }
        }
    }

    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable. For functors, the value is the functor itself.
     *
     * @return The functor itself.
     */
    public Term getValue()
    {
        return this;
    }

    /**
     * Gets the argument within the functor with the specified index.
     *
     * @param  index The index to get the argument for.
     *
     * @return The argument.
     */
    public Term getArgument(int index)
    {
        if ((arguments == null) || (index > (arguments.length - 1)))
        {
            return null;
        }
        else
        {
            return arguments[index];
        }
    }

    /**
     * Provides all of this functors arguments.
     *
     * @return All of this functors arguments, possibly <tt>null</tt>.
     */
    public Term[] getArguments()
    {
        return arguments;
    }

    /**
     * Sets the argument within the functor to the specified value.
     *
     * @param index The index to set the argument for.
     * @param value The argument.
     */
    public void setArgument(int index, Term value)
    {
        arguments[index] = value;
    }

    /**
     * Reports the number of arguments that this functor takes.
     *
     * @return The number of arguments that this functor takes.
     */
    public int getArity()
    {
        return arity;
    }

    /**
     * Reports the name of this functor, or of the set that it denotes.
     *
     * @return This functors name.
     */
    public int getName()
    {
        return name;
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

        if (this == comparator)
        {
            return true;
        }

        if ((comparator == null) || !(getClass().isAssignableFrom(comparator.getClass())))
        {
            return false;
        }

        Functor functor = (Functor) comparator;

        if ((arity != functor.arity) || (name != functor.name))
        {
            return false;
        }

        // Check the arguments of this functor and the comparator for structural equality.
        boolean passedArgCheck = true;

        if (arguments != null)
        {
            for (int i = 0; i < arguments.length; i++)
            {
                Term leftArg = arguments[i];
                Term rightArg = functor.arguments[i];

                if (!leftArg.structuralEquals(rightArg))
                {
                    passedArgCheck = false;

                    break;
                }
            }
        }

        return passedArgCheck;
    }

    /**
     * Compares this functor with another, to check if they are equal by name and arity. Two functors deemed equal by
     * this method, may not be fully equal terms in first order logic. This equality method checks the natural key, that
     * is, the name and arity, of the functor only. It is intended to be used to skim check potentially unifiable
     * functors, and in data structures that need to efficiently compare functors that may be unifiable.
     *
     * <p/>Another way of looking at it is to say that, if this method returns false, the two functors are definitely
     * not equal, and cannot be unified. The false answer is the definite one. If this method returns true, the two
     * functors may be unifiable.
     *
     * @param  comparator The object to compare to.
     *
     * @return <tt>true</tt> if the comparator has the same name and arity as this one, <tt>false</tt> otherwise.
     */
    public boolean equals(Object comparator)
    {
        if (this == comparator)
        {
            return true;
        }

        if ((comparator == null) || (getClass() != comparator.getClass()))
        {
            return false;
        }

        Functor functor = (Functor) comparator;

        return (arity == functor.arity) && (name == functor.name);
    }

    /**
     * Computes a hash code for this functor that is based on the same fields as the {@link #equals} method, that is,
     * the functors name and arity.
     *
     * @return A hash code based on the name and arity.
     */
    public int hashCode()
    {
        int result;
        result = name;
        result = (31 * result) + arity;

        return result;
    }

    /**
     * Provides an iterator over the child terms, if there are any. Only functors are compound, and built across a list
     * of child arguments.
     *
     * @param  reverse Set, if the children should be presented in reverse order.
     *
     * @return The sub-terms of a compound term.
     */
    public Iterator<Operator<Term>> getChildren(boolean reverse)
    {
        if ((traverser != null) && (traverser instanceof FunctorTraverser))
        {
            return ((FunctorTraverser) traverser).traverse(this, reverse);
        }
        else
        {
            if (arguments == null)
            {
                return new LinkedList<Operator<Term>>().iterator();
            }
            else if (!reverse)
            {
                return Arrays.asList((Operator<Term>[]) arguments).iterator();
            }
            else
            {
                List<Operator<Term>> argList = new LinkedList<Operator<Term>>();

                for (int i = arity - 1; i >= 0; i--)
                {
                    argList.add(arguments[i]);
                }

                return argList.iterator();
            }
        }
    }

    /**
     * Makes a clone of the term, converting its variables to refer directly to their storage cells.
     *
     * @return A copy of this term, with entirely independent variables to the term it was copied from.
     */
    public Functor queryConversion()
    {
        /*log.fine("public Functor queryConversion(): called)");*/

        Functor copy = (Functor) super.queryConversion();

        if (arguments != null)
        {
            copy.arguments = new Term[arguments.length];

            for (int i = 0; i < arguments.length; i++)
            {
                copy.arguments[i] = arguments[i].queryConversion();
            }
        }

        return copy;
    }

    /**
     * Creates a string representation of this functor, mostly used for debugging purposes.
     *
     * @return A string representation of this functor.
     */
    public String toString()
    {
        return "Functor: [ name = " + name + ", arity = " + arity + ", arguments = " + toStringArguments() + " ]";
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        if (visitor instanceof FunctorVisitor)
        {
            ((FunctorVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }

    /** {@inheritDoc} */
    public Functor acceptTransformer(TermTransformer transformer)
    {
        Functor result;

        if (transformer instanceof FunctorTransformer)
        {
            result = ((FunctorTransformer) transformer).transform(this);
        }
        else
        {
            result = (Functor) super.acceptTransformer(transformer);
        }

        if (arguments != null)
        {
            for (int i = 0; i < arguments.length; i++)
            {
                result.arguments[i] = arguments[i].acceptTransformer(transformer);
            }
        }

        return result;
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        if (name < 0)
        {
            return "internal_built_in";
        }

        String result = interner.getFunctorName(name);

        if (arity > 0)
        {
            result += "(";

            for (int i = 0; i < arity; i++)
            {
                Term nextArg = arguments[i];
                result += nextArg.toString(interner, printVarName, printBindings) + ((i < (arity - 1)) ? ", " : "");
            }

            result += ")";
        }

        return result;
    }

    /**
     * Creates a string representation of this functors arguments, mostly used for debugging purposes.
     *
     * @return A string reprenestation of this functors arguments.
     */
    protected String toStringArguments()
    {
        String result = "";

        if (arity > 0)
        {
            result += "[ ";

            for (int i = 0; i < arity; i++)
            {
                Term nextArg = arguments[i];
                result += ((nextArg != null) ? nextArg.toString() : "<null>") + ((i < (arity - 1)) ? ", " : " ");
            }

            result += " ]";
        }

        return result;
    }
}
