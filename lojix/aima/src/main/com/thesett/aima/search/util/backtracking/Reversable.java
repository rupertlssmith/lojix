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
package com.thesett.aima.search.util.backtracking;

/**
 * Reversable provides two state change methods, one to establish state, and one to restore it. It is intended to be
 * used by objects that need to establish some globally visible state, when they become 'current' and restore the state
 * to its previous value when they go out of scope.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Apply globally visible state changes on request.
 * <tr><td> Undo any globally visible state changes made when generating this state.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Reversable
{
    /** Apply any globally visible state changes required by the operator that generated this state. */
    public void applyOperator();

    /** Undo any globally visible state changes made by applying an operator to this states parent state. */
    public void undoOperator();
}
