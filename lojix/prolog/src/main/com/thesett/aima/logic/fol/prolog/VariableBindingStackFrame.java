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

import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableBindingContext;

/**
 * VariableBindingStackFrame is a stack frame for holding the bindings of {@link StackVariable}s in. It holds an array
 * of {@link Variable} bindings slots, and provides these as the storage cells to stack variables depending on the stack
 * variable's assigned stack position.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <tr> Responsibilities
 * <tr><td> Initialize an array of storage cells.
 * <tr><td> Provide storage cells for stack variables dependant on their relative storage positions.
 * <td> {@link StackVariable}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VariableBindingStackFrame implements VariableBindingContext<StackVariable>
{
    /** Holds the variable bindings in this stack frame. */
    Variable[] bindings;

    /**
     * Creates a stack frame of the specified size.
     *
     * @param size The size of the stack frame to create.
     */
    public VariableBindingStackFrame(int size)
    {
        bindings = new Variable[size];

        for (int i = 0; i < size; i++)
        {
            bindings[i] = new Variable(-1, null, true);
        }
    }

    /**
     * Provides the storage cell for the specified variable. Some types of variable may defer their storage onto a
     * storage cell other than themselves, other variable types may simply return themselves as their own storage cells.
     *
     * @param  variable The variable to get the storage cell for.
     *
     * @return The storage cell where the specified variable sets its bindings.
     */
    public Variable getStorageCell(StackVariable variable)
    {
        return bindings[variable.getPosition()];
    }

    /**
     * Frees all variables held in this stack frame, and sets all the stack slots to <tt>null</tt>. Note that as the
     * stack slots are <tt>null</tt> the {@link #getStorageCell(StackVariable)} method will not return valid storage
     * cells any more. This method should therefore only be used to clean up the stack frame when it is no longer
     * needed.
     */
    public void free()
    {
        for (int i = 0; i < bindings.length; i++)
        {
            if (bindings[i] != null)
            {
                bindings[i].free();
                bindings[i] = null;
            }
        }
    }
}
