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
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.thesett.common.util.FifoStack;
import com.thesett.common.util.LifoStack;
import com.thesett.common.util.Stack;

/**
 * WorkFlowController is a pure control component in the work flow mvc framework. Its main responsibility is to listen
 * for user inputs, typically generated by {@link WorkFlowButtonsPanel} and respond to these inputs accordingly. It also
 * provides some auxiliary methods than work flow implementation can use to manipulate the work flow, such as setting
 * the current screen.
 *
 * <p/>WorkFlowController provides an ActionListener which responds to four action commands, "back", "next", "finish"
 * and "cancel".
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Listen for back, next, finish and cancel actions
 * <tr><td>Update the current screen
 * <tr><td>Translate actions into calls to implementation of this controller and the current screen <td> {@link WorkFlowScreenPanel}
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   WorkFlowController uses a JPanel to store the current screen in. Should this be changed to a
 *         java.awt.Container to make it more generic?
 * @todo   Need to consider what default behaviour this abtract controller can provide. Far example should it keep track
 *         of all pages in the work flow which are in the NOT_SAVED state and then call each of their saveWork or
 *         dicardWork methods when the finish/cancel buttons are pressed. If so in what order should they be called? Can
 *         a sensible ordering be given or should the ordering remain unspecified. An ordering as well as all the pages
 *         in the flow could be kept track of within the work flow state. Also when the next or back buttons are pressed
 *         the implementation of the controller can be notified and also the current page can be notified. Is it
 *         necessary to notify both or just one of them? In what order should they be notified?
 */
