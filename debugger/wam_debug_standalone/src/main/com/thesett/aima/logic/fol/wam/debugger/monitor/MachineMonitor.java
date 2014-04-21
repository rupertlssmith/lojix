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

import com.thesett.aima.logic.fol.wam.debugger.InternalRegisterBean;
import com.thesett.aima.logic.fol.wam.machine.WAMResolvingMachineDPI;
import com.thesett.aima.logic.fol.wam.machine.WAMResolvingMachineDPIMonitor;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MachineMonitor implements WAMResolvingMachineDPIMonitor
{
    /** Holds a copy of the internal registers and monitors them for changes. */
    InternalRegisterBean internalRegisters;

    /** Holds the monitor to listen for changes to the register set. */
    RegisterSetMonitor registerSetMonitor = new RegisterSetMonitor();

    /** {@inheritDoc} */
    public void onReset(WAMResolvingMachineDPI dpi)
    {
        internalRegisters = new InternalRegisterBean(0, 0, 0, 0, 0, 0, 0, 0, 0, false);
        internalRegisters.setPropertyChangeListener(registerSetMonitor);
        internalRegisters.updateRegisters(dpi.getInternalRegisters());
    }

    /** {@inheritDoc} */
    public void onExecute(WAMResolvingMachineDPI dpi)
    {
        internalRegisters.updateRegisters(dpi.getInternalRegisters());
    }

    /** {@inheritDoc} */
    public void onStep(WAMResolvingMachineDPI dpi)
    {
        internalRegisters.updateRegisters(dpi.getInternalRegisters());
    }
}
