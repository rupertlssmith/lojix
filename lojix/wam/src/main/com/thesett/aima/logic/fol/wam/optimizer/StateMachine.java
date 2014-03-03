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
package com.thesett.aima.logic.fol.wam.optimizer;

import com.thesett.aima.logic.fol.wam.WAMInstruction;

/**
 * StateMachine is used to implement a FSMD, that is driven by a {@link Matcher}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Accept input from the matcher. </td></tr>
 * <tr><td> Accept end of input from the matcher. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface StateMachine<S, T>
{
    public void setMatcher(Matcher<S, T> matcher);

    /**
     * Accepts the next input from the matcher.
     *
     * @param next The next input data item.
     */
    void apply(S next);

    /** Accepts end of input notification from the matcher. */
    void end();
}
