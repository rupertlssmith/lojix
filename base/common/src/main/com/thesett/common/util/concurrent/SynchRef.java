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
package com.thesett.common.util.concurrent;

/**
 * A SynchRef is an interface which is returned from the synchronous take and drain methods. It allows the consumer to
 * communicate when it wants producers that have had their data taken to be unblocked.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Provide the number of records in this reference.
 * <tr><td>Allow all of the producers of the elements in this reference to be unblocked.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface SynchRef
{
    /**
     * Provides the number of records associated with this reference.
     *
     * @return The number of records associated with this reference.
     */
    int getNumRecords();

    /**
     * Any producers that have had their data elements taken from the queue but have not been unblocked are unblocked
     * when this method is called. The exception to this is producers that have had their data put back onto the queue
     * by a consumer. Producers that have had exceptions for their data items registered by consumers will be unblocked
     * but will not return from their put call normally, but with an exception instead.
     */
    void unblockProducers();
}
