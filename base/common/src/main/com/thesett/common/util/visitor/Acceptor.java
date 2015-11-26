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
package com.thesett.common.util.visitor;

/**
 * Acceptor is an abstraction of the root type in a hierarchy, that accepts visitors from a hierarchy of visitor over
 * the type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Accept a visitor and apply it to this.
 * </table></pre>
 *
 * @param  <V> The root type of the visitable hierarchy.
 *
 * @author Rupert Smith
 */
public interface Acceptor<V>
{
    /**
     * Accepts a visitor and applies it to this.
     *
     * @param visitor The visitor to apply to this.
     */
    void accept(Visitor<V> visitor);
}
