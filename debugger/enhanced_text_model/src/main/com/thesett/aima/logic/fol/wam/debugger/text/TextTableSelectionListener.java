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
package com.thesett.aima.logic.fol.wam.debugger.text;

import java.util.EventListener;

import com.thesett.text.api.TextTableEvent;

/**
 * TextTableSelectionListener defines a callback to respond to selection events within a text table.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Accept notification that a row or column in a table has been selected. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TextTableSelectionListener extends EventListener
{
    /**
     * Accepts notification that part of a text table model has been selected.
     *
     * @param event An event describing the part of text table model selected.
     */
    void select(TextTableEvent event);
}
