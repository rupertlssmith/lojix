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
package com.thesett.aima.logic.fol.wam.printer;

import com.thesett.common.util.doublemaps.DoubleKeyedMap;

/**
 * PrintingTable collects information about the row count, and row and column sizes, in order to print information in a
 * table format.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Collect row count, row and column size stats.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface PrintingTable extends DoubleKeyedMap<Integer, Integer, String>
{
    /**
     * Provides the number of rows in the table.
     *
     * @return The number of rows in the table.
     */
    int getRowCount();

    /**
     * Provides the number of columns in the table.
     *
     * @return The number of columns in the table.
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
}
