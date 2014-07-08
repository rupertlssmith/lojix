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
package com.thesett.aima.logic.fol.wam.debugger.uifactory;

/**
 * MainWindow defines the behaviour of the debugger main window.
 *
 * <p/>The main window follows a similar layout pattern to many GUI development tools, with centre, left and right, and
 * console positions within it.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Show/hide the main window. </td></tr>
 * <tr><td> Allow UI component to be placed within the centre, left, right or console position.</td></tr>
 * </table></pre>
 *
 * @param  <C> The type of UI component this window works with.
 * @param  <K> The type of key combination representations used.
 *
 * @author Rupert Smith
 */
public interface MainWindow<C, K>
{
    /** Makes the main window visible. */
    void showMainWindow();

    /**
     * Displays a component in the centre position.
     *
     * @param component The component to display.
     */
    void showCentrePane(C component);

    /**
     * Provides the pane controller for the centre pane.
     *
     * @return The pane controller for the centre pane.
     */
    PaneController<K> getCentreController();

    /**
     * Displays a component in the console position.
     *
     * @param component The component to display.
     */
    void showConsole(C component);

    /**
     * Displays a component in the left position.
     *
     * @param component The component to display.
     */
    void showLeftPane(C component);

    /**
     * Displays a component in the right position.
     *
     * @param component The component to display.
     */
    void showRightPane(C component);

    /**
     * Establishes a keyboard shortcut, that works globally accross all components.
     *
     * @param keyCombination The key combination for the shortcut.
     * @param actionName     A unique name for the action.
     * @param action         The action to invoke.
     */
    void setKeyShortcut(K keyCombination, String actionName, Runnable action);
}
