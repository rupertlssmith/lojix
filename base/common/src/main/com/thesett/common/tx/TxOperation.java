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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.thesett.common.error.NotImplementedException;

/**
 * A TxOperation is an operation which is can be executed against a transactionally managed resource. A transactional
 * operation may have to acquire a lock on a resource in order to be applied, or it may represent a change against a
 * resource, or a copy of a resource, that is to be applied to the original resource at a later point in time.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide a continuation.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class TxOperation<V> implements Future<V>
{
    /** Applies the delayed or potentially blocking transactional operation to the transaction resource. */
    public abstract void execute();

    /**
     * Attempts to cancel execution of this task. This attempt will fail if the task has already completed, already been
     * cancelled, or could not be cancelled for some other reason. If successful, and this task has not started when
     * <tt>cancel</tt> is called, this task should never run. If the task has already started, then the <tt>
     * mayInterruptIfRunning</tt> parameter determines whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     *
     * @param  mayInterruptIfRunning <tt>true</tt> if the thread executing this task should be interrupted; otherwise,
     *                               in-progress tasks are allowed to complete.
     *
     * @return <tt>false</tt> if the task could not be cancelled, typically because it has already completed normally;
     *         <tt>true</tt> otherwise.
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return true;
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed normally.
     *
     * @return <tt>true</tt> if task was cancelled before it completed.
     */
    public boolean isCancelled()
    {
        throw new NotImplementedException();
    }

    /**
     * Returns <tt>true</tt> if this task completed.
     *
     * <p/>Completion may be due to normal termination, an exception, or cancellation; in all of these cases, this
     * method will return <tt>true</tt>.
     *
     * @return <tt>true</tt> if this task completed.
     */
    public boolean isDone()
    {
        throw new NotImplementedException();
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return The computed result.
     *
     * @throws java.util.concurrent.CancellationException If the computation was cancelled.
     * @throws java.util.concurrent.ExecutionException    If the computation threw an exception.
     * @throws InterruptedException                       If the current thread was interrupted while waiting.
     */
    public V get() throws InterruptedException, ExecutionException
    {
        throw new NotImplementedException();
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
     * available.
     *
     * @param  timeout The maximum time to wait.
     * @param  unit    The time unit of the timeout argument.
     *
     * @return The computed result.
     *
     * @throws java.util.concurrent.CancellationException If the computation was cancelled.
     * @throws java.util.concurrent.ExecutionException    If the computation threw an exception.
     * @throws InterruptedException                       If the current thread was interrupted while waiting.
     * @throws java.util.concurrent.TimeoutException      If the wait timed out.
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        throw new NotImplementedException();
    }
}
