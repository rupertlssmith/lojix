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
package com.thesett.aima.logic.fol.wam.debugger;

import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorSchemeBase;

/**
 * Provides a dark color scheme.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide dark colors.
 * </table></pre>
 *
 * @param  <C> The color implementation type.
 *
 * @author Rupert Smith
 */
public class DarkColorScheme<C> extends ColorSchemeBase<C>
{
    /** RGB for cyan. */
    private static final int[] CYAN = new int[] { 0, 255, 255 };

    /** RGB for a dull blue. */
    private static final int[] DUSTY_BLUE = new int[] { 0, 0x66, 0xCC };

    /**
     * Creates a dark color scheme using the supplied factory to create the colors.
     *
     * @param colorFactory The color factory.
     */
    public DarkColorScheme(ColorFactory<C> colorFactory)
    {
        super(colorFactory);
    }

    /** {@inheritDoc} */
    public C getMainText()
    {
        return color(WHITE);
    }

    /** {@inheritDoc} */
    public C getDisabledText()
    {
        return color(LIGHT_GRAY);
    }

    /** {@inheritDoc} */
    public C getHighlight1()
    {
        return color(CYAN);
    }

    /** {@inheritDoc} */
    public C getHighlight2()
    {
        return color(CYAN);
    }

    /** {@inheritDoc} */
    public C getHighlight3()
    {
        return color(CYAN);
    }

    /** {@inheritDoc} */
    public C getHighlight4()
    {
        return color(CYAN);
    }

    /** {@inheritDoc} */
    public C getBackground()
    {
        return color(BLACK);
    }

    /** {@inheritDoc} */
    public C getSelectionBackground()
    {
        return color(LIGHT_GRAY);
    }

    /** {@inheritDoc} */
    public C getLowlightBackground()
    {
        return color(DARK_GRAY);
    }

    /** {@inheritDoc} */
    public C getHighlightBackground()
    {
        return color(DUSTY_BLUE);
    }
}
