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
package com.thesett.aima.logic.fol.prolog;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.VariableBindingContext;

/**
 * PrologCompiledClause is a clause that has been compiled ready for evaluation by a {@link PrologResolver}. Such a
 * clause is the same as an uncompiled {@link Clause} except that its variables have been allocated stack positions and
 * transformed into {@link StackVariable}s.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Capture an optional head and optioanl body sequence of functors as a Horn clause.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologCompiledClause extends Clause<Functor> implements Sentence<PrologCompiledClause>,
    VariableBindingContextSupplier
{
    /** Holds the variable binding context currently being used by this clause. */
    protected VariableBindingContext bindingContext;

    /** Holds the required stack frame size for this clause. */
    protected int stackSize;

    /**
     * Creates a program sentence for the Prolog interpreter.
     *
     * @param head The head of the program, may be <tt>null</tt> for queries.
     * @param body The functors that make up the query body of the program, if any. May be <tt>null</tt>
     */
    public PrologCompiledClause(Functor head, Functor[] body)
    {
        super(head, body);
    }

    /**
     * Gets the wrapped sentence in the logical language over PrologCompiledClauses.
     *
     * @return The wrapped sentence in the logical language.
     */
    public PrologCompiledClause getT()
    {
        return this;
    }

    /**
     * Provides the binding context that this clause stores its variable bindings in.
     *
     * @return The binding context that this clause stores its variable bindings in.
     */
    public VariableBindingContext getBindingContext()
    {
        return bindingContext;
    }

    /**
     * Sets the binding context that this clause stores its variable bindings in.
     *
     * @param context The binding context that this clause stores its variable bindings in.
     */
    public void setBindingContext(VariableBindingContext context)
    {
        bindingContext = context;
    }

    /**
     * Provides the number of stack slots that this clause requires.
     *
     * @return The number of stack slots that this clause requires.
     */
    public int getStackSize()
    {
        return stackSize;
    }

    /**
     * Allows the number of stack slots that this clause needs to be specified. This field is not included in the
     * constructor, because it may be more conventient to set it at a later time, as the clause may need to be
     * constructed in order to use it as a variable context supplier, prior to counting the number of stack slots
     * needed.
     *
     * @param stackSize The number of stack slots that this clause needs.
     */
    public void setStackSize(int stackSize)
    {
        this.stackSize = stackSize;
    }
}
