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
package com.thesett.text.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.thesett.common.util.concurrent.ShutdownHookable;
import com.thesett.common.util.concurrent.StartStopLifecycle;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class StartStopLifecycleBase implements StartStopLifecycle, ShutdownHookable
{
    public enum State
    {
        Initial, Running, Shutdown, Terminated
    }

    protected volatile State state = State.Initial;

    protected ReadWriteLock stateLock = new ReentrantReadWriteLock();
    protected Condition stateChange = stateLock.writeLock().newCondition();

    public void shutdown()
    {
        terminated();
    }

    public void shutdownNow()
    {
        terminated();
    }

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

    public boolean isShutdown()
    {
        return ((state == State.Shutdown) || (state == State.Terminated));
    }

    public boolean isTerminated()
    {
        return state == State.Terminated;
    }

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
