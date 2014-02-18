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
package com.thesett.common.util;

/**
 * Sizeof provides an alternative mechanism to the C sizeof() method for Java. Java does not provide a sizeof() method
 * so getting an exact memeory size of a data structure is not possible using the standard library.
 *
 * <p>The Sizeof class requires a quiescent JVM so that the heap activity is only due to object allocations and garbage
 * collections requested by the measuring thread. It also requires a large number of identical object instances. This
 * does not work when you want to size a single large object.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Calculates how much memory the virtual machine is currently using.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Sizeof
{
    /** Used to hold a reference to the Runtime class for querying memory and triggering garbage collection. */
    private static final Runtime RUNTIME = Runtime.getRuntime();

    /**
     * Repeatedly runs the garbage collector and finalization method of the JVM runtime system until the used memory
     * count becomes stable or 500 iterations occur, whichever happens soonest. If other threads are active then this
     * method is not likely to work as the used memory count will continually be changing.
     */
    private static void runGCTillStable()
    {
        // Possibly add another iteration in here to run this whole method 3 or 4 times.

        long usedMem1 = usedMemory();
        long usedMem2 = Long.MAX_VALUE;

        // Repeatedly garbage collection until the used memory count becomes stable, or 500 iterations occur.
        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); i++)
        {
            // Force finalisation of all object pending finalisation.
            RUNTIME.runFinalization();

            // Return unused memory to the heap.
            RUNTIME.gc();

            // Allow other threads to run.
            Thread.currentThread().yield();

            // Keep the old used memory count from the last iteration and get a fresh reading.
            usedMem2 = usedMem1;
            usedMem1 = usedMemory();
        }
    }

    /**
     * Calculates the total amount of memory used as total memory available minus the amount that is free.
     *
     * @return The total amount of memory used.
     */
    private static long usedMemory()
    {
        runGCTillStable();

        return RUNTIME.totalMemory() - RUNTIME.freeMemory();
    }
}
