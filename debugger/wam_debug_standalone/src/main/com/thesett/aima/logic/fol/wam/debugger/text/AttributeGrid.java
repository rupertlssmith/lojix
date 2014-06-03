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
    /** {@inheritDoc} */
    void insertAttribute(AttributeSet attributes, int c, int r);

    /** {@inheritDoc} */
    void insertColumnAttribute(AttributeSet attributes, int c);

    /** {@inheritDoc} */
    void insertRowAttribute(AttributeSet attributes, int r);

    /** {@inheritDoc} */
    AttributeSet getAttributeAt(int c, int r);
}
