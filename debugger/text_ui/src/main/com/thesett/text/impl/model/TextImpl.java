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
package com.thesett.text.impl.model;

import javax.swing.text.PlainDocument;

import com.thesett.common.util.Source;
import com.thesett.text.api.model.Row;
import com.thesett.text.api.model.Text;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextImpl extends PlainDocument implements Text
{
    public void caretAt(int row, int column)
    {
    }

    public void insert(char character)
    {
    }

    public void backspace()
    {
    }

    public void newline()
    {
    }

    public Source<Row> updates()
    {
        return null;
    }

    public Source<Row> full()
    {
        return null;
    }
}
