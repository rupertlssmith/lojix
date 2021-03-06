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
package com.thesett.text.impl.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.thesett.common.util.doublemaps.HashMapXY;
import com.thesett.text.api.TextGridEvent;
import com.thesett.text.api.TextGridListener;
import com.thesett.text.api.TextTableEvent;
import com.thesett.text.api.TextTableListener;
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
public class TextGridImpl implements TextGridModel
{
    /** Maximum occupied column of this child grid. */
    protected int maxColumn;

    /** Maximum occupied row of this child grid. */
    protected int maxRow;

    /** Holds the grid data. */
    HashMapXY<Character> data = new HashMapXY<Character>(100);

    /** Holds a set of listeners for updates to this model. */
    Collection<TextGridListener> listeners = new HashSet<TextGridListener>();

    /** {@inheritDoc} */
    public int getWidth()
    {
        return maxColumn + 1;
    }

    /** {@inheritDoc} */
    public int getHeight()
    {
        return maxRow + 1;
    }

    /** {@inheritDoc} */
    public void insert(char character, int c, int r)
    {
        internalInsert(character, c, r);
        updateListeners();
    }

    /** {@inheritDoc} */
    public void insert(String string, int c, int r)
    {
        for (char character : string.toCharArray())
        {
            internalInsert(character, c++, r);
        }

        updateListeners();
    }

    /** {@inheritDoc} */
    public char getCharAt(int c, int r)
    {
        Character character = data.get((long) c, (long) r);

        if (character == null)
        {
            return ' ';
        }
        else
        {
            return character;
        }
    }

    /** {@inheritDoc} */
    public TextGridModel createInnerGrid(int c, int r, int w, int h)
    {
        return new NestedTextGridImpl(c, r, w, h, this);
    }

    /** {@inheritDoc} */
    public TextTableModel createTable(int c, int r, int w, int h)
    {
        // Supply a text table, with this grid set up to listen for updates to the table, and to be re-rendered as the
        // table changes.
        TextTableModel textTable = new TextTableImpl();

        textTable.addTextTableListener(new TableListener());

        return textTable;
    }

    /** {@inheritDoc} */
    public void addTextGridListener(TextGridListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    public void removeTextGridListener(TextGridListener listener)
    {
        listeners.remove(listener);
    }

    /** Notifies all interested listeners of an update to this model. */
    protected void updateListeners()
    {
        TextGridEvent event = new TextGridEvent(this);

        for (TextGridListener listener : listeners)
        {
            listener.changedUpdate(event);
        }
    }

    /**
     * Inserts a single character into the grid at the specified location. This is a private insert method, that does
     * not notify model listeners, so that the public insert methods can do that as a separate step.
     *
     * @param character The character to insert.
     * @param c         The column position.
     * @param r         The row position.
     */
    private void internalInsert(char character, int c, int r)
    {
        maxColumn = (c > maxColumn) ? c : maxColumn;
        maxRow = (r > maxRow) ? r : maxRow;

        data.put((long) c, (long) r, character);
    }

    /**
     * Re-renders a table into this grid, when the table changes.
     */
    private class TableListener implements TextTableListener
    {
        /** {@inheritDoc} */
        public void changedUpdate(TextTableEvent event)
        {
            TextTableGridRenderer renderer = new TextTableGridRenderer(event.getModel(), TextGridImpl.this);
            renderer.renderTable();
        }
    }
}
