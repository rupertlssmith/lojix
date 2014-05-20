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
package com.thesett.aima.logic.fol.wam.machine;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * WAMResolvingMachineDPI is a debug and profiling interface for a {@link WAMResolvingMachine}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide access to the machines code buffer.</td></tr>
 * <tr><td> Provide access to the machines data buffer, and delimiters for around different data areas.</td></tr>
 * <tr><td> Provide the current heap pointer.</td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface WAMResolvingMachineDPI
{
    /**
     * Attaches a monitor to the abstract machine.
     *
     * @param monitor The machine monitor.
     */
    void attachMonitor(WAMResolvingMachineDPIMonitor monitor);

    /**
     * Provides the machines bytecode buffer.
     *
     * @return The machines bytecode buffer.
     */
    ByteBuffer getCodeBuffer();

    /**
     * Provides the machines data area.
     *
     * @return The machines data area.
     */
    IntBuffer getDataBuffer();

    /**
     * Provides the internal register file and flags for the machine.
     *
     * @return The internal register file and flags for the machine.
     */
    WAMInternalRegisters getInternalRegisters();

    /**
     * Provides the internal register set describing the memory layout of the machine.
     *
     * @return The internal register set describing the memory layout of the machine.
     */
    WAMMemoryLayout getMemoryLayout();
}
