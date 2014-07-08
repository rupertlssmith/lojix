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

import javax.swing.*;

import com.thesett.aima.logic.fol.wam.debugger.swing.DebuggerLayout;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.PaneController;

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
public class SwingMainWindow implements MainWindow<JComponent, KeyStroke>
{
    /** The application window frame. */
    private JFrame frame;

    /** A customized Swing layout for this application. */
    private DebuggerLayout layout;

    /** The component factory used to build UI elements. */
    private final SwingComponentFactory factory;

    /** The centre component. */
    private JComponent centreComponent;

    /** The console component. */
    private JComponent consoleComponent;

    /** The left component. */
    private JComponent leftComponent;

    /** The right component. */
    private JComponent rightComponent;

    /**
     * Creates the main debugger window, implement as a standalone Swing application window.
     *
     * @param factory The component factory used to build UI elements.
     */
    public SwingMainWindow(SwingComponentFactory factory)
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

        JRootPane rootPane = frame.getRootPane();
        rootPane.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "pressed");
    }

    /** {@inheritDoc} */
    public void showCentrePane(JComponent component)
    {
        frame.getContentPane().add(component, DebuggerLayout.CENTER);
        frame.pack();

        centreComponent = component;
    }

    /** {@inheritDoc} */
    public PaneController<KeyStroke> getCentreController()
    {
        return new SwingPaneController(centreComponent);
    }

    /** {@inheritDoc} */
    public void showConsole(JComponent component)
    {
        showHorizontalBar();
        frame.getContentPane().add(component, DebuggerLayout.CONSOLE);
        frame.pack();

        consoleComponent = component;
    }

    /** {@inheritDoc} */
    public void showLeftPane(JComponent component)
    {
        showLeftBar();
        frame.getContentPane().add(component, DebuggerLayout.LEFT_PANE);
        frame.pack();

        leftComponent = component;
    }

    /** {@inheritDoc} */
    public void showRightPane(JComponent component)
    {
        showRightBar();
        frame.getContentPane().add(component, DebuggerLayout.RIGHT_PANE);
        frame.pack();

        rightComponent = component;
    }

    /** {@inheritDoc} */
    public void setKeyShortcut(KeyStroke keyCombination, String actionName, Runnable action)
    {
        frame.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyCombination, actionName);
        frame.getRootPane().getActionMap().put(actionName, new RunnableAction(action));
    }

    /** Creates a horizontal grip-able bar for adjusting the console height. */
    private void showHorizontalBar()
    {
        // Left vertical bar.
        Component bar = factory.createGripPanel(layout.getConsoleHeightResizer(), false);

        frame.getContentPane().add(bar, DebuggerLayout.STATUS_BAR);
    }

    /** Creates a vertical grip-able bar for adjusting the left panel width. */
    private void showLeftBar()
    {
        // Left vertical bar.
        Component bar = factory.createGripPanel(layout.getLeftPaneWidthResizer(), true);

        frame.getContentPane().add(bar, DebuggerLayout.LEFT_VERTICAL_BAR);
    }

    /** Creates a vertical grip-able bar for adjusting the right panel width. */
    private void showRightBar()
    {
        // Right vertical bar.
        Component bar = factory.createGripPanel(layout.getRightPaneWidthResizer(), true);

        frame.getContentPane().add(bar, DebuggerLayout.RIGHT_VERTICAL_BAR);
    }
}
