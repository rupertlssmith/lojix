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
package com.thesett.aima.logic.fol.vam.vam2p.instructions;

/**
 * CallVisitor is a visitor for a vam2p instruction.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Visit a vam 2p instruction.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface CallVisitor extends VAM2PInstructionVisitor
{
    /**
     * Visits a vam 2p instruction.
     *
     * @param instruction The instruction to visit.
     */
    public void visit(Call instruction);
}
