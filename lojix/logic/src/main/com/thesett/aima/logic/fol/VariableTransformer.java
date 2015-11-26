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
 * VariableVisitor implements a transformation function from variables to variables.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Transform variables to variables.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface VariableTransformer extends TermTransformer
{
    /**
     * Applies a transformation to the variable.
     *
     * @param  variable The variable to transform.
     *
     * @return A variable which is a transformation of the argument.
     */
    Variable transform(Variable variable);
}
