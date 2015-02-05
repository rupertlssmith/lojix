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

import java.beans.PropertyChangeEvent;

import com.thesett.aima.logic.fol.wam.debugger.monitor.BreakpointMonitor;
import com.thesett.aima.logic.fol.wam.debugger.monitor.ByteCodeMonitor;
import com.thesett.aima.logic.fol.wam.debugger.text.AttributeSet;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextTable;
import com.thesett.aima.logic.fol.wam.debugger.text.TextGridSelectionListener;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ControllerLifecycle;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.KeyShortcutMap;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.PaneController;
import com.thesett.text.api.TextGridEvent;

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
    /** Used for debugging. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(CodeStepController.class.getName());

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

    /** The current row corresponding to the instruction currently being stepped. <tt>-1</tt> means no stepped row. */
    private int steppedRow = -1;

    /** The current user selected table row. <tt>-1</tt> means no selected row. */
    private int selectedRow = -1;

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
        // Build a text grid panel in the central position.
        grid = componentFactory.createTextGrid();
        mainWindow.showCentrePane(componentFactory.createTextGridPanel(grid));

        PaneController paneController = mainWindow.getCentreController();
        paneController.showVerticalScrollBar();

        // Build a table model on the text grid, to display the code in.
        table = (EnhancedTextTable) grid.createTable(0, 0, 20, 20);

        breakpointMonitor = new BreakpointMonitorImpl();
        byteCodeMonitor = new ByteCodeMonitor(table);

        // Attach listeners for user events on the table.
        grid.addTextGridSelectionListener(new SelectionHandler());

        // Register some keyboard shortcuts to control the code stepping.
        KeyShortcutMap shortcutMap = componentFactory.getKeyShortcutMap();
        mainWindow.setKeyShortcut(shortcutMap.getStep(), "step", new Step());
        mainWindow.setKeyShortcut(shortcutMap.getStepOver(), "step_over", new StepOver());
        mainWindow.setKeyShortcut(shortcutMap.getResume(), "resume", new Resume());
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

    private class Step implements Runnable
    {
        public void run()
        {
            breakpointMonitor.release();
            log.fine("Step");
        }
    }

    private class StepOver implements Runnable
    {
        public void run()
        {
            breakpointMonitor.release();
            log.fine("StepOver");
        }
    }

    private class Resume implements Runnable
    {
        public void run()
        {
            breakpointMonitor.release();
            log.fine("Resume");
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

                if (row < table.getRowCount())
                {
                    // Clear any previously selected row.
                    if (selectedRow != -1)
                    {
                        grid.insertRowAttribute(null, selectedRow);

                        // Restore stepping highlight if needed.
                        if (selectedRow == steppedRow)
                        {
                            AttributeSet aset = new AttributeSet();
                            aset.put(AttributeSet.BACKGROUND_COLOR,
                                componentFactory.getColorScheme().getLowlightBackground());
                            grid.insertRowAttribute(aset, selectedRow);
                        }
                    }

                    selectedRow = row;

                    AttributeSet aset = new AttributeSet();
                    aset.put(AttributeSet.BACKGROUND_COLOR, componentFactory.getColorScheme().getSelectionBackground());
                    grid.insertRowAttribute(aset, selectedRow);
                }
            }
        }
    }

    /**
     * Triggers background color highlighting on currently stepped row.
     */
    public class BreakpointMonitorImpl extends BreakpointMonitor
    {
        /** The IP register at the current break point. */
        private int ip;

        /** {@inheritDoc} */
        public void propertyChange(PropertyChangeEvent evt)
        {
            String propertyName = evt.getPropertyName();

            if ("IP".equals(propertyName))
            {
                ip = (Integer) evt.getNewValue();

                log.fine("New IP : " + ip);

                highlightSteppedRow(byteCodeMonitor.getRowForAddress(ip));
            }
        }

        /** {@inheritDoc} */
        public void highlightSteppedRow(int row)
        {
            log.fine("Stepping highlight at : " + row);
            log.fine("table has " + table.getRowCount() + " rows.");

            if (row != steppedRow)
            {
                if (row < table.getRowCount())
                {
                    // Clear any previously selected row.
                    if (steppedRow != -1)
                    {
                        grid.insertRowAttribute(null, steppedRow);

                        // Restore selection highlight if needed.
                        if (selectedRow == steppedRow)
                        {
                            AttributeSet aset = new AttributeSet();
                            aset.put(AttributeSet.BACKGROUND_COLOR,
                                componentFactory.getColorScheme().getSelectionBackground());
                            grid.insertRowAttribute(aset, selectedRow);
                        }
                    }

                    steppedRow = row;

                    AttributeSet aset = new AttributeSet();
                    aset.put(AttributeSet.BACKGROUND_COLOR, componentFactory.getColorScheme().getHighlightBackground());
                    grid.insertRowAttribute(aset, steppedRow);
                }
            }
        }
    }
}
