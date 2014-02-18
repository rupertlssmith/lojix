/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
package com.thesett.common.tx;

/**
 * A TxResourceEnlister accepts enlist notification from {@link Transactional}s. Transactional resource can be enlisted
 * or automatically enlist themselves with a transactional context, where a set of transactionally modified resources
 * needs to be tracked in order to apply commits over many resources.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Accept transactional resource enlist notifications.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TxResourceEnlister
{
    /**
     * Enlists a transactional resource.
     *
     * @param resource The resource to enlist.
     */
    public void enlist(Transactional resource);
}
