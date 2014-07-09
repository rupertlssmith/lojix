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
package com.thesett.aima.logic.fol.wam.debugger.monitor;

import java.nio.ByteBuffer;

import com.thesett.aima.logic.fol.wam.debugger.InternalMemoryLayoutBean;
import com.thesett.aima.logic.fol.wam.debugger.InternalRegisterBean;
import com.thesett.aima.logic.fol.wam.machine.WAMResolvingMachineDPI;
import com.thesett.aima.logic.fol.wam.machine.WAMResolvingMachineDPIMonitor;

/**
 * MachineMonitor is the top level DPI monitor implementation for the debugger. Its purpose is to link together child
 * monitor components for different parts of the machine, that in turn update the user interface model in response to
 * events on the machine.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Connect together the child monitor components. </td>
 *     <td> {@link RegisterSetMonitor}, {@link MemoryLayoutMonitor},
 *          {@link BreakpointMonitor}, {@link ByteCodeMonitor} </td></tr>
 * <tr><td> Initiate the update of child monitor components in response to machine evens. </td>
 *     <td> {@link RegisterSetMonitor}, {@link MemoryLayoutMonitor},
 *          {@link BreakpointMonitor}, {@link ByteCodeMonitor} </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MachineMonitor implements WAMResolvingMachineDPIMonitor
{
    /** Holds a copy of the internal registers and monitors them for changes. */
    private InternalRegisterBean internalRegisters;

    /** Holds a copy of the memory layout registers and monitors them for changes. */
    private InternalMemoryLayoutBean layoutRegisters;

    /** Holds the monitor to listen for changes to the register set. */
    private final RegisterSetMonitor registerSetMonitor;

    /** Holds the monitor to listen for changes to the memory layout. */
    private final MemoryLayoutMonitor layoutMonitor;

    /** The monitor to listen for breakpoints. */
    private final BreakpointMonitor breakpointMonitor;

    /** The monitor to listen for bytecode changes on the machine. */
    private final ByteCodeMonitor byteCodeMonitor;

    /**
     * Creates the machine monitor.
     *
     * @param registerSetMonitor The monitor to listen for changes to the register set.
     * @param layoutMonitor      The monitor to listen for changes to the memory layout.
     * @param breakpointMonitor  The monitor to listen for breakpoints.
     * @param byteCodeMonitor    The monitor to listen for bytecode changes on the machine.
     */
    public MachineMonitor(RegisterSetMonitor registerSetMonitor, MemoryLayoutMonitor layoutMonitor,
        BreakpointMonitor breakpointMonitor, ByteCodeMonitor byteCodeMonitor)
    {
        this.registerSetMonitor = registerSetMonitor;
        this.layoutMonitor = layoutMonitor;
        this.breakpointMonitor = breakpointMonitor;
        this.byteCodeMonitor = byteCodeMonitor;
    }

    /** {@inheritDoc} */
    public void onReset(WAMResolvingMachineDPI dpi)
    {
        internalRegisters = new InternalRegisterBean(0, 0, 0, 0, 0, 0, 0, 0, 0, false);
        internalRegisters.setPropertyChangeListener(registerSetMonitor);
        internalRegisters.setPropertyChangeListener(breakpointMonitor);
        internalRegisters.updateRegisters(dpi.getInternalRegisters());

        layoutRegisters = new InternalMemoryLayoutBean(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        layoutRegisters.setPropertyChangeListener(layoutMonitor);
        layoutRegisters.updateRegisters(dpi.getMemoryLayout());
    }

    /** {@inheritDoc} */
    public void onCodeUpdate(WAMResolvingMachineDPI dpi, int start, int length)
    {
        ByteBuffer codeBuffer = dpi.getCodeBuffer(start, length);
        byteCodeMonitor.onCodeUpdate(codeBuffer, start, length, dpi.getVariableAndFunctorInterner(), dpi);
    }

    /** {@inheritDoc} */
    public void onExecute(WAMResolvingMachineDPI dpi)
    {
        internalRegisters.updateRegisters(dpi.getInternalRegisters());
        layoutRegisters.updateRegisters(dpi.getMemoryLayout());
    }

    /** {@inheritDoc} */
    public void onStep(WAMResolvingMachineDPI dpi)
    {
        internalRegisters.updateRegisters(dpi.getInternalRegisters());
        layoutRegisters.updateRegisters(dpi.getMemoryLayout());

        breakpointMonitor.pause();
    }
}
