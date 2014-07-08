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

import javax.swing.*;

import com.thesett.aima.logic.fol.wam.debugger.uifactory.PaneController;

/**
 * SwingPaneController is a {@link PaneController} for Swing based window panes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Add scroll bars to child panes. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SwingPaneController implements PaneController<KeyStroke>
{
    /** Holds the component this is a controller for. */
    private final JComponent component;

    /** Holds the component scroll pane, if it is of the scroll pane type. */
    private final JScrollPane scrollPane;

    /**
     * Creates a child pane controller for Swing components.
     *
     * @param component The component that his is a controller for.
     */
    public SwingPaneController(JComponent component)
    {
        this.component = component;
        scrollPane = (component instanceof JScrollPane) ? (JScrollPane) component : null;
    }

    /** {@inheritDoc} */
    public void showVerticalScrollBar()
    {
        if (scrollPane != null)
        {
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        }
    }

    /** {@inheritDoc} */
    public void showHorizontalScrollBar()
    {
        if (scrollPane != null)
        {
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        }
    }

    /** {@inheritDoc} */
    public void setKeyShortcut(KeyStroke keyCombination, String actionName, Runnable action)
    {
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyCombination, actionName);
        component.getActionMap().put(actionName, new RunnableAction(action));
    }
}
