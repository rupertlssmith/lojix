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

import javax.swing.text.AttributeSet;

import com.thesett.common.util.doublemaps.DoubleKeyedMap;
import com.thesett.common.util.doublemaps.HashMapXY;
import com.thesett.text.impl.model.TextTableImpl;

/**
 * EnhancedTextTableImpl provides an implementation of the {@link EnhancedTextTable} model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Insert text attributes into table cells. </td></tr>
 * <tr><td> Insert text attributes into table columns. </td></tr>
 * <tr><td> Insert text attributes into table rows. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnhancedTextTableImpl extends TextTableImpl implements EnhancedTextTable
{
    /** Holds a table with cell attribute data for enhanced printing. */
    DoubleKeyedMap<Long, Long, AttributeSet> attributeGrid = new HashMapXY<AttributeSet>(10);

    /** {@inheritDoc} */
    public void insertAttribute(AttributeSet attributes, int c, int r)
    {
    }

    /** {@inheritDoc} */
    public void insertColumnAttribute(AttributeSet attributes, int c)
    {
    }

    /** {@inheritDoc} */
    public void insertRowAttribute(AttributeSet attributes, int r)
    {
    }
}
