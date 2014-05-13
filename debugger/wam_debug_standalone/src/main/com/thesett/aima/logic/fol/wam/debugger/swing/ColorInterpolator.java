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
package com.thesett.aima.logic.fol.wam.debugger.swing;

import java.awt.Color;
import java.util.Iterator;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ColorInterpolator implements Iterable<Color>
{
    /** The color to start at. */
    private final Color startColor;

    /** The color to end at. */
    private final Color endColor;

    /** The start color broken down into component values. */
    private float[] startRgbComponents;

    /** The end color broken down into component values. */
    private float[] endRgbComponents;

    /** The number of iteration steps in total. */
    private final int steps;

    /** The current step. */
    private int step;

    /** The current color. */
    private Color color;

    /**
     * Creates an interpolator between two colors.
     *
     * @param startColor The color to start at.
     * @param endColor   The color to end at.
     * @param steps      The number of iteration steps in total.
     */
    public ColorInterpolator(Color startColor, Color endColor, int steps)
    {
        this.startColor = startColor;
        this.endColor = endColor;
        this.steps = steps;

        startRgbComponents = startColor.getRGBComponents(startRgbComponents);
        endRgbComponents = endColor.getRGBComponents(endRgbComponents);
    }

    /** {@inheritDoc} */
    public Iterator<Color> iterator()
    {
        return new ColorIterator();
    }

    /**
     * Implements the color interpolation.
     */
    private class ColorIterator implements Iterator<Color>
    {
        private float[] rgbComponents = new float[4];

        /** {@inheritDoc} */
        public boolean hasNext()
        {
            return step < steps;
        }

        /**
         * {@inheritDoc}
         *
         * <p/>Uses the linear interpolation formula to calculate the new color:
         *
         * <pre>
         * y = y0 + (y1 - y0) * (x - x0) / (x1 - x0)
         * </pre>
         *
         * <p/>Where <tt>y</tt> is a component of the color, and <tt>x</tt> is time.
         */
        public Color next()
        {
            // Initialize the color to the start if this has not already been done.
            if (color == null)
            {
                color = startColor;
            }

            for (int i = 0; i < 4; i++)
            {
                rgbComponents[i] =
                    startRgbComponents[i] + ((endRgbComponents[i] - startRgbComponents[i]) * step / steps);
            }

            step++;

            return new Color(rgbComponents[0], rgbComponents[1], rgbComponents[2], rgbComponents[3]);
        }

        /** {@inheritDoc} */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
