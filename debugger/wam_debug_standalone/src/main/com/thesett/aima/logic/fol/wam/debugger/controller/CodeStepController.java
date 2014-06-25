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

import com.thesett.aima.logic.fol.wam.debugger.monitor.BreakpointMonitor;
import com.thesett.aima.logic.fol.wam.debugger.monitor.ByteCodeMonitor;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextTable;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ControllerLifecycle;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;

/**
 * CodeStepController is the UI controller for the code debugging window. It is responsible for displaying the code
 * being debugged, and the current break-point within it. It also handles all user interaction with the code,
 * principally stepping through it.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Display the code being debugged. </td></tr>
 * <tr><td> Display the current break-point. </td></tr>
 * <tr><td> Handle user input to step the code. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CodeStepController implements ControllerLifecycle
{
    /** Holds the component factory used to build the application components. */
    private final ComponentFactory componentFactory;

    /** Holds the main application window frame. */
    private final MainWindow mainWindow;

    /** The text grid model behind the UI component. */
    private EnhancedTextGrid grid;

    /** A text table model that maps down onto the text grid. */
    private EnhancedTextTable table;

    /** Provides the current break-point. */
    private BreakpointMonitor breakpointMonitor;

    /** Provides the current byte code loaded in the target machine. */
    private ByteCodeMonitor byteCodeMonitor;

    /**
     * Builds the UI controller for the register monitor.
     *
     * @param componentFactory The UI component factory.
     * @param mainWindow       The main window to create the UI component within.
     */
    public CodeStepController(ComponentFactory componentFactory, MainWindow mainWindow)
    {
        this.componentFactory = componentFactory;
        this.mainWindow = mainWindow;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Creates the register monitor component and hooks it up to the register set.
     */
    public void open()
    {
        // Build a text grid panel in the left position.
        grid = componentFactory.createTextGrid();
        grid.insert("test", 1, 1);
        mainWindow.showCentrePane(componentFactory.createTextGridPanel(grid, null));
        mainWindow.getCentrePaneController().showVerticalScrollBar();

        // Build a table model on the text grid, to display the code in.
        table = (EnhancedTextTable) grid.createTable(0, 0, 20, 20);

        breakpointMonitor = new BreakpointMonitor();
        byteCodeMonitor = new ByteCodeMonitor(table);
    }

    /** {@inheritDoc} */
    public void close()
    {
    }

    /**
     * Provides access to the underlying break-point monitor.
     *
     * @return The break-point monitor.
     */
    public BreakpointMonitor getBreakpointMonitor()
    {
        return breakpointMonitor;
    }

    /**
     * Provides access to the underlying byte code monitor.
     *
     * @return The byte code monitor.
     */
    public ByteCodeMonitor getByteCodeMonitor()
    {
        return byteCodeMonitor;
    }
}
