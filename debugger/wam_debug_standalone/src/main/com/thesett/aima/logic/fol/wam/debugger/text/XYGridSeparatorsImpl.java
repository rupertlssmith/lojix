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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class XYGridSeparatorsImpl implements XYGridSeparators
{
    /** Holds the horizontal separators. */
    protected Separators horizontalSeparators = new Separators();

    /** Holds the vertical separators. */
    protected Separators verticalSeparators = new Separators();

    /** {@inheritDoc} */
    public void insertHorizontalSeparator(int r, int pixelHeight)
    {
        horizontalSeparators.insertSeparator(r, pixelHeight);
    }

    /** {@inheritDoc} */
    public void insertVerticalSeparator(int c, int pixelWidth)
    {
        verticalSeparators.insertSeparator(c, pixelWidth);
    }

    /** {@inheritDoc} */
    public SortedMap<Integer, Integer> getHorizontalSeparators()
    {
        return horizontalSeparators.getSeparators();
    }

    /** {@inheritDoc} */
    public SortedMap<Integer, Integer> getVerticalSeparators()
    {
        return verticalSeparators.getSeparators();
    }

    private class Separators
    {
        /** A set of separators sorted in ascending order of position. */
        private Map<Integer, Integer> separators = new HashMap<Integer, Integer>();

        public void insertSeparator(int position, int pixelSize)
        {
            separators.put(position, pixelSize);
        }

        public SortedMap<Integer, Integer> getSeparators()
        {
            return new TreeMap<Integer, Integer>(separators);
        }

        public void mergeIn(SortedMap<Integer, Integer> separators)
        {
        }
    }
}