public abstract class WorkFlowController implements ActionListener
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(WorkFlowController.class.getName()); */

    /** Constant defining forward commit ordering of the screens. */
    public static final String FORWARD_ORDERING = "Forward";

    /** Constant defining reverse commit ordering of the screens. */
    public static final String REVERSE_ORDERING = "Reverse";

    /** Holds the state of the work flow. */
    private final WorkFlowState state;

    /**
     * Holds the screens that have been accessed in the work flow in order of access. They are held in a stack so that
     * they may easily be accessed in the order that they were encountered in the work flow. By default a fifo stack is
     * used so that the saveWork or discardWork methods of the work screens will be called in the order that the user
     * encountered these screens. This may be changed by setting the commit order via the {@link #setCommitOrder}
     * method.
     */
    private Stack accessedScreens;

    /** Holds reference to the JPanel that the work flow controller displays the work flow pages in. */
    private final JPanel panel;

    /** Holds the current screen within the work flow. */
    private WorkFlowScreenPanel currentScreen;

    /**
     * Creates a new work flow controller. The specified panel is used to place the current screen in.
     *
     * @param panel The panel that the work flow is rendered in.
     */
    public WorkFlowController(JPanel panel)
    {
        state = new WorkFlowState();

        // Keep reference to the JPanel to place pages in
        this.panel = panel;

        // Create a fifo stack to hold the accessed screens in
        accessedScreens = new FifoStack();
    }

    /**
     * This method will be called when the work flow is to be displayed. If the state is NOT_INTIALIZED it should load
     * its data, and build its user interface components and set the state to READY. If the state is READY or NOT_SAVED
     * it should not do anything.
     */
    public abstract void initialize();

    /**
     * This method should be implemented to save the state of the work flow. It is called when a finish event is
     * triggered at the end of the work flow.
     */
    public abstract void saveWork();

    /**
     * This method should be implemented to discard the state of the work flow. It is called when a cancel event is
     * triggered.
     */
    public abstract void discardWork();

    /** This method should be implemented to return to the previous page in the work flow. */
    public abstract void prevPage();

    /**
     * Returns the underlying state of the work flow.
     *
     * @return The current work flow state.
     */
    public WorkFlowState getWorkFlowState()
    {
        return state;
    }

    /**
     * Listens for events from back, next, finish and cancel inputs. If the event is finish the {@link #saveWork} method
     * is triggered. If the event is cancel the {@link #discardWork} method is called. If the event is back or next then
     * the {@link #prevPage} or {@link #nextPage} methods are called.
     *
     * @param event The work flow action event.
     */
    public void actionPerformed(ActionEvent event)
    {
        /*log.fine("void actionPerformed(ActionEvent): called");*/
        /*log.fine("Action is " + event.getActionCommand());*/

        // Check which action event was performed
        String action = event.getActionCommand();

        // Check if the finish button was pressed
        if ("Finish".equals(action))
        {
            // Save the work in progress
            saveWorkFlow();
        }

        // Check if the cancel button was pressed
        if ("Cancel".equals(action))
        {
            // Discard the work in progress
            discardWorkFlow();
        }

        // Check if the next page button was pressed
        if ("Next |>".equals(action))
        {
            // Notify implementing class of transition to the next page
            nextPage();
        }

        // Check if the prev page button was pressed
        if ("<| Prev".equals(action))
        {
            // Notify implementing class of transition to the previous page
            prevPage();
        }
    }

    /**
     * Sets the order in which the saveWork or dicardWork methods of the individual screens encountered in a work flow
     * are called. On of the constants {@link #FORWARD_ORDERING} or {@link #REVERSE_ORDERING} should be passed as the
     * value to this method to specify which ordering to use.
     *
     * @param order The commit ordering to use.
     */
    public void setCommitOrder(String order)
    {
        // Check that the specified order matches one of the ordering constants
        if (!order.equals(FORWARD_ORDERING) && !order.equals(REVERSE_ORDERING))
        {
            return;
        }

        // Check that the new ordering is different from the existing one so that some work needs to be done to change
        // it

        if (order.equals(FORWARD_ORDERING) && (accessedScreens instanceof LifoStack))
        {
            // Copy the screens into a forward ordered stack
            accessedScreens = new FifoStack(accessedScreens);
        }
        else if (order.equals(REVERSE_ORDERING) && (accessedScreens instanceof LifoStack))
        {
            // Copy the screens into a reverse ordered stack
            accessedScreens = new LifoStack(accessedScreens);
        }
    }

    /** This method should be implemented to go to the next page in the work flow. */
    protected void nextPage()
    {
        /*log.fine("void nextPage(): called");*/

        // Call the next page method of the current page to get the next page
        WorkFlowScreenPanel nextPage = currentScreen.nextPage();

        // Set the next page as the current one
        setCurrentScreen(nextPage);
    }

    /**
     * Method called when the finish button is pressed. It works through all the screens in the order in which they were
     * accessed (or reverse order, depending on the order set by the {@link #setCommitOrder} method). Any screens that
     * are in the NOT_SAVED state will have their {@link WorkFlowScreenPanel#saveWork} method called. After this step is
     * complete the {@link #saveWork} for the work flow controller is called.
     */
    protected void saveWorkFlow()
    {
        /*log.fine("void saveWorkFlow(): called");*/

        // Cycle through all the accessed screens in the work flow
        while (!accessedScreens.isEmpty())
        {
            WorkFlowScreenPanel nextScreen = (WorkFlowScreenPanel) accessedScreens.pop();

            // Check if the screen has unsaved state and call its save work method if so
            if (nextScreen.getState().getState().equals(WorkFlowScreenState.NOT_SAVED))
            {
                nextScreen.saveWork();
            }
        }

        // Call the save work method for the entire work flow controller to finalize the work flow
        saveWork();
    }

    /**
     * Method called when the cancel button is pressed. It works through all the screens in the order in which they were
     * accessed (or reverse order, depending on the order set by the {@link #setCommitOrder} method). Any screens that
     * are in the NOT_SAVED state will have their {@link WorkFlowScreenPanel#discardWork} method called. After this step
     * is complete the {@link #discardWork} for the work flow controller is called.
     */
    protected void discardWorkFlow()
    {
        /*log.fine("void discardWorkFlow(): called");*/

        // Cycle through all the accessed screens in the work flow
        while (!accessedScreens.isEmpty())
        {
            WorkFlowScreenPanel nextScreen = (WorkFlowScreenPanel) accessedScreens.pop();

            // Check if the screen has unsaved state and call its discard work method if so
            if (nextScreen.getState().getState().equals(WorkFlowScreenState.NOT_SAVED))
            {
                nextScreen.discardWork();
            }
        }

        // Call the discard work method for the entire work flow controller to finalize the work flow
        discardWork();
    }

    /**
     * This is a helper method that controller implementations may find useful for moving to a new screen. It places the
     * screen into the panel that this controller was built with, replacing any existing screen, changes the underlying
     * state to reflect the change to a new current screen and calls the new screens initialize method.
     *
     * @param screen The new work flow screen to be displayed.
     */
    protected void setCurrentScreen(WorkFlowScreenPanel screen)
    {
        // Remove any existing screen from the panel
        panel.removeAll();

        // Place the new screen into the panel
        panel.add(screen);

        // Check if the screen is not already in the stack of accessed screens. It may be if this is the second time
        // the screen is visited foir example if the back button is used.
        if (!accessedScreens.contains(screen))
        {
            // Add the screen to the stack of accessed screens
            accessedScreens.push(screen);
        }

        // Update the work flow state to reflect the change to a new screen state
        state.setCurrentScreenState(screen.getState());

        // Keep track of the current screen in a local member variable
        currentScreen = screen;

        // Initialize the new screen
        screen.initialize();

        // Force the panel to redraw
        panel.validate();
    }
}
