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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.thesett.aima.logic.fol.wam.debugger.monitor.RegisterSetMonitor;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
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
public class RegisterMonitorController
{
    /** Holds the component factory used to build the application components. */
    private final ComponentFactory componentFactory;

    /** Holds the main application window frame. */
    private final MainWindow mainWindow;

    private EnhancedTextGrid grid;
    private TextTableModel table;
    private RegisterSetMonitor monitor;

    private int selectedRow = -1;
    private Timer timer;

    public RegisterMonitorController(ComponentFactory componentFactory, MainWindow mainWindow)
    {
        this.componentFactory = componentFactory;
        this.mainWindow = mainWindow;
    }

    public void open()
    {
        // Build a text grid panel in the left position.
        grid = componentFactory.createTextGrid();
        mainWindow.showLeftPane(componentFactory.createTextGridPanel(grid, new MouseHandler()));

        // Build a table model on the text grid, and construct a register monitor on the table.
        table = grid.createTable(0, 0, 20, 20);
        monitor = new RegisterSetMonitor(table);
    }

    public void close()
    {
    }

    public RegisterSetMonitor getRegisterMonitor()
    {
        return monitor;
    }

    private class MouseHandler extends MouseInputAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            int row = e.getY();

            if (row != selectedRow)
            {
                System.out.println("New mouse selection at : " + e.getX() + ", " + row);

                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet aset =
                    sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.LIGHT_GRAY);

                if (row < table.getRowCount())
                {
                    // Clear any previously selected row.
                    if (selectedRow != -1)
                    {
                        grid.insertRowAttribute(null, selectedRow);
                    }

                    selectedRow = row;
                    grid.insertRowAttribute(aset, selectedRow);

                    // Kick off the fade timer.
                    timer = new Timer(100, new FadeHandler());
                    timer.setInitialDelay(100);
                    timer.start();
                }
            }
        }
    }

    private class FadeHandler implements ActionListener
    {
        Color color = Color.LIGHT_GRAY;

        public void actionPerformed(ActionEvent e)
        {
            if (color.getRed() > 0)
            {
                color =
                    new Color(color.getRed() - (color.getRed() / 2), color.getGreen() - (color.getGreen() / 2),
                        color.getBlue() - (color.getBlue() / 2));

                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, color);
                grid.insertRowAttribute(aset, selectedRow);

                timer.restart();
            }
        }
    }
}
