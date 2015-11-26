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

import java.util.List;

/**
 * A unifier is a function that tries to find a most general assignment of the variables in two terms, that makes the
 * terms identical with respect to equality, or some other comparison measure.
 *
 * <p/>The unification function has been encapsulated beneath an interface because there can be several different
 * implementations. For example, implementations with or without the occurs check, or parallelized implementations that
 * can take advantage of multiple CPUs, or implementations that alter the comparison measure, for example, by unifying
 * arithmetic constraints.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Unify a query against a statement, binding variables in the query.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Unifier<T extends Term>
{
    /**
     * Unifies two terms and produces a list of variable binding of the free variables in the left term, that form the
     * unification, when it it possible. Note that the returned list of bindings should not contain bindings for free
     * variables in the right hand term. For example:
     *
     * <p/>
     * <pre>
     * Unifying f(X) with f(x) should return X = x.
     * Unifying f(x) with f(X) should succeed but return an empty list of bindings.
     * </pre>
     *
     * @param  query     The left term to unify, this is the query with variables to bind.
     * @param  statement The right term to unify, this is the statement to unify against.
     *
     * @return A list of bound variables to form the unification, or <tt>null</tt> when no unification is possible.
     */
    List<Variable> unify(T query, T statement);
}
