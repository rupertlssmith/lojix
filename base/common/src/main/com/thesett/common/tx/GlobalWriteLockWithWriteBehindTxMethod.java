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
package com.thesett.common.tx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.thesett.common.error.NotImplementedException;

/**
 * GlobalWriteLockWithWriteBehindTxMethod provides a global read-write lock based scheme, with write-behind operations,
 * for managing transactional updates to a resource.
 *
 * <p/>Exclusive write locks are taken to ensure that only transaction at a time may commit its write-behind operations.
 * The write-behind operations are held in a map, per transaction id, and this map is a ConcurrentHashMap, so that
 * mutliple transactions can create their write-behind operations in parallel.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Apply pending write behind operations.
 * <tr><td>Forget pending write behind operations.
 * <tr><td>Take record of and synchronize updating operations on a transactional resource.
 * <tr><td>Take record of and synchronize reading operations on a transactional resource.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   When transaction ids are discarded, are their write-behind caches cleaned up? Caches are cleaned up on commit
 *         or rollback. Dangling transactions will cause leaks, need to think about how to handle them.
 * @todo   Need to decide what to do if the commit is interrupted? If commit thread is interrupted presumably that means
 *         that it is deliberately being brought down immediately, with no intention to complete the transaction. It
 *         will be possible to retry, but that does not seem to be the intention of interrupting a commit. I think an
 *         exception needs to be added to the commit signature to allow for commit failures. For the moment it gets
 *         rethrown as a runtime.
 */
public class GlobalWriteLockWithWriteBehindTxMethod<E> implements TxMethod
{
    /** Holds the isolation level of the resource. */
    private IsolationLevel isolationLevel = IsolationLevel.ReadCommitted;

    /** Holds the global write lock that ensures that only one writer at a time is allowed to alter the resource. */
    private final ReadWriteLock globalLock = new ReentrantReadWriteLock();

    /** A condition on the global write lock that is used to signal when it becomes unowned by any transaction. */
    private final Condition globalWriteLockFree = globalLock.writeLock().newCondition();

    /** Holds the transaction id of the only transaction that is allowed to use the global write lock. */
    private TxId globalWriteLockTxId;

    /** Holds the write-behind cache of changes made by transactions. */
    private final Map<TxId, List<TxOperation>> txWrites = new ConcurrentHashMap<TxId, List<TxOperation>>();

    /**
     * When operating in transactional mode causes any changes since the last commit to be made visible to the search
     * method.
     */
    public void commit()
    {
        TxId txId = null;

        // Check if in a higher transactional mode than none, otherwise commit does nothing.
        if (!getIsolationLevel().equals(IsolationLevel.None))
        {
            // Extract the current transaction id.
            txId = TxManager.getTxIdFromThread();

            // Wait until the global write lock can be acquired by this transaction.
            try
            {
                acquireGlobalWriteLock(txId);
            }
            catch (InterruptedException e)
            {
                // The commit was interrupted, so cannot succeed.
                throw new IllegalStateException("Interrupted whilst commit is waiting for global write lock.", e);
            }

            // Check that this transaction has made changes to be committed.
            List<TxOperation> alterations = txWrites.get(txId);

            try
            {
                if (alterations != null)
                {
                    // Loop through all the writes that the transaction wants to apply to the resource.
                    for (TxOperation nextAlteration : alterations)
                    {
                        // Apply the change and update the term resource.
                        nextAlteration.execute();
                    }

                    // Clear the write behind cache for this transaction as its work has been completed.
                    txWrites.remove(txId);
                }
            }
            finally
            {
                // Release the global write lock.
                releaseGlobalWriteLock();
            }
        }
    }

    /**
     * When operation in transactional mode causes any changes since the last commit to be dropped and never made
     * visible to the search method.
     */
    public void rollback()
    {
        TxId txId = null;

        // Check if in a higher transactional mode than none, otherwise commit does nothing.
        if (!getIsolationLevel().equals(IsolationLevel.None))
        {
            // Extract the current transaction id.
            txId = TxManager.getTxIdFromThread();

            // Check that this transaction has made changes to be rolled back.
            List<TxOperation> alterations = txWrites.get(txId);

            if (alterations != null)
            {
                // Loop through all the writes that the transaction wants to apply to the resource.
                for (TxOperation nextAlteration : alterations)
                {
                    // Cancel the operation.
                    nextAlteration.cancel(false);
                }
            }

            // Discard all the changes that the transaction was going to make.
            txWrites.remove(txId);
        }
    }

