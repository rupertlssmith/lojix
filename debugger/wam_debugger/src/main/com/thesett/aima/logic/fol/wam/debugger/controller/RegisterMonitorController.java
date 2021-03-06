/*
 * Copyright The Sett Ltd, 2005 to 2014.
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

import com.thesett.aima.logic.fol.wam.debugger.monitor.RegisterSetMonitor;
import com.thesett.aima.logic.fol.wam.debugger.text.AttributeSet;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.aima.logic.fol.wam.debugger.text.TextGridSelectionListener;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ControllerLifecycle;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.Fader;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.RowBackgroundColorDelta;
import com.thesett.text.api.TextGridEvent;
import com.thesett.text.api.TextTableEvent;
import com.thesett.text.api.TextTableListener;
import com.thesett.text.api.model.TextTableModel;

/**
 * RegisterMonitorController manages the register monitor component. It creates this visual component and the model
 * behind it and wires them together. It is responsible for handling all user interaction with this component.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Build the register monitor visual component. </td></tr>
 * <tr><td> Handle mouse interaction with the register table. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class RegisterMonitorController implements ControllerLifecycle
{
    /** Used for debugging. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(RegisterMonitorController.class.getName());

    /** Holds the component factory used to build the application components. */
    private final ComponentFactory componentFactory;

    /** Holds the main application window frame. */
    private final MainWindow mainWindow;

    /** The text grid model behind the UI component. */
    private EnhancedTextGrid grid;

    /** A text table model that maps down onto the text grid. */
    private TextTableModel table;

    /** The register set monitor that translates value changes on registers into updates to the table. */
    private RegisterSetMonitor monitor;

    /** A color fader used to highlight register value changes. */
    private final Fader fader;

    /** The current user selected table row. <tt>-1</tt> means no selected row. */
    private int selectedRow = -1;

    /**
     * Builds the UI controller for the register monitor.
     *
     * @param componentFactory The UI component factory.
     * @param mainWindow       The main window to create the UI component within.
     */
    public RegisterMonitorController(ComponentFactory componentFactory, MainWindow mainWindow)
    {
        this.componentFactory = componentFactory;
        this.mainWindow = mainWindow;

        ColorScheme colorScheme = componentFactory.getColorScheme();
        fader =
            componentFactory.getColorFactory().createFader(colorScheme.getLowlightBackground(),
                colorScheme.getBackground());
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
        grid.insertVerticalSeparator(3, 10);

        mainWindow.showLeftPane(componentFactory.createTextGridPanel(grid));

        // Build a table model on the text grid, and construct a register monitor on the table.
        table = grid.createTable(0, 0, 20, 20);
        monitor = new RegisterSetMonitor(table);

        // Attach a listener for updates to the register table.
        table.addTextTableListener(new TableUpdateHandler());
        grid.addTextGridSelectionListener(new SelectionHandler());
    }

    /** {@inheritDoc} */
    public void close()
    {
    }

    /**
     * Provides access to the underlying register set monitor.
     *
     * @return The register set monitor.
     */
    public RegisterSetMonitor getRegisterMonitor()
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
            int row = event.getRowChanged();

            // Only trigger a fade if the row is valid and not the currently selected one.
            if ((row >= 0) && (row != selectedRow))
            {
                fader.doFade(new RowBackgroundColorDelta(row, grid), Integer.toString(row));
            }
        }
    }

    /**
     * Triggers background color highlighting on user row selection.
     */
    private class SelectionHandler implements TextGridSelectionListener
    {
        /** {@inheritDoc} */
        public void select(TextGridEvent e)
        {
            int row = e.getRow();

            if (row != selectedRow)
            {
                log.fine("New mouse selection at : " + e.getColumn() + ", " + row);

                AttributeSet aset = new AttributeSet();
                aset.put(AttributeSet.BACKGROUND_COLOR, componentFactory.getColorScheme().getSelectionBackground());

                if (row < table.getRowCount())
                {
                    // Clear any previously selected row.
                    if (selectedRow != -1)
                    {
                        grid.insertRowAttribute(null, selectedRow);
                    }

                    selectedRow = row;
                    grid.insertRowAttribute(aset, selectedRow);
                }
            }
        }
    }
}
