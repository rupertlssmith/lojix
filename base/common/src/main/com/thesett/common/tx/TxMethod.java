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
 * TxMethod supplies a particular implementation strategy for dealing with transactional operations. The operations to
 * be performed on a transactionally protected resources are passed to the transaction method as continuations. There
 * are methods that accept requests for altering operations ({@link #requestWriteOperation}) and read-only operations
 * ({@link #requestReadOperation}). Particualr implementations of the transaction method supply different strategies,
 * appropriate to the resource, in order to make it transactional.
 *
 * <p/>Some examples of transactional method implementation strategies are:
 *
 * <pre><p/>
 * <ul>
 * <li>Only allow one tx to write to part of a resource such as an individual record, at a time. Allow multiple readers.
 *     Implement using read/write locks on resources. Other transactions are blocked until locks are released at commit
 *     time. Care must be taken to to prevent dead-lock when individual records are locked in opposite orders.</li>
 * <li>Only allow one tx to write to a whole resource at a time. Allow multiple readers. Implement using global
 *     read/write locks on the resources. Other transactions are blocked until locks are released at commit time.</li>
 * <li>Apply operations to copies of the resource for different transactions. Upon commit merge the copies into the
 *     definitive version.</li>
 * <li>Use a read/write locking strategy, but delay operations until commit time. Only acquire and hold locks for the
 *     duration of a commit. If locks can be taken in a definitive order, it may be possible to run multiple commits in
 *     parallel whilst avoiding dead-locks.</li>
 * </ul></pre>
 *
 * <p/>In general, transactional strategies may need to be written to work with specific resources in a way that is
 * correct for and specific to the resource.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr><td>Take record of and synchronize updating operations on a transactional resource.
 * <tr><td>Take record of and synchronize reading operations on a transactional resource.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TxMethod<E> extends Transactional
{
    /**
     * Requests an operation that alters the transactional resource. This may be blocked until an appropriate lock can
     * be acquired, delayed until commit time, actioned upon a copy of the data structure private to a transaction
     * branch, or another appropriate transaction strategy may be used.
     *
     * @param op The write operation as a future.
     */
    public void requestWriteOperation(TxOperation op);

    /**
     * Requests an operation that only reads from the transactional resource. If neccessary locks may be acquired, or
     * the operation may otherwise be delayed, or read from a copy of the data, or another transaction strategy may be
     * used as appropriate.
     *
     * @param  op The read operation as a future.
     *
     * @return The value read from the resource.
     */
    public E requestReadOperation(TxOperation op);

    /**
     * Gets this transactional methods isolation level.
     *
     * @return This transactional methods isolation level.
     */
    IsolationLevel getIsolationLevel();

    /**
     * Sets this transactional methods isolation level.
     *
     * @param isolationLevel This transactional methods new isolation level.
     */
    void setIsolationLevel(IsolationLevel isolationLevel);
}
