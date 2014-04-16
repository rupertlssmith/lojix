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

import javax.swing.text.Document;

import com.thesett.aima.logic.fol.wam.debugger.swing.ColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.impl.SwingComponentFactory;
import com.thesett.text.impl.model.TextImpl;

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
    ColorScheme colorScheme = new DarkColorScheme();
    ComponentFactory componentFactory = new SwingComponentFactory(colorScheme);
    MainWindow mainWindow = componentFactory.createMainWindow();
    Document document = new TextImpl();

    public ControllerImpl open()
    {
        mainWindow.showMainWindow();
        mainWindow.showCentrePane(componentFactory.createBlankPanel());
        mainWindow.showLeftPane(componentFactory.createEditor(document));

        return this;
    }

    public void close()
    {
    }
}
