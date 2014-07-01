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
package com.thesett.aima.logic.fol.wam.debugger.uifactory;

/**
 * ColorFactory is used used to create colors, that are compatible with other elements of the UI depending on UI toolkit
 * being used.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create colors from rgb components. </td></tr>
 * </table></pre>
 *
 * @param  <C> The type of colors this factory produces.
 *
 * @author Rupert Smith
 */
public interface ColorFactory<C>
{
    /**
     * Creates a color from RGB components.
     *
     * @param  red   The amount of red. Legal values are 0 to 255.
     * @param  green The amount of green. Legal values are 0 to 255.
     * @param  blue  The amount of blue. Legal values are 0 to 255.
     *
     * @return A color.
     */
    C createColor(int red, int green, int blue);

    /**
     * Creates a color from RGB components.
     *
     * @param  rgb An array of 3 integer components for red, green and blue. Legal values are 0 to 255.
     *
     * @return A color.
     */
    C createColor(int[] rgb);

    /**
     * Creates a color from RGB components.
     *
     * @param  red   The amount of red. Legal values are 0.0 to 1.0.
     * @param  green The amount of green. Legal values are 0.0 to 1.0.
     * @param  blue  The amount of blue. Legal values are 0.0 to 1.0.
     *
     * @return A color.
     */
    C createColor(float red, float green, float blue);

    /**
     * Creates a new color fader.
     *
     * @param  startColor The color to start fading from.
     * @param  endColor   The color to fade to.
     *
     * @return A new color fader.
     */
    Fader<C> createFader(C startColor, C endColor);
}
