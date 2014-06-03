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

import java.awt.Color;

import com.thesett.aima.logic.fol.wam.debugger.monitor.MemoryLayoutMonitor;
import com.thesett.aima.logic.fol.wam.debugger.swing.Fader;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextTable;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ControllerLifecycle;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.text.api.TextTableEvent;
import com.thesett.text.api.TextTableListener;

/**
 * MemoryLayoutMonitorController manages the memory layout monitor component. It creates this visual component and the
 * model behind it and wires them together. It is responsible for handling all user interaction with this component.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Build the memory layout monitor visual component. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MemoryLayoutMonitorController implements ControllerLifecycle
{
    /** Holds the component factory used to build the application components. */
    private final ComponentFactory componentFactory;

    /** Holds the main application window frame. */
    private final MainWindow mainWindow;

    /** The text grid model behind the UI component. */
    private EnhancedTextGrid grid;

    /** A text table model that maps down onto the text grid. */
    private EnhancedTextTable table;

    /** Holds the memory layout monitor that translates value changes into changes to the table. */
    private MemoryLayoutMonitor monitor;

    /** A color fader used to highlight layout value changes. */
    private Fader fader = new Fader(Color.DARK_GRAY, Color.BLACK);

    /**
     * Builds the UI controller for the register monitor.
     *
     * @param componentFactory The UI component factory.
     * @param mainWindow       The main window to create the UI component within.
     */
    public MemoryLayoutMonitorController(ComponentFactory componentFactory, MainWindow mainWindow)
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
        mainWindow.showConsole(componentFactory.createTextGridPanel(grid, null));

        // Build a table model on the text grid, and construct a register monitor on the table.
        table = (EnhancedTextTable) grid.createTable(0, 0, 20, 20);
        monitor = new MemoryLayoutMonitor(table);

        // Attach a listener for updates to the register table.
        table.addTextTableListener(new TableUpdateHandler());
    }

    /** {@inheritDoc} */
    public void close()
    {
    }

    /**
     * Provides access to the underlying layout register set monitor.
     *
     * @return The layout register set monitor.
     */
    public MemoryLayoutMonitor getLayoutMonitor()
    {
        return monitor;
    }

    /**
     * Triggers color fades on table row updates.
     */
    private class TableUpdateHandler implements TextTableListener
    {
        /** {@inheritDoc} */
        public void changedUpdate(TextTableEvent event)
        {
            // Ignore attribute only updates, as these could be events initiated by the fader itself, only trigger
            // a fade on content changes.
            if (!event.isAttributeChangeOnly())
            {
                int col = event.getColumnChanged();
                int row = event.getRowChanged();

                // Only trigger a fade if the row is valid and not the currently selected one.
                if ((row >= 0) && (col >= 0))
                {
                    fader.doFade(new CellBackgroundColorDelta(col, row, table),
                        Integer.toString(row) + "/" + Integer.toString(col));
                }
            }
        }
    }
}
