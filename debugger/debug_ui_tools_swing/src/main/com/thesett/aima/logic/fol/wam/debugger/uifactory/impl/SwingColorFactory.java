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
package com.thesett.aima.logic.fol.wam.debugger.uifactory.impl;

import java.awt.Color;

import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.Fader;

/**
 * SwingColorFactory provides a color factory that produces colors that work with AWT and Swing.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create colors from rgb components. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SwingColorFactory implements ColorFactory<Color>
{
    /** {@inheritDoc} */
    public Color createColor(int red, int green, int blue)
    {
        return new Color(red, green, blue);
    }

    /** {@inheritDoc} */
    public Color createColor(int[] rgb)
    {
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /** {@inheritDoc} */
    public Color createColor(float red, float green, float blue)
    {
        return new Color(red, green, blue);
    }

    /** {@inheritDoc} */
    public Fader<Color> createFader(Color startColor, Color endColor)
    {
        return new FaderImpl(startColor, endColor);
    }
}
