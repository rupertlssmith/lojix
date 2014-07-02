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
package com.thesett.aima.logic.fol.wam.debugger.monitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * BreakpointMonitor listens for updates to the "IP" register, which will occur every time a breakpoint (or step)
 * occurs.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Watch for changes to the IP register. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BreakpointMonitor implements PropertyChangeListener
{
    /** The IP register at the current break point. */
    private int ip;

    /** {@inheritDoc} */
    public void propertyChange(PropertyChangeEvent evt)
    {
        String propertyName = evt.getPropertyName();

        if ("IP".equals(propertyName))
        {
            String hex = String.format("%08X", evt.getNewValue());
            ip = (Integer) evt.getNewValue();
        }
    }

    /**
     * Provides the IP register at the current break point.
     *
     * @return The IP register at the current break point.
     */
    public int getIp()
    {
        return ip;
    }
}
