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

/**
 * VariableBindingContext provides a reference to the storage cell in which a {@link Variable} holds its binding.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide the storage cell for a variable.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface VariableBindingContext<V extends Variable>
{
    /**
     * Provides the storage cell for the specified variable. Some types of variable may defer their storage onto a
     * storage cell other than themselves, other variable types may simply return themselves as their own storage cells.
     *
     * @param  variable The variable to get the storage cell for.
     *
     * @return The storage cell where the specified variable sets its bindings.
     */
    public Variable getStorageCell(V variable);
}
