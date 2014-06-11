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

import com.thesett.text.api.model.TextGridModel;

/**
 * EnhancedTextGrid is an extension of the {@link TextGridModel} to support more richly decorated text.
 *
 * <p/>This decorator support storing attributes in grid cells, or lines or rows. When fetching the attributes for a
 * cell, a cascading merge is used to arrive at the attributes of the cell.
 *
 * <p/>Horizontal and vertical separators can be inserted into the text grid, and these do not have to conform to the
 * same grid cell size as the text within the grid. To keep the model simple, these separators are always considered to
 * be infinitely wide or tall, otherwise the alignment of the grid would be lost.
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
public interface EnhancedTextGrid extends TextGridModel, AttributeGrid
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
     * Reads the attributes from the grid at the specified location.
     *
     * @param c The column position.
     * @param r The row position.
     * @return The attributes at the specified location.
     */
    AttributeSet getAttributeAt(int c, int r);

    /**
     * Inserts a horizontal separator into the grid, with the given height.
     *
     * @param r           The row to insert the separator before.
     * @param pixelHeight The height in pixels.
     */
    void insertHorizontalSeparator(int r, int pixelHeight);

    /**
     * Inserts a vertical separator into the grid, with the given width.
     *
     * @param c          The column to insert the separator before.
     * @param pixelWidth The width in pixels.
     */
    void insertVerticalSeparator(int c, int pixelWidth);
}
