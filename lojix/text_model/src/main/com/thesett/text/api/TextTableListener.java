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
package com.thesett.text.api;

import java.util.EventListener;

/**
 * TextGridListener defines a set of callback functions for updates on the status of a text table model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Accept notification that a text table model has changed.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TextTableListener extends EventListener
{
    /**
     * Accepts notification that a text table model has changed.
     *
     * @param event An event describing the change to the text table model.
     */
    void changedUpdate(TextTableEvent event);
}
