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

import java.util.Iterator;

import com.thesett.aima.search.Operator;
import com.thesett.aima.search.util.backtracking.ReTraversable;
import com.thesett.aima.search.util.backtracking.Reversable;
import com.thesett.common.parsing.SourceCodePosition;
import com.thesett.common.util.doublemaps.SymbolKey;

/**
 * Term is the root abstract type of all terms in first order logic extended with standard definitions of various
 * numerical types. The terms in this language may either be numbers, variables or functors.
 *
 * <p/>The allowable number types are {@link IntLiteral}s, {@link FloatLiteral}s, {@link DoubleLiteral}s and
 * {@link LongLiteral}s, all of which behave in the same way as the underlying Java types they correspond to.
 * {@link Variable}s are assignments of another term to a name, or are free (unnassigned). Functors are compositions of
 * other terms, expressing logical membership of a set defined by the functor and denoted by its name. A special case is
 * functors of arity zero, which are constants. This class defines methods {@link #isNumber}, {@link #isFunctor} and
 * {@link #isVar} to determine which of these basic term types a term is.
 *
 * <p/>A term is a constant if it (or its value for assinged variables) is a functor of arity zero, or a number. This
 * can be tested for using the {@link #isConstant} method.
 *
 * <p/>A term is compound if it (or its value for assigned variables), is a functor with one or more arguments. This can
 * be tesed for using the {@link #isCompound} method.
 *
 * <p/>A term is an atom if it (or its value for assigned variables), is a functor with no arguments. This can be tested
 * for using the {@link #isAtom} method.
 *
 * <p/>Numbers are ground terms, as are variables that are assigned numeric values and functors whose arguments are
 * recursively ground. This can be tested for using the {@link #isGround} method.
 *
 * <p/>The purpose of the class hierarchy headed by this term type, is to act both as an abstract syntax tree for parsed
 * statements in first order logic, and to provide a minimal semantic structure on which logical engines can be built.
 * The intent is to build a data structure that can be queried efficiently, in order to build efficient logical engines
 * on top of.
 *
 * <p/>The {@link #getAllocation()} and {@link #setAllocation} methods can be used to associate a term with a register
 * allocation, or memory cell allocation or reference, or any other int value that may be of use during compilation or
 * execution of logic programs.
 *
 * <p/>Terms must also implement {@link ReTraversable} as this provides a mechanism to walk over the syntax tree in a
 * variety of ways using {@link com.thesett.aima.search.QueueBasedSearchMethod}s. When searches over term trees the
 * available child terms in a search form the operators to move to new search states, hence this implements
 * {@link Operator}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Report whether or not a term is a number.
 * <tr><td> Report whether or not a term is a functor.
 * <tr><td> Report whether or not a term is a variable.
 * <tr><td> Report whether or not a term is a constant.
 * <tr><td> Report whether or not a term is compound.
 * <tr><td> Report whether or not a term is an atom.
 * <tr><td> Report whether or not a term is a ground term.
 * <tr><td> Get the actual value of a variable term.
 * <tr><td> Mark a term with an allocated integer.
 * <tr><td> Free all variable assignments in a term.
 * <tr><td> Get all child terms of a compound term.
 * <tr><td> Optionally hold a reference to the source code position that the term was parsed from.
 * <tr><td> Reports if a term has been parsed as a bracketed expression, not requiring further reduction.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Term extends ReTraversable<Term>, Operator<Term>
{
    /**
     * Reports whether or not this term is a number.
     *
     * @return <tt>true</tt> if this term is a number, <tt>false</tt> otherwise.
     */
    boolean isNumber();

    /**
     * Reports whether or not this term is a functor.
     *
     * @return <tt>true</tt> if this term is a functor, <tt>false</tt> otherwise.
     */
    boolean isFunctor();

    /**
     * Reports whether or not this term is a variable.
     *
     * @return <tt>true</tt> if this term is a variable, <tt>false</tt> otherwise.
     */
    boolean isVar();

    /**
     * Reports whether or not this term is a constant (a number of a functor of arity zero).
     *
     * @return <tt>true</tt> if this term is constant, <tt>false</tt> otherwise.
     */
    boolean isConstant();

    /**
     * Reports whether or not this term is compound (a functor of arity one or more).
     *
     * @return <tt>true</tt> if this term is compound, <tt>fals</tt> otherwise.
     */
    boolean isCompound();

    /**
     * Reports whether or not this term is an atom (a functor of arity zero).
     *
     * @return <tt>true</tt> if this term is an atom, <tt>false</tt> otherwise.
     */
    boolean isAtom();

    /**
     * Reports whether or not this term is a ground term.
     *
     * @return <tt>true</tt> if this term is a ground term, <tt>false</tt> othewise.
     */
    boolean isGround();

    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable.
     *
     * @return The term itself, or the assigned value for variables.
     */
    Term getValue();

    /**
     * Gets this terms allocation cell. This is an extra value that may be of use during compilation.
     *
     * @return This terms allocation cell. A value of -1 means unnassigned.
     */
    int getAllocation();

    /**
     * Sets this terms allocation cell. This may be of use during compilation.
     *
     * @param alloc The terms allocation cell.
     */
    void setAllocation(int alloc);

    /**
     * Associates a symbol key with this term, that is unique within all scopes of a symbol table.
     *
     * @param key The symbol key for the term.
     */
    void setSymbolKey(SymbolKey key);

    /**
     * Gets this terms unique symbol key.
     *
     * @return This terms unique symbol key.
     */
    SymbolKey getSymbolKey();

    /** Frees all assigned variables in the term, leaving them unnassigned. */
    void free();

    /**
     * Makes a clone of the term, converting its variables to refer directly to their storage cells.
     *
     * @return A copy of this term, with entirely independent variables to the term it was copied from.
     */
    Term queryConversion();

    /**
     * Allows a reversable operator to be set upon the term, so that context can be established or cleared as terms are
     * traversed.
     *
     * @param reversable The reversable operator to use on the term.
     */
    void setReversable(Reversable reversable);

    /**
     * Allows a term traverser to supply search operators over terms to be set.
     *
     * @param traverser The traverser to supply search operators over terms.
     */
    void setTermTraverser(TermTraverser traverser);

    /**
     * Provides an iterator over the child terms, if there are any. Only functors are compound, and build across a list
     * of child arguments.
     *
     * @param  reverse Set, if the children should be presented in reverse order.
     *
     * @return The sub-terms of a compound term.
     */
    Iterator<Operator<Term>> getChildren(boolean reverse);

    /**
     * Provides the source code position that this term was parsed from.
     *
     * @return The source code position that this term was parsed from. May be <tt>null</tt> if no position has been
     *         associated.
     */
    SourceCodePosition getSourceCodePosition();

    /**
     * Associates a source code position with this term.
     *
     * @param sourceCodePosition The source code position that this term was parsed from.
     */
    void setSourceCodePosition(SourceCodePosition sourceCodePosition);

    /**
     * Reports whether this term is the top-level term in a bracketed expression, and therefore requires no fruther
     * reduction outside of the brackets.
     *
     * @return <tt>true</tt> if this term is bracketed, <tt>false</tt> if not.
     */
    boolean isBracketed();

    /**
     * Sets the bracketed status of this term.
     *
     * @param bracketed The bracketed status of this term.
     */
    void setBracketed(boolean bracketed);

    /**
     * Accepts a term visitor.
     *
     * @param visitor The term visitor to accept.
     */
    void accept(TermVisitor visitor);

    /**
     * Applies a term to term transformation function over the term tree, recursively from this point downards. This is
     * a general recursive mapping function over term trees, and is intended to be usefull for transforming term trees
     * for compilation, optimization or other transformational activities.
     *
     * @param  transformer The transformation function to apply.
     *
     * @return The transformed term tree.
     */
    Term acceptTransformer(TermTransformer transformer);

    /**
     * Pretty prints a term relative to the symbol namings provided by the specified interner.
     *
     * @param  interner      The interner use to provide symbol names.
     * @param  printVarName  <tt>true</tt> if the names of bound variables should be printed, <tt>false</tt> if just the
     *                       binding without the variable name should be printed.
     * @param  printBindings <tt>true</tt> if variable binding values should be printed, <tt>false</tt> if just the
     *                       variables name without any binding should be printed.
     *
     * @return A pretty printed string containing the term.
     */
    String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings);

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
    boolean structuralEquals(Term term);
}
