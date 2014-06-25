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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thesett.common.util.Pair;
import com.thesett.common.util.doublemaps.DoubleKeyedMap;
import com.thesett.common.util.doublemaps.HashMapXY;
import com.thesett.text.api.TextTableEvent;
import com.thesett.text.api.TextTableListener;
import com.thesett.text.api.model.TextTableModel;

/**
 * TextTableImpl provides an implementation of the {@link TextTableModel}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Add or remove strings to table cells. </td></tr>
 * <tr><td> Provide the table size. </td></tr>
 * <tr><td> Monitor maximum text width of columns. </td></tr>
 * <tr><td> Provide ability to register listeners for changes to the table. </td></tr>
 * <tr><td> Allow table columns and rows to be labelled. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextTableImpl implements TextTableModel
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

    /** Holds any interested listeners for updates to the table model. */
    protected Set<TextTableListener> listeners = new HashSet<TextTableListener>();

    /** Holds any column labels. */
    private Map<String, Integer> columnLabels = new HashMap<String, Integer>();

    /** Holds any row labels. */
    private Map<String, Integer> rowLabels = new HashMap<String, Integer>();

    /** Holds any individual cell labels. */
    private Map<String, Pair<Integer, Integer>> cellLabels = new HashMap<String, Pair<Integer, Integer>>();

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

        updateMaxColumnWidth(col, value.length());

        String result = grid.put((long) col, (long) row, value);

        updateListeners(col, row);

        return result;
    }

    /** {@inheritDoc} */
    public String get(Integer col, Integer row)
    {
        return grid.get((long) col, (long) row);
    }

    /** {@inheritDoc} */
    public String remove(Integer col, Integer row)
    {
        String result = grid.remove((long) col, (long) row);

        updateListeners(col, row);

        return result;
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
    public int getRowCount()
    {
        return maxRows + 1;
    }

    /** {@inheritDoc} */
    public int getColumnCount()
    {
        return maxColumns + 1;
    }

    /** {@inheritDoc} */
    public int getMaxColumnSize(int col)
    {
        Integer result = maxColumnSizes.get(col);

        return (result == null) ? 0 : result;
    }

    /** {@inheritDoc} */
    public void addTextTableListener(TextTableListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    public void removeTextTableListener(TextTableListener listener)
    {
        listeners.remove(listener);
    }

    /** {@inheritDoc} */
    public void labelColumn(String label, int column)
    {
        columnLabels.put(label, column);
    }

    /** {@inheritDoc} */
    public void labelRow(String label, int row)
    {
        rowLabels.put(label, row);
    }

    /** {@inheritDoc} */
    public void labelCell(String label, int col, int row)
    {
        cellLabels.put(label, new Pair<Integer, Integer>(col, row));
    }

    /** {@inheritDoc} */
    public DoubleKeyedMap<String, Integer, String> withColumnLabels()
    {
        return new ColumnLabelView();
    }

    /** {@inheritDoc} */
    public DoubleKeyedMap<Integer, String, String> withRowLabels()
    {
        return new RowLabelView();
    }

    /** {@inheritDoc} */
    public DoubleKeyedMap<String, String, String> withLabels()
    {
        return new RowAndColumnLabelView();
    }

    /** {@inheritDoc} */
    public Map<String, String> withCellLabels()
    {
        return new CellLabelView();
    }

    /** Notifies all interested listeners of an update to this model. */
    protected void updateListeners(int col, int row)
    {
        TextTableEvent event = new TextTableEvent(this, row, col);

        for (TextTableListener listener : listeners)
        {
            listener.changedUpdate(event);
        }
    }

    /**
     * Updates the maximum column width for a column of the data table.
     *
     * @param column The column to update.
     * @param width  The max width reached.
     */
    private void updateMaxColumnWidth(int column, int width)
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

    /**
     * Provides a base implementation for the label views.
     */
    public abstract class ViewBase
    {
        /** {@inheritDoc} */
        public void clear()
        {
            TextTableImpl.this.clear();
        }

        /** {@inheritDoc} */
        public int size()
        {
            return TextTableImpl.this.size();
        }

        /** {@inheritDoc} */
        public boolean isEmpty()
        {
            return TextTableImpl.this.isEmpty();
        }
    }

    /**
     * Provides a view onto the table with labeled columns.
     */
    private class ColumnLabelView extends ViewBase implements DoubleKeyedMap<String, Integer, String>
    {
        /** {@inheritDoc} */
        public boolean containsKey(String label, Integer row)
        {
            Integer col = columnLabels.get(label);

            if (col == null)
            {
                return false;
            }
            else
            {
                return TextTableImpl.this.containsKey(col, row);
            }
        }

        /** {@inheritDoc} */
        public String put(String label, Integer row, String value)
        {
            Integer col = columnLabels.get(label);

            if (col != null)
            {
                return TextTableImpl.this.put(col, row, value);
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String get(String label, Integer row)
        {
            Integer col = columnLabels.get(label);

            if (col != null)
            {
                return TextTableImpl.this.get(col, row);
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String remove(String label, Integer row)
        {
            Integer col = columnLabels.get(label);

            if (col != null)
            {
                return TextTableImpl.this.remove(col, row);
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Provides a view onto the table with labeled rows.
     */
    private class RowLabelView extends ViewBase implements DoubleKeyedMap<Integer, String, String>
    {
        /** {@inheritDoc} */
        public boolean containsKey(Integer col, String label)
        {
            Integer row = rowLabels.get(label);

            if (row == null)
            {
                return false;
            }
            else
            {
                return TextTableImpl.this.containsKey(col, row);
            }
        }

        /** {@inheritDoc} */
        public String put(Integer col, String label, String value)
        {
            Integer row = rowLabels.get(label);

            if (row != null)
            {
                return TextTableImpl.this.put(col, row, value);
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String get(Integer col, String label)
        {
            Integer row = rowLabels.get(label);

            if (row != null)
            {
                return TextTableImpl.this.get(col, row);
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String remove(Integer col, String label)
        {
            Integer row = rowLabels.get(label);

            if (row != null)
            {
                return TextTableImpl.this.remove(col, row);
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Provides a view onto the table with labeled rows and columns.
     */
    private class RowAndColumnLabelView extends ViewBase implements DoubleKeyedMap<String, String, String>
    {
        /** {@inheritDoc} */
        public boolean containsKey(String colLabel, String rowLabel)
        {
            Integer row = rowLabels.get(rowLabel);
            Integer col = columnLabels.get(colLabel);

            if ((row != null) && (col != null))
            {
                return TextTableImpl.this.containsKey(col, row);
            }
            else
            {
                return false;
            }
        }

        /** {@inheritDoc} */
        public String put(String colLabel, String rowLabel, String value)
        {
            Integer row = rowLabels.get(rowLabel);
            Integer col = columnLabels.get(colLabel);

            if ((row != null) && (col != null))
            {
                return TextTableImpl.this.put(col, row, value);
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String get(String colLabel, String rowLabel)
        {
            Integer row = rowLabels.get(rowLabel);
            Integer col = columnLabels.get(colLabel);

            if ((row != null) && (col != null))
            {
                return TextTableImpl.this.get(col, row);
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String remove(String colLabel, String rowLabel)
        {
            Integer row = rowLabels.get(rowLabel);
            Integer col = columnLabels.get(colLabel);

            if ((row != null) && (col != null))
            {
                return TextTableImpl.this.remove(col, row);
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Provides a view onto the table with individually labelled cells.
     */
    private class CellLabelView extends ViewBase implements Map<String, String>
    {
        /** {@inheritDoc} */
        public boolean containsKey(Object key)
        {
            return cellLabels.containsKey(key);
        }

        /** {@inheritDoc} */
        public boolean containsValue(Object value)
        {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public String get(Object key)
        {
            Pair<Integer, Integer> cell = cellLabels.get(key);

            if (cell != null)
            {
                return TextTableImpl.this.get(cell.getFirst(), cell.getSecond());
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String put(String key, String value)
        {
            Pair<Integer, Integer> cell = cellLabels.get(key);

            if (cell != null)
            {
                return TextTableImpl.this.put(cell.getFirst(), cell.getSecond(), value);
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public String remove(Object key)
        {
            Pair<Integer, Integer> cell = cellLabels.get(key);

            if (cell != null)
            {
                return TextTableImpl.this.remove(cell.getFirst(), cell.getSecond());
            }
            else
            {
                return null;
            }
        }

        /** {@inheritDoc} */
        public void putAll(Map<? extends String, ? extends String> m)
        {
            for (Map.Entry<? extends String, ? extends String> entry : m.entrySet())
            {
                put(entry.getKey(), entry.getValue());
            }
        }

        /** {@inheritDoc} */
        public Set<String> keySet()
        {
            return cellLabels.keySet();
        }

        /** {@inheritDoc} */
        public Collection<String> values()
        {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public Set<Entry<String, String>> entrySet()
        {
            throw new UnsupportedOperationException();
        }
    }
}
