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

import javax.swing.text.Document;

import com.thesett.aima.logic.fol.wam.debugger.swing.MotionDelta;

/**
 * ComponentFactory builds UI components that are compatible with other elements of the UI.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create the main window. </td></tr>
 * <tr><td> Create a grip-able panel for adjusting screen layout. </td></tr>
 * <tr><td> Create an editor panel. </td></tr>
 * <tr><td> Create a black panel for filling in unused areas of the screen. </td></tr>
 * </table></pre>
 *
 * @param  <C> The type of ui components this factory produces.
 *
 * @author Rupert Smith
 */
public interface ComponentFactory<C>
{
    /**
     * Creates the main debugger window.
     *
     * @return The main debugger window.
     */
    MainWindow<C> createMainWindow();

    /**
     * Creates an editor panel.
     *
     * @param  document The underlying document data model.
     *
     * @return An editor panel.
     */
    C createEditor(Document document);

    /**
     * Creates a grip-able panel for adjusting the screen layout.
     *
     * @param  motionDelta The motion controller to attach to the grip-able panel.
     *
     * @return A grip-able panel.
     */
    C createGripPanel(MotionDelta motionDelta);

    /**
     * Creates a blank panel for filling empty space.
     *
     * @return A blank panel for filling empty space.
     */
    C createBlankPanel();
}
