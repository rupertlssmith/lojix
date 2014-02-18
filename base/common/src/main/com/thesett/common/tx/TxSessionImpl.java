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

import java.util.HashSet;
import java.util.Set;

/**
 * TxSessionImpl provides an implementation of a transactional session for spanning of multiple transactional resources.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Apply commit or rollback over many transactional resources.
 * <tr><td>Bind a session to the current thread.
 * <tr><td>Unbind a session from the current thread.
 * <tr><td>Accept transactional resource enlist notifications.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TxSessionImpl implements TxSession
{
    /** Provides thread local variable assignment of sessions. */
    private static ThreadLocal<TxSession> threadSession =
        new ThreadLocal<TxSession>()
        {
            /**
             * Session are always initialized to null before being assigned.
             *
             * @return Always null.
             */
            protected synchronized TxSession initialValue()
            {
                return null;
            }
        };

    /** Holds the transaction id of the session. */
    TxId txId;

    /** Holds all transactional resources that have been enlisted in this session. */
    Set<Transactional> enlists = new HashSet<Transactional>();

    /**
     * Gets the session associated with the current thread, if any.
     *
     * @return The session associated with the current thread, or <tt>null</tt> if none has been associated.
     */
    public static TxSession getCurrentSession()
    {
        return threadSession.get();
    }

    /** Binds the session as a transactional context to the current thread, if it is not already bound. */
    public void bind()
    {
        // If necessary create a fresh transaction id.
        if ((txId == null) || !txId.isValid())
        {
            txId = TxManager.createTxId();
        }

        // Bind the transaction to the current thread.
        TxManager.assignTxIdToThread(txId);

        // Bind this session to the current thread.
        threadSession.set(this);
    }

    /** Unbinds the session as a transactional context from the current thread. */
    public void unbind()
    {
        // Unbind the transaction id from the current thread, but keep hold of it for subsequent rebinding.
        txId = TxManager.removeTxIdFromThread();

        // Unbind this session from the current thread.
        threadSession.remove();
    }

    /** Applies pending operations. */
    public void commit()
    {
        // Two phase commit over all hard resources enlisted.

        // Commit all soft resources.
        for (Transactional enlist : enlists)
        {
            enlist.commit();
        }

        // Clear all of the committed resources.
        enlists.clear();

        // Invalidate the transaction id, so that a fresh transaction is begun.
        txId = TxManager.removeTxIdFromThread();
        TxManager.invalidateTxId(txId);
        bind();
    }

    /** Forgets pending operations. */
    public void rollback()
    {
        // Rollback all soft resources.
        for (Transactional enlist : enlists)
        {
            enlist.rollback();
        }

        // Clear all of the rolled back resources.
        enlists.clear();

        // Invalidate the transaction id, so that a fresh transaction is begun.
        txId = TxManager.removeTxIdFromThread();
        TxManager.invalidateTxId(txId);
        bind();
    }

    /**
     * Enlists a transactional resource with this session.
     *
     * @param resource The resource to enlist.
     */
    public void enlist(Transactional resource)
    {
        // Enlist the resource if it has not already been enlisted.
        enlists.add(resource);
    }
}
