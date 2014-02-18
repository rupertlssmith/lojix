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
package com.thesett.aima.logic.fol.vam.vamai.instructions;

import com.thesett.common.util.visitor.Visitor;

/**
 * Int is a VAMAI instruction.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encode a VAM AI instruction.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Goal extends VAMAIInstruction
{
    /** Holds the interned name of the functor to call as the goal. */
    private int arg;

    /** Holds a pointer to the next instruction beyond the end of the goal. */
    private int continuation;

    /**
     * Creates a VAM AI instruction with the specified functor argument.
     *
     * @param name    The functor argument.
     * @param endGoal A pointer to the end of the goal; the instruction following the terminating 'call'.
     */
    public Goal(int name, int endGoal)
    {
        super(VAMAIInstructionSet.Goal);
    }

    /** {@inheritDoc} */
    public void accept(Visitor<VAMAIInstruction> visitor)
    {
        if (visitor instanceof GoalVisitor)
        {
            ((GoalVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }

    /**
     * Provides the interned name of the functor to call.
     *
     * @return The interned name of the functor to call.
     */
    public int getArg()
    {
        return arg;
    }

    /**
     * Provides a pointer to the next instruction beyond the end of the goal.
     *
     * @return A pointer to the next instruction beyond the end of the goal.
     */
    public int getContinuation()
    {
        return continuation;
    }
}
