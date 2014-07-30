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
package com.thesett.common.swing.workpanel;

import java.beans.PropertyChangeEvent;

import com.thesett.common.state.BaseState;

/**
 * WorkPanelState models the possible states of a work panel.
 *
 * <p>A work panel can have three states, not intialized, ready and not saved. A newly created work panel that has not
 * loaded its data or built its user interface will be in the not initialized state. A work panel that has loaded its
 * data and built its user interface will be in the ready state. A work panel that has modified its data but not yet
 * commited the changes to the server will be in the not intialized state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Maintain work panel state
 * <tr><td>Notify listners of changes to state
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WorkPanelState extends BaseState
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(WorkPanelState.class.getName()); */

    /** Constant representing the initial state of the work panel before it is ready to be used. */
    public static final String NOT_INITIALIZED = "Not Initialized";

    /** Constant representing the state of the work panel when its data is loaded and it is ready to be edited. */
    public static final String READY = "Ready";

    /**
     * Constant representing the state of the work panel when its data has been changed but the changes have not yet
     * been commited or canceled.
     */
    public static final String NOT_SAVED = "Not Saved";

    /** Represents the initial state of a work panel before it is ready to use. */
    private String state = NOT_INITIALIZED;

    /**
     * Sets the state of the work panel. State must be one of the defined constants: NOT_INITIALIZED, READY or
     * NOT_SAVED.
     *
     * <p/>Listneres are notified of the state change if it is a change.
     *
     * @param state The new state to set.
     */
    public void setState(String state)
    {
        /*log.fine("void setState(String): called");*/
        /*log.fine("state is " + state);*/

        // Check if the state has changed
        if (!this.state.equals(state))
        {
            String oldState = this.state;

            // Keep the new state
            this.state = state;

            // Notify any listeners of the change in state
            firePropertyChange(new PropertyChangeEvent(this, "state", oldState, state));
        }
    }

    /**
     * Returns the state of the work panel.
     *
     * @return The current state of the work panel.
     */
    public String getState()
    {
        return state;
    }
}
