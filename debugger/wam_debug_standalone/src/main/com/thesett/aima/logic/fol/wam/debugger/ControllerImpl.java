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
package com.thesett.aima.logic.fol.wam.debugger;

import com.thesett.aima.logic.fol.wam.debugger.swing.ColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.impl.SwingComponentFactory;
import com.thesett.text.api.model.TextGridModel;
import com.thesett.text.api.model.TextTableModel;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ControllerImpl
{
    private ColorScheme colorScheme = new DarkColorScheme();
    private ComponentFactory componentFactory = new SwingComponentFactory(colorScheme);
    private MainWindow mainWindow = componentFactory.createMainWindow();

    private final RegisterModel registerModel;

    public ControllerImpl(RegisterModel registerModel)
    {
        this.registerModel = registerModel;
    }

    public ControllerImpl open()
    {
        mainWindow.showMainWindow();
        mainWindow.showCentrePane(componentFactory.createBlankPanel());

        TextGridModel textGrid = componentFactory.createTextGrid();
        mainWindow.showLeftPane(componentFactory.createTextGridPanel(textGrid));

        TextTableModel table = textGrid.createTable(0, 0, 16, 10);

        String[] registerNames = new String[] { "ip", "hp", "ep", "bp" };

        for (int i = 0; i < registerNames.length; i++)
        {
            String registerName = registerNames[i];
            table.put(0, i, registerName);
        }

        return this;
    }

    public void close()
    {
    }
}
