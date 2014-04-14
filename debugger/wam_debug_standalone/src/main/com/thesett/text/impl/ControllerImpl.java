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
package com.thesett.text.impl;

import javax.swing.text.Document;

import com.thesett.text.api.Controller;
import com.thesett.text.api.model.Text;
import com.thesett.text.impl.model.TextImpl;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ControllerImpl implements Controller
{
    UIFactory uiFactory = new UIFactory();
    Document document = new TextImpl();

    public Controller open()
    {
        uiFactory.createMainWindow();
        uiFactory.addTextPane(document);
        uiFactory.showConsole(document);
        uiFactory.showStatusBar(document);

        return this;
    }

    public Controller close()
    {
        return this;
    }

    public Controller update(Text model)
    {
        return this;
    }
}
