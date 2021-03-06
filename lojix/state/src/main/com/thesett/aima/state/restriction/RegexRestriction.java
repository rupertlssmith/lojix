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
 * Describes a regular expression on allowable values of a string.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Represent allowable strings matching a regular expression. </td></tr>
 * </table></pre>
 */
public class RegexRestriction implements TypeRestriction
{
    private final String pattern;

    public RegexRestriction(String pattern)
    {
        this.pattern = pattern;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return "Regex";
    }

    public String getPattern()
    {
        return pattern;
    }
}
