/*
 * © Copyright Rupert Smith, 2005 to 2013.
 *
 * ALL RIGHTS RESERVED. Any unauthorized reproduction or use of this
 * material is prohibited. No part of this work may be reproduced or
 * transmitted in any form or by any means, electronic or mechanical,
 * including photocopying, recording, or by any information storage
 * and retrieval system without express written permission from the
 * author.
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
