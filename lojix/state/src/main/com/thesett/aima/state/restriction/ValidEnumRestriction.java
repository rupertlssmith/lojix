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
 * Describes an enum type that a value must fall within the bounds of.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Represent a value that must fall within an enum. </td></tr>
 * </table></pre>
 */
public class ValidEnumRestriction implements TypeRestriction
{
    private final String enumTypeName;

    public ValidEnumRestriction(String enumTypeName)
    {
        this.enumTypeName = enumTypeName;
    }

    public String getName()
    {
        return "ValidEnum";
    }

    public String getEnumTypeName()
    {
        return enumTypeName;
    }
}
