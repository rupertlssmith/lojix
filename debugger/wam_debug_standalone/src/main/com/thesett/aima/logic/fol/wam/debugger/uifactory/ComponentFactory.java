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
package com.thesett.aima.logic.fol.wam.debugger.uifactory;

import javax.swing.text.Document;

import com.thesett.aima.logic.fol.wam.debugger.swing.MotionDelta;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @param  <C> The type of ui components this factory produces.
 *
 * @author Rupert Smith
 */
public interface ComponentFactory<C>
{
    MainWindow<C> createMainWindow();

    C createEditor(Document document);

    C createGripPanel(MotionDelta motionDelta);

    C createBlankPanel();
}
