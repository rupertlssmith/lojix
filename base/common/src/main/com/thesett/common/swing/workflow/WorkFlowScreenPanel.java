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

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

/**
 * A WorkFlowScreenPanel is a screen within a work flow. It has an underlying state, {@link WorkFlowScreenState} that
 * allows it to be manipulated as part of a work flow and also for controlling components to react to changes in the
 * pages state.
 *
 * <p/>Implementation should provide the {@link #saveWork}, {@link #discardWork}, {@link #nextPage}, {@link #prevPage}
 * methods with suitable reactions to these events. These methods will be triggered by a supervising controller derived
 * from {@link WorkFlowController} at appropriate times.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>React to save, discard, next and previous events.
 * <tr><td>Provide helper methods to manipulate underlying state flags.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class WorkFlowScreenPanel extends JPanel // implements ActionListener
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(WorkFlowScreenPanel.class.getName()); */

    /** Holds the state of this work flow screen. */
    protected WorkFlowScreenState state;

    /** Creates a new WorkFlowScreenPanel object. */
    public WorkFlowScreenPanel()
    {
        state = new WorkFlowScreenState();
    }

    /**
     * This method will be called when the panel is to be displayed. If the state is NOT_INTIALIZED it should load its
     * data, and build its user interface components and set the state to READY. If the state is READY or NOT_SAVED it
     * should not do anything.
     */
    public abstract void initialize();

    /**
     * Called when the entire work flow is completed. If the screen is in the NOT_SAVED state it should now save its
     * contents or othewise use them to fulfill the intention of the work flow.
     */
    public abstract void saveWork();

    /**
     * Called if the entire work flow is canceled. If the screen is in the NOT_SAVED state it should abandon its
     * contents and possibly revert to its original state or simply NOT_INTIIALIZED.
     */
    public abstract void discardWork();

    /**
     * Gets the current work flow screen state.
     *
     * @return The current work flow screen state.
     */
    public WorkFlowScreenState getState()
    {
        return state;
    }

    /**
     * Listens for the back, next, finish and cancel events.
     *
     * @param event The work flow action event.
     */
    public void actionPerformed(ActionEvent event)
    {
        /*log.fine("void actionPerformed(ActionEvent): called");*/
        /*log.fine("Action is " + event.getActionCommand());*/
    }

    /**
     * Called when the next page is selected. If the screen is in the NOT_SAVED state at this point in time it may wish
     * to save its contents or othwerwise use them to fulfill the intention of the work flow and then revert to the
     * READY or NOT_INITIALIZED state.
     *
     * @return Always null.
     */
    public WorkFlowScreenPanel nextPage()
    {
        return null;
    }

    /**
     * Called when the previous page is selected. The page may decide to abandon its work and revert to the
     * NOT_INITIALIZED state at this point if moving to a previous screen in the flow makes its contents invalid.
     *
     * @return Always null.
     */
    public WorkFlowScreenPanel prevPage()
    {
        return null;
    }

    /**
     * This method should be called once the data in the work flow screen is complete and the screen is ready to move on
     * to the next screen. It changes the underlying state to enable/disable the next button.
     */
    public void isComplete()
    {
        // Enable or disable the next button
        getState().setNextAvailable(true);
    }

    /**
     * This method should be called if the data in the work flow screen is such that the entire flow may be ended
     * immediately. It changes the underlying state to enable the finish button.
     */
    public void isFinished()
    {
        // Make the finish button available
        getState().setFinished(true);
    }

    /**
     * This method should be called to set or reset the back button. It changes the underlying state to enable/disable
     * the back button.
     *
     * @param backFlag Set to true to enable the back button.
     */
    public void backAllowed(boolean backFlag)
    {
        // Enable or disable the back button
        getState().setPrevAvailable(backFlag);
    }

    /**
     * This method should be called to set or reset the next button. It changes the underlying state to enable/disable
     * the next button.
     *
     * @param nextFlag Set to true to enable the next button.
     */
    public void nextAllowed(boolean nextFlag)
    {
        // Enable or disable the next button
        getState().setNextAvailable(nextFlag);
    }

    /** This method should be called once the data in the panel has been changed. It sets the state to NOT_SAVED. */
    public void hasChanged()
    {
        // Change the state to NOT_SAVED
        state.setState(WorkFlowScreenState.NOT_SAVED);
    }
}
