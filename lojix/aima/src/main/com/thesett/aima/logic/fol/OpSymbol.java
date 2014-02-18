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
package com.thesett.aima.logic.fol;

/**
 * Operators in first order logic, connect terms into compound units composed of many terms under the semantics of the
 * operator. Some operators are implicit in the language, such as the standard logic operators of 'and', 'or', 'implies'
 * and so on. Others may be invented and given a particular semantics specified in the language of first order logic
 * itself. An operator is a functor; it has a name, a number of arguments and forms a compound of terms. On top of the
 * behaviour of functors, operators also have precedences and associativities, that define in what order they bind to
 * neighbouring terms in the a textual form of the language. The precedences and associativity are not part of the logic
 * as such, they are hints to a parser.
 *
 * <p/>An OpSymbol is a functor, that additionally provides information to a parser as to its parsing priority and
 * associativity. An operator is a restricted case of a functor, in that it can take a minimum of one and a maximum of
 * two arguments.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Combine associativity and priority with a named composite.
 * <tr><td> Provide the textual representnation of a symbol.
 * <tr><td> Report a symbols fixity.
 * <tr><td> Provide priority comparison of the symbol with other symbols.
 * <tr><td> Allow a symbol to act as a template to instantiate copies from.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class OpSymbol extends Functor implements Comparable, Cloneable
{
    /** Defines the possible operator associativities. */
    public enum Associativity
    {
        /** Non-associative post-fix. */
        XF,

        /** Left associative post-fix. */
        YF,

        /** Non-associative pre-fix. */
        FX,

        /** Right associative pre-fix. */
        FY,

        /** Non-associative in-fix. */
        XFX,

        /** Right associative in-fix. */
        XFY,

        /** Left associative in-fix. */
        YFX
    }

    /** Defines the possible operator fixities. */
    public enum Fixity
    {
        /** Pre-fix. */
        Pre,

        /** Post-fix. */
        Post,

        /** In-fix. */
        In
    }

    /** Holds the raw text name of this operator. */
    protected String textName;

    /** Holds the associativity of this operator. */
    protected Associativity associativity;

    /** Holds the priority of this operator. */
    protected int priority;

    /**
     * Creates a new operator with the specified name and arguments.
     *
     * @param textName      The name of the operator.
     * @param arguments     The arguments the operator is applied to.
     * @param associativity Specifies the associativity of the operator.
     * @param priority      The operators priority.
     */
    public OpSymbol(String textName, Term[] arguments, Associativity associativity, int priority)
    {
        super(-1, arguments);

        // Check that there is at least one and at most two arguments.
        if ((arguments == null) || (arguments.length < 1) || (arguments.length > 2))
        {
            throw new IllegalArgumentException("An operator has minimum 1 and maximum 2 arguments.");
        }

        this.textName = textName;
        this.priority = priority;
        this.associativity = associativity;
    }

    /**
     * Creates a new operator with the specified name but no arguments.
     *
     * @param name          The interned name of the operator.
     * @param textName      The text name of the operator.
     * @param associativity Specifies the associativity of the operator.
     * @param priority      The operators priority.
     */
    public OpSymbol(int name, String textName, Associativity associativity, int priority)
    {
        super(name, null);

        this.textName = textName;
        this.priority = priority;
        this.associativity = associativity;
    }

    /**
     * Sets the arguments of this operator. It can be convenient to be able to set the outside of the constructor, for
     * example, when parsing may want to create the operator first and fill in its arguments later.
     *
     * @param arguments The arguments the operator is applied to.
     */
    public void setArguments(Term[] arguments)
    {
        // Check that there is at least one and at most two arguments.
        if ((arguments == null) || (arguments.length < 1) || (arguments.length > 2))
        {
            throw new IllegalArgumentException("An operator has minimum 1 and maximum 2 arguments.");
        }

        this.arguments = arguments;
        this.arity = arguments.length;
    }

    /**
     * Provides the symbols associativity.
     *
     * @return The symbols associativity.
     */
    public Associativity getAssociativity()
    {
        return associativity;
    }

    /**
     * Provides the symbols priority.
     *
     * @return The symbols priority.
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Provides the symbols textual representation.
     *
     * @return The symbols textual representation.
     */
    public String getTextName()
    {
        return textName;
    }

    /**
     * Provides the symbols fixity, derived from its associativity.
     *
     * @return The symbols fixity.
     */
    public Fixity getFixity()
    {
        switch (associativity)
        {
        case FX:
        case FY:
            return Fixity.Pre;

        case XF:
        case YF:
            return Fixity.Post;

        case XFX:
        case XFY:
        case YFX:
            return Fixity.In;

        default:
            throw new RuntimeException("Unknown associativity.");
        }
    }

    /**
     * Reports whether this operator is an prefix operator.
     *
     * @return <tt>true <tt>if this operator is an prefix operator.
     */
    public boolean isPrefix()
    {
        return ((associativity == Associativity.FX) || (associativity == Associativity.FY));
    }

    /**
     * Reports whether this operator is an postfix operator.
     *
     * @return <tt>true <tt>if this operator is an postfix operator.
     */
    public boolean isPostfix()
    {
        return ((associativity == Associativity.XF) || (associativity == Associativity.YF));
    }

    /**
     * Reports whether this operator is an infix operator.
     *
     * @return <tt>true <tt>if this operator is an infix operator.
     */
    public boolean isInfix()
    {
        return ((associativity == Associativity.XFY) || (associativity == Associativity.YFX) ||
            (associativity == Associativity.XFX));
    }

    /**
     * Reports whether this operatis is right associative.
     *
     * @return <tt>true</tt> if this operatis is right associative.
     */
    public boolean isRightAssociative()
    {
        return ((associativity == Associativity.FY) || (associativity == Associativity.XFY));
    }

    /**
     * Reports whether this operatis is left associative.
     *
     * @return <tt>true</tt> if this operatis is left associative.
     */
    public boolean isLeftAssociative()
    {
        return ((associativity == Associativity.YF) || (associativity == Associativity.YFX));
    }

    /**
     * Compares this object with the specified object for order, providing a negative integer, zero, or a positive
     * integer as this symbols priority is less than, equal to, or greater than the comparator. If this symbol is 'less'
     * than another that means that it has a lower priority value, which means that it binds more tightly.
     *
     * @param  o The object to be compared with.
     *
     * @return A negative integer, zero, or a positive integer as this symbols priority is less than, equal to, or
     *         greater than the comparator.
     */
    public int compareTo(Object o)
    {
        OpSymbol opSymbol = (OpSymbol) o;

        return (priority < opSymbol.priority) ? -1 : ((priority > opSymbol.priority) ? 1 : 0);
    }

    /**
     * Provides a copied clone of the symbol. The available symbols that a parser recognizes may be set up in a symbol
     * table. When an instance of a symbol is encountered it may be desireable to copy the symbol from the table. Using
     * this method an OpSymbol may act both as a template for symbols and as an individual instance of a symbols
     * occurance.
     *
     * @return A shallow copy of the symbol.
     */
    public OpSymbol copySymbol()
    {
        try
        {
            return (OpSymbol) clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(
                "Got a CloneNotSupportedException but clone is defined on OpSymbol and should not fail.", e);
        }
    }

    /**
     * Outputs the operator name, associativity, priority, arity and arguments as a string, used mainly for debugging
     * purposes.
     *
     * @return The operator as a string.
     */
    public String toString()
    {
        return "OpSymbol: [ name = " + textName + ", arity = " + arity + ", priority = " + priority +
            ", associativity = " + associativity + ", arguments = " + toStringArguments() + " ]";
    }

    /**
     * Creates a shallow clone of this operator symbol.
     *
     * @return A shallow clone of this object.
     *
     * @throws CloneNotSupportedException If cloning fails.
     */
    protected Object clone() throws CloneNotSupportedException
    {
        // Create a new state and copy the existing board position into it
        OpSymbol newSymbol = (OpSymbol) super.clone();

        return newSymbol;
    }
}
