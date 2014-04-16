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
package com.thesett.aima.logic.fol.wam.debugger.uifactory.impl;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.thesett.aima.logic.fol.wam.debugger.swing.DebuggerLayout;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;

/**
 * SwingMainWindow implements the main debugger application window as a stand-alone Swing application.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Show/hide the main window. </td><td> {@link ComponentFactory}</td></tr>
 * <tr><td> Allow UI component to be placed within the centre, left, right or console position.</td>
 *     <td> {@link ComponentFactory}</td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SwingMainWindow implements MainWindow<Component>
{
    /** The application window frame. */
    private JFrame frame;

    /** A customized Swing layout for this application. */
    private DebuggerLayout layout;

    /** The component factory used to build UI elements. */
    private final ComponentFactory<Component> factory;

    /**
     * Creates the main debugger window, implement as a standalone Swing application window.
     *
     * @param factory The component factory used to build UI elements.
     */
    public SwingMainWindow(ComponentFactory<Component> factory)
    {
        this.factory = factory;
    }

    /** {@inheritDoc} */
    public void showMainWindow()
    {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(1000, 800));

        layout = new DebuggerLayout();
        frame.getContentPane().setLayout(layout);

        frame.setVisible(true);
    }

    /** {@inheritDoc} */
    public void showCentrePane(Component component)
    {
        frame.getContentPane().add(component, DebuggerLayout.CENTER);
        frame.pack();
    }

    /** {@inheritDoc} */
    public void showConsole(Component component)
    {
        showHorizontalBar();
        frame.getContentPane().add(component, DebuggerLayout.CONSOLE);
        frame.pack();
    }

    /** {@inheritDoc} */
    public void showLeftPane(Component component)
    {
        showLeftBar();
        frame.getContentPane().add(component, DebuggerLayout.LEFT_PANE);
        frame.pack();
    }

    /** {@inheritDoc} */
    public void showRightPane(Component component)
    {
        showRightBar();
        frame.getContentPane().add(component, DebuggerLayout.RIGHT_PANE);
        frame.pack();
    }

    /** Creates a horizontal grip-able bar for adjusting the console height. */
    private void showHorizontalBar()
    {
        // Left vertical bar.
        Component bar = factory.createGripPanel(layout.getConsoleHeightResizer());

        frame.getContentPane().add(bar, DebuggerLayout.STATUS_BAR);
    }

    /** Creates a vertical grip-able bar for adjusting the left panel width. */
    private void showLeftBar()
    {
        // Left vertical bar.
        Component bar = factory.createGripPanel(layout.getLeftPaneWidthResizer());

        frame.getContentPane().add(bar, DebuggerLayout.LEFT_VERTICAL_BAR);
    }

    /** Creates a vertical grip-able bar for adjusting the right panel width. */
    private void showRightBar()
    {
        // Right vertical bar.
        Component bar = factory.createGripPanel(layout.getRightPaneWidthResizer());

        frame.getContentPane().add(bar, DebuggerLayout.RIGHT_VERTICAL_BAR);
    }
}
