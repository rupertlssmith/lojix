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
import java.util.Set;

/**
 * A resolver implements a resolution (or proof) procedure over logical clauses. The result of the resolution will be a
 * binding of variables in the query, to satisfy the query over the domain of resolution. Multiple bindings may be
 * possible, in which case subsequent calls to the {@link #resolve} method will return them.
 *
 * <p/>Resolution is presented as a search to satisfy a query over a domain, and as such, this interface extends
 * {@link com.thesett.aima.search.QueueBasedSearchMethod}. To prepare a search, add clauses to the domain
 * ({@link #addToDomain}, then set the query as the starting point for the search
 * ({@link com.thesett.aima.search.QueueBasedSearchMethod#addStartState} before calling the
 * {@link com.thesett.aima.search.QueueBasedSearchMethod#findGoalPath()} method. This interface provides alternatives to
 * these two methods, that present the search more directly in terms of clauses and variable bindings, though their
 * implementation should usually delegate to the search methods, these are {@link #setQuery} and {@link #resolve}.
 *
 * <p/>The resolution procedure has been encapsulated beneath an interface because there can be many different
 * implementations. All resolvers that implement this interface work over Horn clauses.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Resolve a query over a set of Horn clauses.
 * <tr><td> Provide an iterator to generate all resolutions with.
 * </table></pre>
 *
 * @param  <T> The type of terms that the resolver accepts to form its domain (usually clauses or predicates).
 * @param  <Q> The type of terms that form queries on the resolver (usually clause bodies with no head).
 *
 * @author Rupert Smith
 */
public interface Resolver<T, Q> extends Iterable<Set<Variable>>
{
    /**
     * Adds the specified construction to the domain of resolution searched by this resolver.
     *
     * @param  term The term to add to the domain.
     *
     * @throws LinkageException If the term to add to the domain, cannot be added to it, because it depends on the
     *                          existance of other clauses which are not in the domain. Implementations may elect to
     *                          raise this as an error at the time the clauses are added to the domain, or during
     *                          resolution, or simply to fail to find a resolution.
     */
    void addToDomain(T term) throws LinkageException;

    /**
     * Sets the query to resolve.
     *
     * @param  query The query to resolve.
     *
     * @throws LinkageException If the query to add run over the domain, cannot be applied to it, because it depends on
     *                          the existance of clauses which are not in the domain. Implementations may elect to raise
     *                          this as an error at the time the query is created, or during resolution, or simply to
     *                          fail to find a resolution.
     */
    void setQuery(Q query) throws LinkageException;

    /**
     * Resolves a query over a logical domain, or knowledge base and a query. The domain and query to resolve over must
     * be established by prior to invoking this method. There may be more than one set of bindings that make the query
     * provable over the domain, in which case subsequent calls to this method will return successive bindings until no
     * more can be found. If no proof can be found, this method will return <tt>null</tt>.
     *
     * @return A list of variable bindings, if the query can be satisfied, or <tt>null</tt> otherwise.
     */
    Set<Variable> resolve();

    /**
     * Resets the resolver. This should clear any start and goal states, and leave the resolver in a state in which it
     * is ready to be run.
     */
    void reset();

    /**
     * Provides an iterator that generates all solutions on demand as a sequence of variable bindings.
     *
     * @return An iterator that generates all solutions on demand as a sequence of variable bindings.
     */
    Iterator<Set<Variable>> iterator();
}
