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
package com.thesett.common.swing.workflow;

import java.beans.PropertyChangeEvent;

import com.thesett.common.swing.workpanel.WorkPanelState;

/**
 * WorkFlowState models the possible states of a work flow. A work flow is a series of user inputs (often dialog boxes)
 * that progress through stages of a process interactively. They are frequently used as wizards.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 *
 * <tr>
 * <th>Hold the state of the current work flow screen.
 *
 * <tr>
 * <th>Notify listeners of changes of screen state.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WorkFlowState extends WorkPanelState
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(WorkFlowState.class.getName()); */

    /** Holds the state of the current work flow screen. */
    private WorkFlowScreenState currentScreenState;

    /**
     * Sets the new current screen state and notifies all listeners of the change in screen state.
     *
     * @param state The current screen state.
     */
    public void setCurrentScreenState(WorkFlowScreenState state)
    {
        /*log.fine("void setCurrentScreenState(WorkFlowScreenState): called");*/

        WorkFlowScreenState oldState = currentScreenState;

        // Keep the new state.
        currentScreenState = state;

        // Notify all listeners of the change of current screen.
        firePropertyChange(new PropertyChangeEvent(this, "currentScreenState", oldState, state));
    }

    /**
     * Gets the current screen state.
     *
     * @return The current screen state.
     */
    public WorkFlowScreenState getCurrentScreenState()
    {
        return currentScreenState;
    }
}
