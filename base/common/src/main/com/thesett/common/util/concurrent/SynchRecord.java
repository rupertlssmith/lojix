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
 * SynchRecord encapsulates a data item from a {@link BatchSynchQueue} with its producer. This enables the asociation
 * between the data item and its producer to be exploited, by putting the data item back on the queue without unblocking
 * its producer, or in order to send exceptions to the producer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Get the underlying data element.
 * <tr><td>Put the data element back on the queue without unblocking its producer.
 * <tr><td>Send and exception to the data elements producer.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface SynchRecord<E>
{
    /**
     * Gets the data element contained by this record.
     *
     * @return The data element contained by this record.
     */
    E getElement();

    /**
     * Tells the synch queue to put this element back onto the queue instead of releasing its producer. The element is
     * not requeued immediately but upon calling the {@link SynchRef#unblockProducers()} method.
     *
     * <p/>This method will raise a runtime exception {@link AlreadyUnblockedException} if the producer for this element
     * has already been unblocked.
     */
    void reQueue();

    /**
     * Immediately releases the producer of this data record. Consumers can bring the synchronization time of producers
     * to a minimum by using this method to release them at the earliest possible moment when batch consuming records
     * from sychronized producers.
     */
    void releaseImmediately();

    /**
     * Tells the synch queue to raise an exception with this elements producer. The exception is not raised immediately
     * but upon calling the {@link SynchRef#unblockProducers()} method. The exception will be wrapped in a
     * {@link SynchException} before it is raised on the producer.
     *
     * <p/>This method is unusual in that it accepts an exception as an argument. This is non-standard but is used
     * because the exception is to be passed onto a different thread.
     *
     * <p/>This method will raise a runtime exception {@link AlreadyUnblockedException} if the producer for this element
     * has already been unblocked.
     *
     * @param e The exception to raise on the producer.
     */
    void inError(Exception e);
}
