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
package com.thesett.aima.logic.fol.wam.debugger;

import com.thesett.aima.logic.fol.wam.debugger.monitor.MachineMonitor;
import com.thesett.aima.logic.fol.wam.debugger.monitor.RegisterSetMonitor;
import com.thesett.aima.logic.fol.wam.debugger.swing.ColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.impl.SwingComponentFactory;
import com.thesett.text.api.model.TextGridModel;
import com.thesett.text.api.model.TextTableModel;

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
public class TopLevelStandaloneController
{
    /** Defines the application colour scheme. */
    private ColorScheme colorScheme = new DarkColorScheme();

    /** Holds the component factory used to build the application components. */
    private ComponentFactory componentFactory = new SwingComponentFactory(colorScheme);

    /** Holds the main application window frame. */
    private MainWindow mainWindow = componentFactory.createMainWindow();

    /** Holds the top-level machine monitor that is attached to the DPI of the machine being debugged. */
    private MachineMonitor machineMonitor;

    /** Creates the debugger application from its components. */
    public void open()
    {
        // Build the main window frame.
        mainWindow.showMainWindow();

        // Blank out the centre panel for now.
        mainWindow.showCentrePane(componentFactory.createBlankPanel());

        // Build a text grid panel in the left position.
        TextGridModel textGrid = componentFactory.createTextGrid();
        mainWindow.showLeftPane(componentFactory.createTextGridPanel(textGrid));

        // Build a table model on the text grid, and construct a register monitor on the table.
        TextTableModel table = textGrid.createTable(0, 0, 20, 20);
        RegisterSetMonitor registerSetMonitor = new RegisterSetMonitor(table);

        // Build the top-level machine monitor.
        machineMonitor = new MachineMonitor(registerSetMonitor);
    }

    /** Destroys the debugger application components. */
    public void close()
    {
    }

    public MachineMonitor getMachineMonitor()
    {
        return machineMonitor;
    }
}