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
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * WorkFlowButtonsPanel is a panel of buttons consisting of back, next, finish and cancel buttons.
 *
 * <p/>An ActionListener can register itself with the button panel and it will be sent all of the back, next, finish and
 * cancel events that the button panel generates.
 *
 * <p/>WorkFlowButtonsPanel works in conjunction with work flows. A work flow controller can be registered with the
 * button panel in which case the controller will be sent all the action events that originate from the buttons. The
 * button panel will also listen for changes to the underlying state of the work flow and current work screen and enable
 * or disable its buttons accordingly.
 *
 * <p/>The rules for enabling and disabling buttons are as follows:
 *
 * <p/>If the current screen state changes its back or next available or finished state or if the current screen changes
 * then the back, next and finished buttons will be enabled according to the {@link WorkFlowScreenState#isNextAvailable},
 * {@link WorkFlowScreenState#isPrevAvailable} and {@link WorkFlowScreenState#isFinished} properties of the current
 * screen. This holds true unless the current screen is int the NOT_INITIALIZED state in which case back, next and
 * finish buttons will all be disabled.
 *
 * <p/>If the entire work flow moves in to the NOT_INITIALIZED state then all buttons will be disabled. If it moves into
 * the ready state the cancel button will be enabled but not the finish button. If it moves into the NOT_SAVED state
 * then both the cancel and finish buttons will be enabled.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Build user interface
 * <tr><td>Register listeners for button events
 * <tr><td>Enable or diable buttons according to the state of the work flow
 * <tr><td>Enable or diable buttons according to the state of curent work flow screen
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WorkFlowButtonsPanel extends JPanel implements PropertyChangeListener
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(WorkFlowButtonsPanel.class.getName()); */

    /** Holds the Back button. */
    private final JButton backButton = new JButton("<| Back");

    /** Holds the Next button. */
    private final JButton nextButton = new JButton("Next |>");

    /** Holds the Finish button. */
    private final JButton finishButton = new JButton("Finish");

    /** Holds the Cancel button. */
    private final JButton cancelButton = new JButton("Cancel");

    /** Create a new work flow buttons panel. */
    public WorkFlowButtonsPanel()
    {
        initComponents();
    }

    /** Builds the user interface. */
    public void initComponents()
    {
        // Place the buttons in the panel
        add(backButton);
        add(nextButton);
        add(finishButton);
        add(cancelButton);
    }

    /**
     * Called when a property in the work flow state is changed.
     *
     * @param event The property change event.
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        /*log.fine("void propertyChange(PropertyChangeEvent): called");*/

        /*log.fine("source class = " + event.getSource().getClass());*/
        /*log.fine("source object = " + event.getSource());*/
        /*log.fine("property name = " + event.getPropertyName());*/
        /*log.fine("new value = " + event.getNewValue());*/
        /*log.fine("old value = " + event.getOldValue());*/

        Object source = event.getSource();
        Object oldValue = event.getOldValue();
        String propertyName = event.getPropertyName();

        // Check if the event source is an individual screen state
        if (source instanceof WorkFlowScreenState)
        {
            WorkFlowScreenState wfsState = (WorkFlowScreenState) source;

            // Update the buttons to reflect the change in screen state
            updateButtonsForScreen(wfsState);
        }

        // Check if the event source is the whole work flow
        if (source instanceof WorkFlowState)
        {
            WorkFlowState wfState = (WorkFlowState) source;

            // Check if the event cause is a change in current screen
            if ("currentScreenState".equals(propertyName))
            {
                WorkFlowScreenState newScreenState = wfState.getCurrentScreenState();
                WorkFlowScreenState oldScreenState = (WorkFlowScreenState) oldValue;

                // De-register this as a listener for the old current screen state
                if (oldScreenState != null)
                {
                    oldScreenState.removePropertyChangeListener(this);
                }

                // Register this as a listener for the new current screen state
                if (newScreenState != null)
                {
                    newScreenState.addPropertyChangeListener(this);
                }

                // Update the buttons to reflect the current screen state
                updateButtonsForScreen(newScreenState);
            }

            // Check if the event cause is a change in the work flow state
            else if ("state".equals(propertyName))
            {
                // Update the buttons to reflect the change in state
                updateButtonsForWorkFlow(wfState);
            }
        }
    }

    /**
     * Updates the buttons enabled/disabled status to reflect the current screen state.
     *
     * @param state The new work flow screen state.
     */
    public void updateButtonsForScreen(WorkFlowScreenState state)
    {
        // Check if it is in the READY or NOT_SAVED state
        if (state.getState().equals(WorkFlowScreenState.READY) ||
                state.getState().equals(WorkFlowScreenState.NOT_SAVED))
        {
            backButton.setEnabled(state.isPrevAvailable());
            nextButton.setEnabled(state.isNextAvailable());
            finishButton.setEnabled(state.isFinished());
        }

        // Check if it is in the NOT_INITIALIZED state
        if (state.getState().equals(WorkFlowScreenState.NOT_INITIALIZED))
        {
            backButton.setEnabled(false);
            nextButton.setEnabled(false);
            finishButton.setEnabled(false);
        }
    }

    /**
     * Updates the buttons enabled/disabled status to reflect the current work flow state.
     *
     * @param state The new work flow state.
     */
    public void updateButtonsForWorkFlow(WorkFlowState state)
    {
        // Check if it is in the NOT_INITIALIZED state
        if (state.getState().equals(WorkFlowState.NOT_INITIALIZED))
        {
            backButton.setEnabled(false);
            nextButton.setEnabled(false);
            finishButton.setEnabled(false);
            cancelButton.setEnabled(false);
        }

        // Check if it is in the READY state
        if (state.getState().equals(WorkFlowState.READY))
        {
            finishButton.setEnabled(false);
            cancelButton.setEnabled(true);

            // Update buttons for the current screen state
            updateButtonsForScreen(state.getCurrentScreenState());
        }

        // Check if it is in the NOT_SAVED state
        if (state.getState().equals(WorkFlowState.NOT_SAVED))
        {
            finishButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }

    /**
     * Registers the work flow button panel with the specified work flow controller. This will cause the work flow
     * controller to receive button press events from the panel and register the button panel to receive state changes
     * from the underlying work flow model.
     *
     * @param controller The work flow controller to recieve work flow events from.
     */
    public void registerWorkFlowController(WorkFlowController controller)
    {
        // Set the work flow controller to listen for button events
        backButton.addActionListener(controller);
        nextButton.addActionListener(controller);
        finishButton.addActionListener(controller);
        cancelButton.addActionListener(controller);

        // Register this to listen for changes to the work flow state
        controller.getWorkFlowState().addPropertyChangeListener(this);

        // Register this to listen for changes to the state for the current screen if it is not null
        WorkFlowScreenState currentScreenState = controller.getWorkFlowState().getCurrentScreenState();

        if (currentScreenState != null)
        {
            currentScreenState.addPropertyChangeListener(this);
        }
    }
}