    /**
     * Requests an operation that alters the transactional resource. This may be blocked until an appropriate lock can
     * be acquired, delayed until commit time, or actioned upon a copy of the data structure private to a transaction
     * branch.
     *
     * <p/>This stores the requested operation for execution at commit time, unless running in non-transactional mode in
     * which case it is run immediately.
     *
     * @param op The write operation as a future.
     */
    public void requestWriteOperation(TxOperation op)
    {
        // Check if in a higher transactional mode than none and capture the transaction id if so.
        TxId txId = null;

        if (getIsolationLevel().compareTo(IsolationLevel.None) > 0)
        {
            // Extract the current transaction id.
            txId = TxManager.getTxIdFromThread();

            // Ensure that this resource is enlisted with the current session.
            enlistWithSession();

        }

        // For non-transactional isolation levels, apply the requested operation immediately.
        if (getIsolationLevel().equals(IsolationLevel.None))
        {
            op.execute();
        }

        // Add the operation to the transaction write-behind cache for the transaction id, if using transactional
        // isolation, to defer the operation untill commit time.
        else
        {
            addCachedOperation(txId, op);
        }
    }

    /**
     * Requests an operation that only reads from the transactional resource. If neccessary locks may be acquired, or
     * the operation may otherwise be delayed, or read from a copy of the data, as appropriate.
     *
     * @param  op The read operation as a future.
     *
     * @return The value read from the resource.
     */
    public E requestReadOperation(TxOperation op)
    {
        throw new NotImplementedException();

        // Ensure that this resource is enlisted with the current session.
        // enlistWithSession();

        // Acquire a read lock.
        // Apply the operation.
        // Release the read lock.
    }

    /**
     * Gets this transactional methods isolation level.
     *
     * @return This transactional methods isolation level.
     */
    public IsolationLevel getIsolationLevel()
    {
        return isolationLevel;
    }

    /**
     * Sets this transactional methods isolation level.
     *
     * @param isolationLevel This transactional methods new isolation level.
     */
    public void setIsolationLevel(IsolationLevel isolationLevel)
    {
        this.isolationLevel = isolationLevel;
    }

    /**
     * Adds a transactional operation to the transactional write-behind cache for the specified transaction. If no cache
     * exists for the specified transaction id, a new one is created.
     *
     * @param txId                 The transaction id to store the operation against.
     * @param cachedWriteOperation The operation to store.
     */
    private void addCachedOperation(TxId txId, TxOperation cachedWriteOperation)
    {
        List<TxOperation> writeCache = txWrites.get(txId);

        if (writeCache == null)
        {
            writeCache = new ArrayList<TxOperation>();
            txWrites.put(txId, writeCache);
        }

        writeCache.add(cachedWriteOperation);
    }

    /**
     * Waits until a global read lock can be acquired by the specified transaction.
     *
     * @param txId The transaction id to acquite a global read lock for.
     */
    private void acquireGlobalReadLock(TxId txId)
    {
        // Get the global read lock to ensure only one thread at a time can execute this code.
        globalLock.readLock().lock();
    }

    /** Releases the global write lock from being assigned to a transaction. */
    private void releaseGlobalReadLock()
    {
        // Get the global write lock to ensure only one thread at a time can execute this code.
        globalLock.readLock().unlock();
    }

    /**
     * Waits until the global write lock can be acquired by the specified transaction.
     *
     * @param  txId The transaction id to acquite the global write lock for.
     *
     * @throws InterruptedException If interrupted whilst waiting for the global write lock.
     */
    private void acquireGlobalWriteLock(TxId txId) throws InterruptedException
    {
        // Get the global write lock to ensure only one thread at a time can execute this code.
        globalLock.writeLock().lock();

        // Use a try block so that the corresponding finally block guarantees release of the thread lock.
        try
        {
            // Check that this transaction does not already own the lock.
            if (!txId.equals(globalWriteLockTxId))
            {
                // Wait until the write lock becomes free.
                while (globalWriteLockTxId != null)
                {
                    globalWriteLockFree.await();
                }

                // Assign the global write lock to this transaction.
                globalWriteLockTxId = txId;
            }
        }
        finally
        {
            // Ensure that the thread lock is released once assignment of the write lock to the transaction is complete.
            globalLock.writeLock().unlock();
        }
    }

    /** Releases the global write lock from being assigned to a transaction. */
    private void releaseGlobalWriteLock()
    {
        // Get the global write lock to ensure only one thread at a time can execute this code.
        globalLock.writeLock().lock();

        // Use a try block so that the corresponding finally block guarantees release of the thread lock.
        try
        {
            // Release the global write lock, assigning it to no transaction.
            globalWriteLockTxId = null;

            // Signal that the write lock is now free.
            globalWriteLockFree.signal();
        }

        // Ensure that the thread lock is released once assignment of the write lock to the transaction is complete.
        finally
        {
            globalLock.writeLock().unlock();
        }
    }

    /**
     * Enlists this transactional resource with the current session. If no session exists this will fail.
     *
     * @throws IllegalStateException If this resource is transactional and accessed outside of a transaction.
     */
    private void enlistWithSession()
    {
        TxSession session = TxSessionImpl.getCurrentSession();

        // Ensure that this resource is being used within a session.
        if (session == null)
        {
            throw new IllegalStateException("Cannot access transactional resource outside of a session.");
        }

        // Ensure that this resource is enlisted with the session.
        session.enlist(this);
    }
}
