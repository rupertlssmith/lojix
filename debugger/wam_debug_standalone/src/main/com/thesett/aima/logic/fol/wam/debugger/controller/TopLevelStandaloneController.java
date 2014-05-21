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
package com.thesett.aima.logic.fol.wam.debugger.controller;

import com.thesett.aima.logic.fol.wam.debugger.DarkColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.monitor.MachineMonitor;
import com.thesett.aima.logic.fol.wam.debugger.swing.ColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ControllerLifecycle;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.impl.SwingComponentFactory;

/**
 * TopLevelStandaloneController is the top-level controller for the debugger UI. It is responsible for initializing the
 * debugger user interface, building the debugger data model and sub-controller components, and attaching all of these
 * components together to form the debugger application. It is also responsible for closing down the debugger
 * application components. To summarize, it is responsible for handling the top-level lifecycle events to create and
 * destroy the debugger application.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Build the debugger application components. </td></tr>
 * <tr><td> Destroy the debugger application components. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TopLevelStandaloneController implements ControllerLifecycle
{
    /** Defines the application colour scheme. */
    private ColorScheme colorScheme = new DarkColorScheme();

    /** Holds the component factory used to build the application components. */
    private ComponentFactory componentFactory = new SwingComponentFactory(colorScheme);

    /** Holds the main application window frame. */
    private MainWindow mainWindow = componentFactory.createMainWindow();

    /** Holds the top-level machine monitor that is attached to the DPI of the machine being debugged. */
    private MachineMonitor machineMonitor;

    /** Holds the controller for the register set monitor. */
    private RegisterMonitorController registerMonitorController;

    /** Holds the controller for the memeory layout monitor. */
    private MemoryLayoutMonitorController memoryLayoutMonitorController;

    /**
     * {@inheritDoc}
     *
     * <p/>Creates the debugger application from its components.
     */
    public void open()
    {
        // Build the main window frame.
        mainWindow.showMainWindow();

        // Blank out the centre panel for now.
        mainWindow.showCentrePane(componentFactory.createBlankPanel());

        // Create and initialize the register monitor.
        registerMonitorController = new RegisterMonitorController(componentFactory, mainWindow);
        registerMonitorController.open();

        // Create and initialize the memory layout monitor.
        memoryLayoutMonitorController = new MemoryLayoutMonitorController(componentFactory, mainWindow);
        memoryLayoutMonitorController.open();

        // Build the top-level machine monitor.
        machineMonitor =
            new MachineMonitor(registerMonitorController.getRegisterMonitor(),
                memoryLayoutMonitorController.getLayoutMonitor());
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Destroys the debugger application components.
     */
    public void close()
    {
        registerMonitorController.close();
    }

    public MachineMonitor getMachineMonitor()
    {
        return machineMonitor;
    }
}