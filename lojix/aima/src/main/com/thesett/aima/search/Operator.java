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
package com.thesett.aima.search;

/**
 * Operator represents an operation that can be performed on a {@link Traversable} search state to produce more
 * searchable states. Operators are defined over an operator type, O, to enable the simple encapsulation of any object
 * type as operators, using the equality and hashcode methods of the object for differentiation between operators.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide an encapsulated object as an operator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Operator<O>
{
    /**
     * Returns the underlying object operator.
     *
     * @return The underlying object operator.
     */
    public O getOp();
}
