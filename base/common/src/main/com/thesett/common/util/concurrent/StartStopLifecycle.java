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

import java.util.concurrent.TimeUnit;

/**
 * StartStopLifecycle provides control and query methods for working with resources that can be run asynchronously. It
 * provides methods for a controlling thread to start and stop the resource and to query whether it is currently
 * running.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Start the resource running.
 * <tr><td>Cleanly shut-down the resource once all its work is finished.
 * <tr><td>Immediately shut-down the resource without completing all of its work.
 * <tr><td>Check if a resource has been requested to shut-down.
 * <tr><td>Check if a resource has completely stopped running.
 * <tr><td>Wait for a resource to completely stop running.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface StartStopLifecycle
{
    /** Starts this resource running. */
    void start();

    /**
     * Prompts this resource to complete all of the events that are outstanding for processing by it, and to stop once
     * this is done.
     */
    void shutdown();

    /**
     * Instructs this resource to complete the immediate tasks that its thread are running, but to stop as soon as they
     * are complete, without emptying its event queue first.
     */
    void shutdownNow();

    /**
     * Checks if this resource has been requested to shut down.
     *
     * @return <tt>true</tt> if this resource has been requested to shut down, <tt>false</tt> otherwise.
     */
    boolean isShutdown();

    /**
     * Checks if this resource has stopped running, or has not yet been started.
     *
     * @return <tt>true</tt> if this resource has stopped running, or has not yet been started, <tt>false</tt>
     *         otherwise.
     */
    boolean isTerminated();

    /**
     * Waits for up to the specified time limit for this resource to terminate.
     *
     * @param  timeout The maximum length of time to wait for.
     * @param  unit    The units that the timeout is specifeid in.
     *
     * @return If the resource terminated before the timeout expired.
     *
     * @throws InterruptedException If the calling thread was interrupted whilst waiting for the resource to terminate.
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
}
