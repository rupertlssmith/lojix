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
public class TextTableImpl implements TextTableModel
{
    /**
     * Creates a table addressable text grid.
     *
     * @param  c The column start position of the table.
     * @param  r The row start position of the table.
     * @param  w The maximum width of the table.
     * @param  h The maximum height of the table.
     *
     * @return A table within this grid.
     */
    public TextTableImpl(int c, int r, int w, int h, TextGridModel parent)
    {
    }

    /** {@inheritDoc} */
    public void clear()
    {
    }

    /** {@inheritDoc} */
    public boolean containsKey(Integer primaryKey, Integer secondaryKey)
    {
        return false;
    }

    /** {@inheritDoc} */
    public String put(Integer primaryKey, Integer secondaryKey, String value)
    {
        return null;
    }

    /** {@inheritDoc} */
    public String get(Integer primaryKey, Integer secondaryKey)
    {
        return null;
    }

    /** {@inheritDoc} */
    public String remove(Integer primaryKey, Integer secondaryKey)
    {
        return null;
    }

    /** {@inheritDoc} */
    public int size()
    {
        return 0;
    }

    /** {@inheritDoc} */
    public boolean isEmpty()
    {
        return false;
    }
}
