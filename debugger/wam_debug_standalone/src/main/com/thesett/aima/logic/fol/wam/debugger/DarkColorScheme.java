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
package com.thesett.aima.logic.fol.wam.debugger;

import java.awt.Color;

import com.thesett.aima.logic.fol.wam.debugger.swing.ColorScheme;

/**
 * Provides a dark color scheme.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide dark colors.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DarkColorScheme implements ColorScheme
{
    /** {@inheritDoc} */
    public Color getMainText()
    {
        return Color.WHITE;
    }

    /** {@inheritDoc} */
    public Color getDisabledText()
    {
        return Color.LIGHT_GRAY;
    }

    /** {@inheritDoc} */
    public Color getHighlight1()
    {
        return Color.CYAN;
    }

    /** {@inheritDoc} */
    public Color getHighlight2()
    {
        return Color.CYAN;
    }

    /** {@inheritDoc} */
    public Color getHighlight3()
    {
        return Color.CYAN;
    }

    /** {@inheritDoc} */
    public Color getHighlight4()
    {
        return Color.CYAN;
    }

    /** {@inheritDoc} */
    public Color getUserWorkingBackground()
    {
        return Color.BLACK;
    }

    /** {@inheritDoc} */
    public Color getToolingBackground()
    {
        return Color.DARK_GRAY;
    }

    /** {@inheritDoc} */
    public Color getToolingActiveBackground()
    {
        return Color.LIGHT_GRAY;
    }
}
