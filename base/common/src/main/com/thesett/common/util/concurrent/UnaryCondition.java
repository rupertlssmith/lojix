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
package com.thesett.common.util.concurrent;

import com.thesett.common.util.logic.UnaryPredicate;

/**
 * A UnaryCondition is a unary predicate on objects where the predicate membership is changing with time and can be
 * queried by multiple threads, making it a 'condition' from the point of view of thread synchronization. The object to
 * test for membership can be thought of as a member of the set defined by its class. The unary condition is a function
 * mapping from this set to the set { true, false }, where the set definition is changing with time.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Determine membership of a predicate.
 * <tr><td>Wait for membership of a predicate to become possible.
 * <tr><td>Provide an estimate of how long it may take for membership of a predicate to become possible.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface UnaryCondition<T> extends UnaryPredicate<T>, Signalable, Notifying
{
    /**
     * Wait for the predicate to become true on the specified object.
     *
     * @param  t The object to test for predicate membership.
     *
     * @throws java.lang.InterruptedException If interrupted whilst waiting.
     */
    void await(T t) throws java.lang.InterruptedException;

    /**
     * Wait for up to a timeout limit for the predicate to become true on the specified object.
     *
     * @param  t        The object to test for predicate membership.
     * @param  l        The amount of time to wait.
     * @param  timeUnit The units of time used.
     *
     * @return <tt>true</tt> if the predicate became true before the timeout expired, <tt>false</tt> otherwise.
     *
     * @throws java.lang.InterruptedException If interrupted whilst waiting.
     */
    boolean await(T t, long l, java.util.concurrent.TimeUnit timeUnit) throws java.lang.InterruptedException;

    /**
     * Evaluates a logical predicate, and if the evaluation is false, provides an estimate of the time that a caller
     * should wait for before attempting to re-evaluate the predicate with the expectation that is may evaluate to true
     * at that time. If an implementation is not time dependant or cannot supply a reasonable estimate then
     * Long.MAX_VALUE can be returned to indicate that a very long wait should be used.
     *
     * @param  t The object to test for predicate membership.
     *
     * @return Zero or less if the predicate evaluate to true, or an estimate of the time remaining for the predicate to
     *         evaluate to true otherwise. The units of time are nanoseconds.
     */
    long evaluateWithWaitTimeNanos(T t);
}
