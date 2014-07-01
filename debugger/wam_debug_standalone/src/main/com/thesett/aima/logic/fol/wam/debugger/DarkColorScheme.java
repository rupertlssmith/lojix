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

import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorScheme;

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
public class DarkColorScheme<C> implements ColorScheme<C>
{
    private static final int[] WHITE = new int[] { 255, 255, 255 };
    private static final int[] CYAN = new int[] { 0, 255, 255 };
    private static final int[] LIGHT_GRAY = new int[] { 192, 192, 192 };
    private static final int[] DARK_GRAY = new int[] { 64, 64, 64 };
    private static final int[] BLACK = new int[] { 0, 0, 0 };

    /** Holds the color factory used to create the colors. */
    private final ColorFactory<C> colorFactory;

    /**
     * Creates a dark color scheme using the supplied factory to create the colors.
     *
     * @param colorFactory The color factory.
     */
    public DarkColorScheme(ColorFactory<C> colorFactory)
    {
        this.colorFactory = colorFactory;
    }

    /** {@inheritDoc} */
    public C getMainText()
    {
        return colorFactory.createColor(WHITE);
    }

    /** {@inheritDoc} */
    public C getDisabledText()
    {
        return colorFactory.createColor(LIGHT_GRAY);
    }

    /** {@inheritDoc} */
    public C getHighlight1()
    {
        return colorFactory.createColor(CYAN);
    }

    /** {@inheritDoc} */
    public C getHighlight2()
    {
        return colorFactory.createColor(CYAN);
    }

    /** {@inheritDoc} */
    public C getHighlight3()
    {
        return colorFactory.createColor(CYAN);
    }

    /** {@inheritDoc} */
    public C getHighlight4()
    {
        return colorFactory.createColor(CYAN);
    }

    /** {@inheritDoc} */
    public C getUserWorkingBackground()
    {
        return colorFactory.createColor(BLACK);
    }

    /** {@inheritDoc} */
    public C getToolingBackground()
    {
        return colorFactory.createColor(DARK_GRAY);
    }

    /** {@inheritDoc} */
    public C getToolingActiveBackground()
    {
        return colorFactory.createColor(LIGHT_GRAY);
    }
}
