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

import com.thesett.aima.logic.fol.wam.debugger.swing.DebuggerLayout;
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
public class SwingPaneController implements PaneController
{
    private final DebuggerLayout debuggerLayout;
    private final String layout;

    /**
     * Creates a child pane controller for Swing components.
     *
     * @param debuggerLayout The layout manager,
     * @param layout         The layout position.
     */
    public SwingPaneController(DebuggerLayout debuggerLayout, String layout)
    {
        this.debuggerLayout = debuggerLayout;
        this.layout = layout;
    }

    /** {@inheritDoc} */
    public void showVerticalScrollBar()
    {
        debuggerLayout.addVerticalScrollBar(layout);
    }

    /** {@inheritDoc} */
    public void showHorizontalScrollBar()
    {
        debuggerLayout.addHorizontalScrollBar(layout);
    }
}
