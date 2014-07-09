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
package com.thesett.aima.logic.fol.wam.debugger.monitor;

import java.beans.PropertyChangeListener;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BreakpointMonitor listens for updates to the "IP" register, which will occur every time a breakpoint (or step)
 * occurs.
 *
 * <p/>Once a break-point has been hit, the thread that is running the virtual machine will be paused by invoking the
 * {@link #pause()} method. The {@link #release()} method should release any paused thread and allow it to continue
 * running.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Watch for changes to the IP register. </td></tr>
 * <tr><td> Block and release execution threads. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BreakpointMonitor implements PropertyChangeListener
{
    Lock lock = new ReentrantLock();
    Condition released = lock.newCondition();

    /** Pauses any calling thread, until {@link #released} is invoked. */
    public void pause()
    {
        try
        {
            lock.lock();

            try
            {
                released.await();
            }
            catch (InterruptedException e)
            {
                // Exception set to null as compensation action of returning immediately with current thread interrupted
                // is taken.
                e = null;
                Thread.currentThread().interrupt();

                return;
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    /** Releases any threads waiting in the {@link #pause()} method. */
    public void release()
    {
        try
        {
            lock.lock();

            released.signalAll();
        }
        finally
        {
            lock.unlock();
        }
    }
}
