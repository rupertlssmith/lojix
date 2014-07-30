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
package com.thesett.aima.state.impl;

import com.thesett.aima.state.Attribute;
import com.thesett.aima.state.Type;

/**
 * TypeHelper provides some helper methods for deriving the {@link Type}s of objects.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Get the type of an object.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TypeHelper
{
    /**
     * Gets the type of a specified object.
     *
     * @param  o The object to get the type of.
     *
     * @return The type of the object.
     */
    public static Type getTypeFromObject(Object o)
    {
        // Check if the object is null, in which case its type cannot be derived.
        if (o == null)
        {
            return new UnknownType();
        }

        // Check if the object is an attribute a and get its type that way if possible.
        if (o instanceof Attribute)
        {
            return ((Attribute) o).getType();
        }

        // Return an approproate Type for the java primitive, wrapper or class type of the argument.
        return new JavaType(o);
    }
}
