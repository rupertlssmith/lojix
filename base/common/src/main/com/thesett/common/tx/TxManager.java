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
 * TxManager is a helper class for setting up local transaction ids on the current thread to assist in calling
 * {@link Transactional}s. When running in a transactional mode, a transactional resource expects to have its methods
 * called with a valid transaction id attached to the current thread as a thread local variable. Passing variables as
 * thread locals means that the resource methods do not have to be polluted with extra parameters for transaction id
 * passing, so transactional resources can have exactly the same interface as non-transactional ones. This class
 * provides methods to create, assign and remove transaction ids to threads.
 *
 * <p/>Transactions may be coordinated by an external transaction manager which creates its own unique transaction
 * identifiers. This class provides a method {@link #assignTxIdToThread(TxId)} that maps such an external id onto the
 * internal one, creating a new internal one if one does not already exist for that external id. Subsequent calls to
 * this method for the same external id will result in the same internal id being reattached to the thread.
 *
 * <p/>Local transactions not involiving an external transaction manager should use the {@link #createTxId} method to
 * create and assign local ids to the current thread.
 *
 * <p/>When a transaction id is invalidated because a transaction has been completed, the {@link #invalidateTxId} method
 * should be called to notify this manager of the invalidation.
 *
 * <p/>The current transaction id may be removed from the current thread and re-attached on demand using the
 * {@link #removeTxIdFromThread}, {@link #assignTxIdToThread(TxId)} and {@link #assignTxIdToThread(TxId)} methods.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide a mapping from exernal transaction ids to internal (local) ones.
 * <tr><td>Generate local transaction ids.
 * <tr><td>Attach and detach transaction ids on the current thread.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TxManager
{
    /** Provides thread local variable assignment of resource transaction ids. */
    private static ThreadLocal<TxId> threadLocalTxId =
        new ThreadLocal<TxId>()
        {
            /**
             * Ids are always initialized to null before being assigned.
             *
             * @return Always null.
             */
            protected synchronized TxId initialValue()
            {
                return null;
            }
        };

    /**
     * Called when a local transaction id is invalidated. This method removes any corresponding mapping to external
     * transaction ids.
     *
     * @param txId The local transaction id invalidated.
     */
    public static void invalidateTxId(TxId txId)
    {
        // Mark the tx id as invalid.
        txId.invalidate();
    }

    /**
     * Creates a new local transaction id.
     *
     * @return The newly created local transaction id.
     */
    public static TxId createTxId()
    {
        return TxIdImpl.createTxId();
    }

    /**
     * Attaches the specified local transaction id to the current thread.
     *
     * @param txId The transaction id to attach.
     */
    public static void assignTxIdToThread(TxId txId)
    {
        threadLocalTxId.set(txId);
    }

    /**
     * Gets the currently assigned local transaction id from the current thread.
     *
     * @return The currently assigned local transaction id from the current thread.
     */
    public static TxId getTxIdFromThread()
    {
        return threadLocalTxId.get();
    }

    /**
     * Removes the currently assigned local transaction id from the current thread and returns its value.
     *
     * @return The currently assigned local transaction id from the current thread just prior to this method being
     *         called.
     */
    public static TxId removeTxIdFromThread()
    {
        TxId txId = threadLocalTxId.get();
        threadLocalTxId.remove();

        return txId;
    }

    /**
     * Gets the session associated with the current thread, if any.
     *
     * @return The session associated with the current thread, or <tt>null</tt> if none has been associated.
     */
    public static TxSession getCurrentSession()
    {
        return TxSessionImpl.getCurrentSession();
    }
}
