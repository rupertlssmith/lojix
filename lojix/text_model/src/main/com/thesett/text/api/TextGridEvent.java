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
package com.thesett.text.api;

import com.thesett.text.api.model.TextGridModel;

/**
 * TextGridEvent is an event that describes a change to a text grid model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide the changed model.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextGridEvent
{
    /** The changed model. */
    private final TextGridModel model;

    /**
     * Creates a text grid update event.
     *
     * @param model The changed model.
     */
    public TextGridEvent(TextGridModel model)
    {
        this.model = model;
    }

    /**
     * Provides the changed model.
     *
     * @return The changed model.
     */
    TextGridModel getModel()
    {
        return model;
    }
}
