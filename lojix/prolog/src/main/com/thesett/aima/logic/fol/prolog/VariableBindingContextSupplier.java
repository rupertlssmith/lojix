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

import com.thesett.aima.logic.fol.VariableBindingContext;

/**
 * A VariableBindingContextSupplier can supply a {@link VariableBindingContext} to a
 * {@link com.thesett.aima.logic.fol.Variable}. This allows many variables to hold a reference to a context supplier,
 * and for the context to be set accross all those variables in one go, simply by setting it on the supplier. For
 * example, a clause may act as the supplier to all its variables. When resolving against a clause the current stack
 * frame can be set as the context and all variables will know to store their bindings in the stack frame.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide a variable binding context.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface VariableBindingContextSupplier
{
    /**
     * Provides the variable binding context.
     *
     * @return The variable binding context.
     */
    VariableBindingContext getBindingContext();
}
