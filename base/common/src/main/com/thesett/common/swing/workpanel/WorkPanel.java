/*
 * Copyright The Sett Ltd, 2005 to 2009.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * A WorkPanel is a user interface element that is used to build screens that use a commit or cancel workflow. This is
 * usually implemented by having an ok and a cancel button and possibly also an apply button. The idea is that
 * WorkPanels will allow a user to edit data and then either drop their changes if cancel is selected or commit their
 * changes if ok or apply is selected.
 *
 * <p>WorkPanel interacts with three possible actions: Ok, Cancel and Apply. A WorkPanel has methods that will be called
 * to inform the panel when it should set up its data, when it is made visible and when it is made invisible.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Handle OK, Cancel and Apply actions
 * <tr><td>Initialize
 * <tr><td>Save data
 * <tr><td>Discard data
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class WorkPanel extends JPanel implements ActionListener
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(WorkPanel.class.getName()); */

    /** Holds the state of this work panel. */
    protected WorkPanelState state;

    /** Creates a new WorkPanel object. */
    public WorkPanel()
    {
        state = new WorkPanelState();
    }

    /**
     * This method will be called when the panel is to be displayed. If the state is NOT_INTIALIZED it should load its
     * data, and build its user interface components and set the state to READY. If the state is READY or NOT_SAVED it
     * should not do anything.
     */
    public abstract void initialize();

    /**
     * This method should be implemented to save the state of the WorkPanel. It is called when an Ok or an Apply event
     * is triggered.
     */
    public abstract void saveWork();

    /**
     * This method should be implemented to discard the state of the WorkPanel. It is called when a Cancel event is
     * triggered.
     */
    public abstract void discardWork();

    /**
     * Gets the work panel state.
     *
     * @return The work panel state.
     */
    public WorkPanelState getWorkPanelState()
    {
        return state;
    }

    /**
     * Listens for the button events Ok, Cancel and Apply. If the event is Ok or Apply the saveWork method is triggered.
     * If the event is Cancel then the discardWork method is triggered.
     *
     * @param event The work panel event, ignored if it is not one of "Ok", "Cancel" or "Apply".
     */
    public void actionPerformed(ActionEvent event)
    {
        /*log.fine("void actionPerformed(ActionEvent): called");*/
        /*log.fine("Action is " + event.getActionCommand());*/

        // Check which action was performed
        String action = event.getActionCommand();

        if ("OK".equals(action))
        {
            // Check if the state is NOT_SAVED
            if (state.getState().equals(WorkPanelState.NOT_SAVED))
            {
                // Save the work
                saveWork();
            }
        }
        else if ("Cancel".equals(action))
        {
            // Check if the state is NOT_SAVED
            if (state.getState().equals(WorkPanelState.NOT_SAVED))
            {
                // Discard the work
                discardWork();
            }
        }
        else if ("Apply".equals(action))
        {
            // Check if the state is NOT_SAVED
            if (state.getState().equals(WorkPanelState.NOT_SAVED))
            {
                // Save the work
                saveWork();
            }
        }
    }

    /**
     * This method should be called once the data in the panel has been changed. It should set the state to NOT_SAVED.
     */
    public void hasChanged()
    {
        // Change the state to NOT_SAVED
        state.setState(WorkPanelState.NOT_SAVED);
    }
}
