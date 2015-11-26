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
 * WorkFlowScreenState models the possible states of an individual screen within a work flow. A work flow is a series of
 * user inputs (often dialog boxes) that progress through stages of a process interactively. They are frequently used as
 * wizards.
 *
 * <p/>WorkFlowScreenState inherits its fundamental state model from
 * {@link com.thesett.common.swing.workpanel.WorkPanelState} which has three states. These and NOT_INITIALIZED, the
 * state of a screen that is not yet ready to accept user input, READY, the state of a screen which is ready to accept
 * user input but is still in a clean state, and NOT_SAVED the state of a screen that has some user input but which has
 * not yet commited or done anything with that input. A NOT_SAVED screen has input but is waiting for the completion of
 * the entire work flow before it saves its state, or it may alternatively commit its state when the next or previous
 * page is requested.
 *
 * <p/>The screen state also implements flags to determine whether or not a previous or next page is avaliable from this
 * one and whether or not its current state means that the entire work flow may be finished at this point. Screen
 * implementations may call the set methods for these flags and property change event will be triggered to notify the
 * controlling button panel of the fact that these flags have changed. The button panel will update the enabled/disabled
 * status of its buttons to reflect the current state of the flags.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Maintain state of a screen in a work flow
 * <tr><td>Provide flags for next and prev pages and for completion of work flow
 * <tr><td>Notify event listeners of changes to any properties.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WorkFlowScreenState extends WorkPanelState
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(WorkFlowScreenState.class.getName()); */

    /** Flag determining whether another page is available in the work flow. */
    private boolean nextAvailable;

    /** Flag determining whether a pervious page is available in the work flow. */
    private boolean prevAvailable;

    /** Flag determining whether the state of the screen means that the entire flow may be ended at this point. */
    private boolean finished;

    /**
     * Checks if the next button should be enabled.
     *
     * @return The state of the next available flag.
     */
    public boolean isNextAvailable()
    {
        return nextAvailable;
    }

    /**
     * Checks if the back button should be enabled.
     *
     * @return The state of the previous available flag.
     */
    public boolean isPrevAvailable()
    {
        return prevAvailable;
    }

    /**
     * Checks if the entire work flow is complete.
     *
     * @return The work flow complete flag.
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Sets the state of the next available flag and notifies any listeners of this change.
     *
     * @param avail The next page available state.
     */
    public void setNextAvailable(boolean avail)
    {
        // Check if the state has changed
        if (nextAvailable != avail)
        {
            // Keep the new state
            nextAvailable = avail;

            // Notify any listeners fo the change in state
            firePropertyChange(new PropertyChangeEvent(this, "nextAvailable", !avail, avail));
        }
    }

    /**
     * Sets the state of the previous available flag and notifies any listeners of this change.
     *
     * @param avail The previous page available state.
     */
    public void setPrevAvailable(boolean avail)
    {
        // Check if the state has changed
        if (prevAvailable != avail)
        {
            // Keep the new state
            prevAvailable = avail;

            // Notify any listeners fo the change in state
            firePropertyChange(new PropertyChangeEvent(this, "prevAvailable", !avail, avail));
        }
    }

    /**
     * Sets the state of the finished and notifies any listeners of this change.
     *
     * @param avail The work flow completed state.
     */
    public void setFinished(boolean avail)
    {
        /*log.fine("void setFinished(boolean): called");*/

        // Check if the state has changed
        if (finished != avail)
        {
            // Keep the new state
            finished = avail;

            // Notify any listeners fo the change in state
            firePropertyChange(new PropertyChangeEvent(this, "finished", !avail, avail));

            /*log.fine("fired property change event");*/
        }
    }
}
