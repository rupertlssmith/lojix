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
package com.thesett.common.state;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * BaseState defines a mechanism for beans to register property change listeners and to notify the listeners by sending
 * them a property change event on the Swing event queue.
 *
 * <p/>This class can be extended to build any class that needs to notify property change listeners of changes to its
 * state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Register poperty change listeners
 * <tr><td>Notify property change listeners of any changes to state
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Consider how this class differs from java.util.Observable. Does it really offer anything that that does not
 *         already? Perhaps make this an interface rather than a class and then have a change notifier class that the
 *         observable interface gets attached to to actually implement the propery change notifications. This class
 *         would implement the interface as an abstract base class for extending to produce observable beans. Some of
 *         its code would also be used by the notifier class to which plain implementations of the interface can be
 *         attached to take their observable implementation from without the restriction of having to extend this.
 *         Perhaps Observer is a better interface to partner this with than PropertyChangeListener as
 *         PropertyChangeListener comes from the swing package implying some sort of swing dependency of this class
 *         which is not really swing dependant at all.
 * @todo   Add in per property stuff.
 */
public abstract class BaseState
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(BaseState.class.getName()); */

    /** List of property change listeners. */
    private Collection listeners = new ArrayList();

    /**
     * Adds a property changed listener to be notified of changes to the application state.
     *
     * @param l The listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        // Check if the listneres list has been initialized
        if (listeners == null)
        {
            // Listeneres list not intialized so create a new list
            listeners = new ArrayList();
        }

        synchronized (listeners)
        {
            // Add the new listener to the list
            listeners.add(l);
        }
    }

    /**
     * Adds a property changed listener to be notified of changes to the named property.
     *
     * @param l The listener/
     * @param p The property (ignored, notifies of changes to any properties).
     */
    public void addPropertyChangeListener(String p, PropertyChangeListener l)
    {
        // Check if the listeneres list has been initialized
        if (listeners == null)
        {
            // Listeneres list not initialized so create a new list
            listeners = new ArrayList();
        }

        synchronized (listeners)
        {
            // Add the new listener to the list
            listeners.add(l);
        }
    }

    /**
     * Removes the specified property change listener from the list of active listeners.
     *
     * @param l The property change listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        if (listeners == null)
        {
            return;
        }

        synchronized (listeners)
        {
            listeners.remove(l);
        }
    }

    /**
     * Removes the specified property change listener from the list of active listeners.
     *
     * @param p Ignored. In future will be used to name the specific property to remove the listener for.
     * @param l The property change listener to remove.
     */
    public void removePropertyChangeListener(String p, PropertyChangeListener l)
    {
        if (listeners == null)
        {
            return;
        }

        synchronized (listeners)
        {
            listeners.remove(l);
        }
    }

    /**
     * Determines if the application state has any property change listeners.
     *
     * @param  prop Ignored but will be used in future to check for listeners for a particular property.
     *
     * @return True if there are listeners, false otherwise.
     */
    public boolean hasListeners(String prop)
    {
        // If the listeners list is not inialized then there are no listeners
        if (listeners == null)
        {
            return false;
        }

        synchronized (listeners)
        {
            // If the listeners list is empty then there are no listeners
            return listeners.isEmpty();
        }
    }

    /** Triggers notification of all property change listeners.. */
    protected void firePropertyChange()
    {
        firePropertyChange(new PropertyChangeEvent(this, null, null, null));
    }

    /**
     * Notifies all property change listeners of the given PropertyChangeEvent.
     *
     * @param evt The property change event to pass to all listeners.
     */
    protected void firePropertyChange(PropertyChangeEvent evt)
    {
        /*log.fine("firePropertyChange: called");*/

        // Take a copy of the event as a final variable so that it can be used in an inner class
        final PropertyChangeEvent finalEvent = evt;

        Iterator it;

        // Check if the list of listeners is empty
        if (listeners == null)
        {
            // There are no listeners so simply return without doing anything
            return;
        }

        // synchronize on the list of listeners to prevent comodification
        synchronized (listeners)
        {
            // Cycle through all listeners and notify them
            it = listeners.iterator();

            while (it.hasNext())
            {
                // Get the next listener from the list
                final PropertyChangeListener l = (PropertyChangeListener) it.next();

                // Notify the listener of the property change event
                Runnable r =
                    new Runnable()
                    {
                        public void run()
                        {
                            // Fire a property change event
                            l.propertyChange(finalEvent);
                        }
                    };

                // Run the property change event in the Swing event queue
                SwingUtilities.invokeLater(r);
            }
        }
    }
}
