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
package com.thesett.common.util.logic;

/**
 * TrueUnaryPredicate is a unary object {@link UnaryPredicate} that always evaluates to true.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Evaluate to true.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TrueUnaryPredicate implements UnaryPredicate
{
    /**
     * Evaluates a logical predicate.
     *
     * @param  o The object to test for predicate membership.
     *
     * @return <tt>true</tt> always.
     */
    public boolean evaluate(Object o)
    {
        return true;
    }
}
