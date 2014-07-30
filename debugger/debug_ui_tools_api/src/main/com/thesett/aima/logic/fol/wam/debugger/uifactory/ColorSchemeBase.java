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
package com.thesett.aima.logic.fol.wam.debugger.uifactory;

/**
 * ColorSchemeBase provides a base implementation for deriving color schemes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Convert RGB components to color instances. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class ColorSchemeBase<C> implements ColorScheme<C>
{
    /** RGB for white. */
    protected static final int[] WHITE = new int[] { 255, 255, 255 };

    /** RGB for light gray. */
    protected static final int[] LIGHT_GRAY = new int[] { 192, 192, 192 };

    /** RGB for black. */
    protected static final int[] BLACK = new int[] { 0, 0, 0 };

    /** RGB for dark gray. */
    protected static final int[] DARK_GRAY = new int[] { 64, 64, 64 };

    /** Holds the color factory used to create the colors. */
    protected final ColorFactory<C> colorFactory;

    /**
     * Builds a color scheme using the supplied color factory.
     *
     * @param colorFactory The color factory to use.
     */
    public ColorSchemeBase(ColorFactory<C> colorFactory)
    {
        this.colorFactory = colorFactory;
    }

    /**
     * Constructs a color from its RGB components.
     *
     * @param  rgb The RGB components of the color.
     *
     * @return An instance of the color.
     */
    protected C color(int[] rgb)
    {
        return colorFactory.createColor(rgb);
    }
}
