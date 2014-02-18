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
package com.thesett.aima.state;

/**
 * Attribute is the root for defining advanced data types. All Java primitive and wrapper types do not implement
 * Attribute but all custom types do. The advantage of using Attribute is that it provides a link to the
 * {@link com.thesett.aima.state.Type} class which makes it easier to derive the type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Report the type of the attribute <td> {@link com.thesett.aima.state.Type}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Attribute
{
    /**
     * Should return a correct instance of the type class for this attribute.
     *
     * @return The attribute type of this attribute.
     */
    public Type<? extends Attribute> getType();
}
