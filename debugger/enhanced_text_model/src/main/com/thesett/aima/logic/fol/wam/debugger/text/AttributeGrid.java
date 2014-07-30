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
package com.thesett.aima.logic.fol.wam.debugger.text;

/**
 * AttributeGrid defines an modifiable grid of attribute sets.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Insert text attributes into grid cells. </td></tr>
 * <tr><td> Insert text attributes into grid columns. </td></tr>
 * <tr><td> Insert text attributes into grid rows. </td></tr>
 * <tr><td> Provide cascaded text attributes for grid cells. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface AttributeGrid
{
    /**
     * Inserts an attribute set into the grid at the specified location.
     *
     * @param attributes The attribute set to insert.
     * @param c          The column position.
     * @param r          The row position.
     */
    void insertAttribute(AttributeSet attributes, int c, int r);

    /**
     * Inserts an attribute set into the grid at the specified column.
     *
     * @param attributes The attribute set to insert.
     * @param c          The column position.
     */
    void insertColumnAttribute(AttributeSet attributes, int c);

    /**
     * Inserts an attribute set into the grid at the specified row.
     *
     * @param attributes The attribute set to insert.
     * @param r          The row position.
     */
    void insertRowAttribute(AttributeSet attributes, int r);

    /**
     * Reads the attributes from the grid at the specified location. This will provide the attributes set on a specific
     * cell, combined with attributes set on the cells column, then row, if the cell has attributes specified in more
     * than one way.
     *
     * @param  c The column position.
     * @param  r The row position.
     *
     * @return The attributes at the specified location.
     */
    AttributeSet getAttributeAt(int c, int r);

    /**
     * Reads attributes set on a specified column.
     *
     * @param  c The column position.
     *
     * @return The attributes at the specified location.
     */
    AttributeSet getColumnAttribute(int c);

    /**
     * Reads attributes set on a specified row.
     *
     * @param  r The row position.
     *
     * @return The attributes at the specified location.
     */
    AttributeSet getRowAttribute(int r);
}
