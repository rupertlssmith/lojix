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
package com.thesett.aima.logic.fol.wam.debugger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.thesett.common.util.concurrent.ShutdownHookable;
import com.thesett.common.util.concurrent.StartStopLifecycle;

/**
 * StartStopLifecycleBase provides a base implementation of {@link StartStopLifecycle}, for controlling the run
 * lifecycle of threaded resources.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>Start the resource running.
 * <tr><td>Cleanly shut-down the resource once all its work is finished.
 * <tr><td>Immediately shut-down the resource without completing all of its work.
 * <tr><td>Check if a resource has been requested to shut-down.
 * <tr><td>Check if a resource has completely stopped running.
 * <tr><td>Wait for a resource to completely stop running.
 * <tr><td>Make transitions between lifecycle states.
 * <tr><td>Provide a shutdown hook, to shut down the resource.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class StartStopLifecycleBase implements StartStopLifecycle, ShutdownHookable
{
    /** Defines the possible lifecycle states. */
    public enum State
    {
        /** Resource created, not yet running. */
        Initial,

        /** Resource running. */
        Running,

        /** Resource requested to shut down. */
        Shutdown,

        /** Resource terminated. */
        Terminated
    }

    /** The current lifecycle state. */
    protected volatile State state = State.Initial;

    /** Lock used to ensure threads are well-behaved around state changes. */
    protected ReadWriteLock stateLock = new ReentrantReadWriteLock();

    /** Condition used to signal changes of state. */
    protected Condition stateChange = stateLock.writeLock().newCondition();

    /** {@inheritDoc} */
    public void shutdown()
    {
        terminated();
    }

    /** {@inheritDoc} */
    public void shutdownNow()
    {
        terminated();
    }

    /** {@inheritDoc} */
    public Thread getShutdownHook()
    {
        return new Thread(new Runnable()
            {
                public void run()
                {
                    shutdown();
                }
            });
    }

    /**
     * Makes a transaction from the Initial state to the Running state, or no transition if the current state is not
     * Initial.
     */
    public void running()
    {
        try
        {
            stateLock.writeLock().lock();

            if (state == State.Initial)
            {
                state = State.Running;
                stateChange.signalAll();
            }
        }
        finally
        {
            stateLock.writeLock().unlock();
        }

    }

    /**
     * Makes a transaction from the Running state to the Shutdown state, or no transition if the current state is not
     * Running.
     */
    public void terminating()
    {
        try
        {
            stateLock.writeLock().lock();

            if (state == State.Running)
            {
                state = State.Shutdown;
                stateChange.signalAll();
            }
        }
        finally
        {
            stateLock.writeLock().unlock();
        }

    }

    /**
     * Makes a transaction from the Running or Shutdown state to the Terminated state, or no transition if the current
     * state is not Running or Shutdown.
     */
    public void terminated()
    {
        try
        {
            stateLock.writeLock().lock();

            if ((state == State.Shutdown) || (state == State.Running))
            {
                state = State.Terminated;
                stateChange.signalAll();
            }
        }
        finally
        {
            stateLock.writeLock().unlock();
        }

    }

    /** {@inheritDoc} */
    public boolean isShutdown()
    {
        return ((state == State.Shutdown) || (state == State.Terminated));
    }

    /** {@inheritDoc} */
    public boolean isTerminated()
    {
        return state == State.Terminated;
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
        try
        {
            stateLock.writeLock().lock();

            while (state != State.Terminated)
            {
                if (!stateChange.await(timeout, unit))
                {
                    return false;
                }
            }
        }
        finally
        {
            stateLock.writeLock().unlock();
        }

        return true;
    }
}
