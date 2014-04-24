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

import java.util.HashMap;
import java.util.Map;

import com.thesett.common.util.doublemaps.DoubleKeyedMap;
import com.thesett.common.util.doublemaps.HashMapXY;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrintingTableImpl implements PrintingTable
{
    /** Holds the maximum row sizes in the grid. */
    Map<Integer, Integer> maxRowSizes = new HashMap<Integer, Integer>();

    /** Holds the maximum column sizes in the grid. */
    Map<Integer, Integer> maxColumnSizes = new HashMap<Integer, Integer>();

    /** Used to count the maximum row with data in it. */
    private int maxRows;

    /** Used to count the maximum column with data in it. */
    private int maxColumns;

    /** Holds a table with cell data to pretty print. */
    DoubleKeyedMap<Long, Long, String> grid = new HashMapXY<String>(10);

    /**
     * Updates the maximum row count of the data table.
     *
     * @param row The maximum row count reached.
     */
    public void setMaxRowCount(int row)
    {
        if (maxRows < row)
        {
            maxRows = row;
        }
    }

    /**
     * Updates the maximum row height for a row of the data table.
     *
     * @param row    The row to update.
     * @param height The max height reached.
     */
    public void setMaxRowHeight(int row, int height)
    {
        Integer previousValue = maxRowSizes.get(row);

        if (previousValue == null)
        {
            maxRowSizes.put(row, height);
        }
        else if (previousValue < height)
        {
            maxRowSizes.put(row, height);
        }
    }

    /**
     * Updates the maximum column width for a column of the data table.
     *
     * @param column The column to update.
     * @param width  The max width reached.
     */
    public void setMaxColumnWidth(int column, int width)
    {
        Integer previousValue = maxColumnSizes.get(column);

        if (previousValue == null)
        {
            maxColumnSizes.put(column, width);
        }
        else if (previousValue < width)
        {
            maxColumnSizes.put(column, width);
        }
    }

    /** {@inheritDoc} */
    public void clear()
    {
        grid.clear();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Integer col, Integer row)
    {
        return grid.containsKey((long) col, (long) row);
    }

    /** {@inheritDoc} */
    public String put(Integer col, Integer row, String value)
    {
        maxColumns = (col > maxColumns) ? col : maxColumns;
        maxRows = (row > maxRows) ? row : maxRows;

        return grid.put((long) col, (long) row, value);
    }

    /** {@inheritDoc} */
    public String get(Integer col, Integer row)
    {
        return grid.get((long) col, (long) row);
    }

    /** {@inheritDoc} */
    public String remove(Integer col, Integer row)
    {
        return grid.remove((long) col, (long) row);
    }

    /** {@inheritDoc} */
    public int size()
    {
        return grid.size();
    }

    /** {@inheritDoc} */
    public boolean isEmpty()
    {
        return grid.isEmpty();
    }

    /** {@inheritDoc} */
    public int getHeight()
    {
        return maxRows + 1;
    }

    /** {@inheritDoc} */
    public int getWidth()
    {
        return maxColumns + 1;
    }

    /** {@inheritDoc} */
    public int getMaxColumnSize(int col)
    {
        return maxColumnSizes.get(col);
    }
}
