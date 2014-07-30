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

import java.util.HashMap;
import java.util.Map;

import com.thesett.aima.logic.fol.BaseTermTransformer;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableTransformer;

/**
 * StackVariableTransform implements a compilation transformation, that replaces all variables in a term with stack
 * variables, assigned to stack slots sequentially from a specified offset, with a variable binding context.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Replace variables with stack assigned variables.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StackVariableTransform extends BaseTermTransformer implements VariableTransformer
{
    /** Used to build up a mapping from normal variables to stack variables. */
    protected Map<Variable, StackVariable> varMapping = new HashMap<Variable, StackVariable>();

    /** This is used to number the stack positions as variables are encountered. */
    protected int offset;

    /** Used to build up the result of the current compilation in. */
    protected VariableBindingContextSupplier context;

    /**
     * Creates a stack variable transformation that assigns variables to stack slots numbered from the specified offset
     * within the specified binding context.
     *
     * @param offset  The start offset to number stack slots form.
     * @param context The variable binding context that supplies the stack frame.
     */
    public StackVariableTransform(int offset, VariableBindingContextSupplier context)
    {
        this.offset = offset;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Replaces all of the free variables in the clause with stack variables.
     */
    public Variable transform(Variable variable)
    {
        StackVariable stackVar = varMapping.get(variable);

        // Check if a stack variable for the variable has not been created yet, and if so create a new stack variable,
        // setting the clause that it is in as its binding context supplier.
        if (stackVar == null)
        {
            stackVar = new StackVariable(variable.getName(), null, variable.isAnonymous(), offset++, context);
            varMapping.put(variable, stackVar);
        }

        return stackVar;
    }
}
