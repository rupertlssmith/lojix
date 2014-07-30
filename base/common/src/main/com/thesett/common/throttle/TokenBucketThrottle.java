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
package com.thesett.common.throttle;

/**
 * TokenBucketThrottle is a Throttle implementation that uses a token bucket to limit the throttle rate. There is a
 * bucket of tokens (a count), which can be filled to a maximum level. Tokens are inserted into the bucket at a constant
 * rate. Every time a throttle request is made, a token is removed from the bucket. So long as there is a token
 * available to remove, the throttle request will pass.
 *
 * <p/>The advantage of a token bucket based throttle, is that it can allow the throttle rate to over-run its defined
 * limit for a short period, until the token bucket is exhausted. In the longer term it enforces an average rate.
 *
 * <p/>If the token bucket is empty, then the {@link Throttle#throttle()} method will block until there are some tokens
 * available. It will attempt to wait for one tokens length of time before waking up the blocked thread and adding a new
 * token. The accuracy of that wait will be constrained by the minimum wait time that the JVM can generate, which is
 * often around a few milliseconds.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Accept throttling rate in operations per second.
 * <tr><td>Inject short pauses to fill out processing cycles to a specified rate.
 * <tr><td>Check against a throttle speed without waiting.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TokenBucketThrottle extends BaseThrottle
{
    /** Holds the target rate for this throttle in hertz. */
    private float targetRate;

    /** Holds the maximum number of tokens in the bucket. */
    private int depth;

    /** Holds a count of the number of tokens that have been consumed. */
    private float tokenCount = 0f;

    /** Holds the time of the last throttle query. */
    private long lastTimeNanos = System.nanoTime();

    /**
     * Specifies the throttling rate in operations per second. This must be called with with a value, the inverse of
     * which is a measurement in nano seconds, such that the number of nano seconds do not overflow a long integer. The
     * value must also be larger than zero.
     *
     * @param hertz The throttling rate in cycles per second.
     */
    public void setRate(float hertz)
    {
        targetRate = hertz;
    }

    /**
     * Sets up the maximum size of the token bucket.
     *
     * @param depth The maximum size of the token bucket.
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * Checks but does not enforce the throttle rate. When this method is called, it checks if a length of time greater
     * than that equal to the inverse of the throttling rate has passed since it was last called and returned <tt>
     * true</tt>. If the length of time still to elapse to the next throttle allow point is zero or less, this method
     * will return a negative value, if there is still time to pass until the throttle allow point, this method will
     * return a positive value indicating the amount of time still to pass. A thread can wait for that period of time
     * before rechecking the throttle condition.
     *
     * @return The length of time since or still to elaps to the next throttle allow point. A negative value indicates
     *         that the throttle time has passed and a positive time indicates that there is still time to pass before
     *         it is reached. The units are in nanoseconds.
     */
    public long timeToThrottleNanos()
    {
        // Work out how long ago the last throttle query was.
        long currentTimeNanos = System.nanoTime();
        long delay = currentTimeNanos - lastTimeNanos;

        // Regenerate the tokens allowed since the last query.
        float numTokens = (delay * 1.0e9f) * targetRate;

        tokenCount -= numTokens;

        if (tokenCount < 0f)
        {
            tokenCount = 0f;
        }

        // Update the last time stamp for next time around.
        lastTimeNanos = currentTimeNanos;

        // Check if there are tokens available to consume, and consume one if so.
        if (tokenCount <= (depth - 1.0))
        {
            tokenCount += 1.0;

            return 0;
        }
        else
        {
            // There are no tokens to consume, so return an estimate of how long it will take to generate one token.
            return (long) (1.0e9 / targetRate);
        }
    }
}
