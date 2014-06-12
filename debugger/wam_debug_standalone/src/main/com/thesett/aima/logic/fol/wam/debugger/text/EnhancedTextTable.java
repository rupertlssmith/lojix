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

import com.thesett.text.api.model.TextTableModel;

import java.util.SortedMap;

/**
 * EnhancedTextTable is an extension of the {@link TextTableModel} to support more richly decorated text. This decorator
 * supports storing attributes against table cells, or lines or rows. When fetching the attributes for a cell, a
 * cascading merge is used to arrive at the attributes of the cell.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Insert text attributes into table cells. </td></tr>
 * <tr><td> Insert text attributes into table columns. </td></tr>
 * <tr><td> Insert text attributes into table rows. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface EnhancedTextTable extends TextTableModel, AttributeGrid
{
}
