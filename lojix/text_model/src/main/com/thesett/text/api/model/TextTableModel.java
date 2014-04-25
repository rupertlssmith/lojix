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
package com.thesett.text.api.model;

import com.thesett.common.util.doublemaps.DoubleKeyedMap;
import com.thesett.text.api.TextTableListener;

/**
 * TextTableModel is a {@link TextGridModel} that additionally divides its grid area into addressable rows and columns.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TextTableModel extends DoubleKeyedMap<Integer, Integer, String>
{
    /**
     * Provides the count of the number of rows in the table.
     *
     * @return The count of the number of rows in the table.
     */
    int getRowCount();

    /**
     * Provides the count of the number of columns in the table.
     *
     * @return The count of the number of columns in the table.
     */
    int getColumnCount();

    /**
     * Provides the maximum width of the specified column.
     *
     * @param  col The column offset to get.
     *
     * @return The maximum column width.
     */
    int getMaxColumnSize(int col);

    /**
     * Adds a listener for updates to this model.
     *
     * @param listener The listener for updates to this model.
     */
    void addTextGridListener(TextTableListener listener);

    /**
     * Removes a listener for updates to this model.
     *
     * @param listener The listener to remove.
     */
    void removeTextGridListener(TextTableListener listener);
}
