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
package com.thesett.common.throttle;

/**
 * Provides a base implementation of the non-waiting throttle checking method, using the system nano timer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check against a throttle speed without waiting.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseThrottle implements Throttle
{
    /** Holds the length of a single cycle in nano seconds. */
    protected long cycleTimeNanos;

    /** Holds the time of the last succesfull call to the check method. */
    private long lastCheckTimeNanos;

    /** Flag used to detect the first call to the {@link #checkThrottle()} method. */
    boolean firstCheckCall = true;

    /**
     * Flag used to detect the first call to the {@link #throttle()} method. Zero or negative start time cannot be
     * relied on to detect this as System.nanoTime can return zero or negative values.
     */
    boolean firstCall = true;

    /**
     * Specifies the throttling rate in operations per second. This must be called with with a value, the inverse of
     * which is a measurement in nano seconds, such that the number of nano seconds do not overflow a long integer. The
     * value must also be larger than zero.
     *
     * @param hertz The throttling rate in cycles per second.
     */
    public void setRate(float hertz)
    {
        // Check that the argument is above zero.
        if (hertz <= 0.0f)
        {
            throw new IllegalArgumentException("The throttle rate must be above zero.");
        }

        // Calculate the cycle time.
        cycleTimeNanos = (long) (1000000000f / hertz);

        // Reset the first pass flag.
        firstCall = false;
        firstCheckCall = false;
    }

    /**
     * This method can only be called at the rate set by the {@link #setRate} method, if it is called faster than this
     * it will inject short pauses to restrict the call rate to that rate.
     *
     * @throws InterruptedException If interrupted whilst performing a blocking wait on the throttle.
     */
    public void throttle() throws InterruptedException
    {
        // Don't introduce any pause on the first call.
        if (!firstCall)
        {
            // Check if there is any time left in the cycle since the last throttle call to this method and introduce a
            // short pause to fill that time if there is.
            long remainingTimeNanos = timeToThrottleNanos();

            while (remainingTimeNanos > 0)
            {
                long milliPause = remainingTimeNanos / 1000000;
                int nanoPause = (int) (remainingTimeNanos % 1000000);

                Thread.sleep(milliPause, nanoPause);

                remainingTimeNanos = timeToThrottleNanos();
            }
        }
        else
        {
            firstCall = false;
        }
    }

    /**
     * Checks but does not enforce the throttle rate. When this method is called, it checks if a length of time greater
     * than that equal to the inverse of the throttling rate has passed since it was last called and returned <tt>
     * true</tt>
     *
     * @return <tt>true</tt> if a length of time greater than that equal to the inverse of the throttling rate has
     *         passed since this method was last called and returned <tt>true</tt>, <tt>false</tt> otherwise. The very
     *         first time this method is called on a throttle, it returns <tt>true</tt> as the base case to the above
     *         self-referential definition.
     */
    public boolean checkThrottle()
    {
        return timeToThrottleNanos() <= 0;
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
        long now = System.nanoTime();
        long remainingNanos = (cycleTimeNanos + lastCheckTimeNanos) - now;

        if ((remainingNanos <= 0))
        {
            firstCheckCall = false;
            lastCheckTimeNanos = now;

            return remainingNanos;
        }
        else if (firstCheckCall)
        {
            firstCheckCall = false;
            lastCheckTimeNanos = now;

            return 0;
        }
        else
        {
            return remainingNanos;
        }
    }
}
