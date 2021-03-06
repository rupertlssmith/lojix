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
package com.thesett.aima.logic.fol.wam.debugger.uifactory;

/**
 * PaneController is a controller to apply behaviour to a child pane of the {@link MainWindow}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Add scroll bars to child panes. </td></tr>
 * <tr><td> Add keyboard short cuts to child panes. </td></tr>
 * </table></pre>
 *
 * @param  <K> The type of key combination representations used.
 *
 * @author Rupert Smith
 */
public interface PaneController<K>
{
    /** Displays a vertical scroll bar in the child pane. */
    void showVerticalScrollBar();

    /** Displays a horizontal scroll bar in the child pane. */
    void showHorizontalScrollBar();

    /**
     * Provides a {@link ScrollController} for the vertical scroll bar.
     *
     * @return A {@link ScrollController} for the vertical scroll bar.
     */
    ScrollController getVerticalScrollController();

    /**
     * Provides a {@link ScrollController} for the horizontal scroll bar.
     *
     * @return A {@link ScrollController} for the horizontal scroll bar.
     */
    ScrollController getHorizontalScrollController();

    /**
     * Establishes a keyboard shortcut, that is triggered when the child pane has focus.
     *
     * @param keyCombination The key combination for the shortcut.
     * @param actionName     A unique name for the action.
     * @param action         The action to invoke.
     */
    void setKeyShortcut(K keyCombination, String actionName, Runnable action);
}
