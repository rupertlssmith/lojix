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
package com.thesett.aima.state.restriction;

/**
 * Describes a maximum allowed value of an integer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Represent a maximum allowed value. </td></tr>
 * </table></pre>
 */
public class MaxRestriction implements TypeRestriction
{
    private final long max;

    public MaxRestriction(long max)
    {
        this.max = max;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return "Max";
    }

    public long getMax()
    {
        return max;
    }
}
