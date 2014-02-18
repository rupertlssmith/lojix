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
package com.thesett.aima.logic.fol.compiler;

import java.util.Iterator;

import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.TermTraverser;
import com.thesett.aima.logic.fol.TermVisitor;
import com.thesett.aima.search.QueueBasedSearchMethod;
import com.thesett.aima.search.util.Searches;

/**
 * TermWalker combines together a {@link QueueBasedSearchMethod}, a {@link com.thesett.aima.logic.fol.TermTraverser} and
 * a {@link com.thesett.aima.logic.fol.TermVisitor}, to implement a walk over a recursive term data structure. The queue
 * based search method, controls the order of the walk and the order in which nodes in the term are examined. The
 * traverser allows context information to be embedded in the traversal operators, such that whenever a node is examined,
 * the operators leading to it from the root node, will have been expanded in the order that they are encountered along
 * the path from the root to the current node. The term visitor allows visitor methods to be established on a sub-set of
 * the term nodes in order to effect some function or transformation of the term.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Walk over a term in a specified order, visiting sub-terms that match a search criteria.
 *     <td> {@link Term}, {@link TermTraverser}, {@link TermVisitor}, {@link QueueBasedSearchMethod}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TermWalker
{
    /** Holds the search that order the walk. */
    QueueBasedSearchMethod<Term, Term> search;

    /** Holds the traverser to expand selected nodes with context. */
    TermTraverser traverser;

    /** Holds the visitor to apply to every goal node discovered by the search. */
    TermVisitor visitor;

    /**
     * Creates a term walker using the specified search, traverser and visitor.
     *
     * @param search    The search to order the walk by.
     * @param traverser The traverser to expand nodes and supply context with.
     * @param visitor   The visitor to apply to every goal node encountered.
     */
    public TermWalker(QueueBasedSearchMethod<Term, Term> search, TermTraverser traverser, TermVisitor visitor)
    {
        this.search = search;
        this.traverser = traverser;
        this.visitor = visitor;
    }

    /**
     * Walks over the supplied term.
     *
     * @param term The term to walk over.
     */
    public void walk(Term term)
    {
        // Set up the traverser on the term to walk over.
        term.setTermTraverser(traverser);

        // Create a fresh search starting from the term.
        search.reset();
        search.addStartState(term);

        Iterator<Term> treeWalker = Searches.allSolutions(search);

        // If the traverser is a term visitor, allow it to visit the top-level term in the walk to establish
        // an initial context.
        if (traverser instanceof TermVisitor)
        {
            term.accept((TermVisitor) traverser);
        }

        // Visit every goal node discovered in the walk over the term.
        while (treeWalker.hasNext())
        {
            Term nextTerm = treeWalker.next();
            nextTerm.accept(visitor);
        }

        // Remote the traverser on the term to walk over.
        term.setTermTraverser(null);
    }
}
