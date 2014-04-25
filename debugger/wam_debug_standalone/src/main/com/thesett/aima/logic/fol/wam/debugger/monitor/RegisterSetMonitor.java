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

import com.thesett.text.api.model.TextTableModel;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class RegisterSetMonitor implements PropertyChangeListener
{
    private final TextTableModel table;

    public RegisterSetMonitor(TextTableModel table)
    {
        this.table = table;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        System.out.println(evt);
    }

    public void initialize()
    {
        String[] registerNames = new String[] { "ip", "hp", "ep", "bp" };

        for (int i = 0; i < registerNames.length; i++)
        {
            String registerName = registerNames[i];
            table.put(0, i, registerName);
        }
    }
}
