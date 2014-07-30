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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.TermUtils;
import com.thesett.aima.logic.fol.Unifier;
import com.thesett.aima.logic.fol.Variable;

/**
 * A prolog unifier is a single threaded unification algorithm that ommits the occurs check. Ommitting the occurs check
 * makes it logically unsound, but in practice this is avoided by the carefull arrangement of prolog style logic
 * programs.
 *
 * <p/>This unification algorithm is based on the one presented on page 303 of Artificial Intelligence a Modern
 * Approach, the basic outline of that algorithm is still present, but the details have changed significantly.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Attempt to unify two terms. <td> {@link Term}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologUnifier implements Unifier<Term>
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(PrologUnifier.class.getName()); */

    /**
     * Unifies two terms and produces a list of bound variables that form the unification, when it it possible.
     *
     * @param  query     The left term to unify.
     * @param  statement The right term to unify.
     *
     * @return A list of bound variables to form the unification, or <tt>null</tt> when no unification is possible.
     */
    public List<Variable> unify(Term query, Term statement)
    {
        /*log.fine("unify(Term left = " + query + ", Term right = " + statement + "): called");*/

        // Find all free variables in the query.
        Set<Variable> freeVars = TermUtils.findFreeNonAnonymousVariables(query);

        // Build up all the variable bindings in both sides of the unification in these bindings.
        List<Variable> queryBindings = new LinkedList<Variable>();
        List<Variable> statementBindings = new LinkedList<Variable>();

        // Fund the most general unifier, if possible.
        boolean unified = unifyInternal(query, statement, queryBindings, statementBindings);
        List<Variable> results = null;

        // If a unification was found, only retain the free variables in the query in the results returned.
        if (unified)
        {
            queryBindings.retainAll(freeVars);
            results = new ArrayList<Variable>(queryBindings);
        }

        return results;
    }

    /**
     * Attempts to unify one term with another, against a background of already unified variables in both terms. In the
     * case where two terms are being unified from scratch the variable assignments will be empty.
     *
     * @param  left       The left hand term to unify with.
     * @param  right      The right hand term to unify against.
     * @param  leftTrail  The trail of bound variables in the left hand term.
     * @param  rightTrail The trail of bound variables in the right hand term.
     *
     * @return <tt>true</tt> if the terms were unified, <tt>false</tt> otherwise.
     */
    public boolean unifyInternal(Term left, Term right, List<Variable> leftTrail, List<Variable> rightTrail)
    {
        /*log.fine("public boolean unifyInternal(Term left = " + left + ", Term right = " + right +
            ", List<Variable> trail = " + leftTrail + "): called");*/

        if (left == right)
        {
            /*log.fine("Terms are identical objects.");*/

            return true;
        }

        if (!left.isVar() && !right.isVar() && left.isConstant() && right.isConstant() && left.equals(right))
        {
            /*log.fine("Terms are equal atoms or literals.");*/

            return true;
        }
        else if (left.isVar())
        {
            /*log.fine("Left is a variable.");*/

            return unifyVar((Variable) left, right, leftTrail, rightTrail);
        }
        else if (right.isVar())
        {
            /*log.fine("Right is a variable.");*/

            return unifyVar((Variable) right, left, rightTrail, leftTrail);
        }
        else if (left.isFunctor() && right.isFunctor())
        {
            /*log.fine("Terms are functors, at least one of which is not an atom.");*/

            Functor leftFunctor = (Functor) left;
            Functor rightFunctor = (Functor) right;

            // Check if the functors may be not be equal (that is, they do not have the same name and arity), in
            // which case they cannot possibly be unified.
            if (!left.equals(right))
            {
                return false;
            }

            /*log.fine("Terms are functors with same name and arity, both are compound.");*/

            // Pairwise unify all of the arguments of the functor.
            int arity = leftFunctor.getArity();

            for (int i = 0; i < arity; i++)
            {
                Term leftArgument = leftFunctor.getArgument(i);
                Term rightArgument = rightFunctor.getArgument(i);

                boolean result = unifyInternal(leftArgument, rightArgument, leftTrail, rightTrail);

                if (!result)
                {
                    /*log.fine("Non unifying arguments in functors encountered, left = " + leftArgument + ", right = " +
                        rightArgument);*/

                    return false;
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Unifies a variable with a term. If the variable is bound, then the bound value is unified with the term. If the
     * term is a bound variable, and the variable is free, then the vairable is unified with the bound value of the
     * term. Otherwise, the variable is free and is bound to the value of the term.
     *
     * @param  leftVar    The variable to unify.
     * @param  rightTerm  The term to unify against.
     * @param  leftTrail  The built up trail of substitutions in the left hand side.
     * @param  rightTrail The built up trail of substitutions in the right hand side.
     *
     * @return <tt>true</tt> if the variable unifies with the term, <tt>false</tt> otherwise.
     */
    protected boolean unifyVar(Variable leftVar, Term rightTerm, List<Variable> leftTrail, List<Variable> rightTrail)
    {
        /*log.fine("protected boolean unifyVar(Variable var = " + leftVar + ", Term term = " + rightTerm +
            ", List<Variable> trail = " + leftTrail + "): called");*/

        // Check if the variable is bound (in the trail, but no need to explicitly check the trail as the binding is
        // already held against the variable).
        if (leftVar.isBound())
        {
            /*log.fine("Variable is bound.");*/

            return unifyInternal(leftVar.getValue(), rightTerm, leftTrail, rightTrail);
        }
        else if (rightTerm.isVar() && ((Variable) rightTerm).isBound())
        {
            // The variable is free, but the term itself is a bound variable, in which case unify againt the value
            // of the term.
            /*log.fine("Term is a bound variable.");*/

            return unifyInternal(leftVar, rightTerm.getValue(), leftTrail, rightTrail);
        }
        else
        {
            // Otherwise, unify by binding the variable to the value of the term.
            /*log.fine("Variable is free, substituting in the term for it.");*/
            leftVar.setSubstitution(rightTerm);
            leftTrail.add(leftVar.getStorageCell(leftVar));
            //leftTrail.add(leftVar);

            return true;
        }

        // Occurs can go above if desired.
        /*else if (var occurs anywhere in x)
        {
            return false;
        }*/
    }
}
