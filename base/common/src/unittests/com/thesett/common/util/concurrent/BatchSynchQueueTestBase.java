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

import junit.framework.TestCase;

/**
 * BatchSynchQueueTestBase tests the synchronizing and synchronous error handling abilities of {@link BatchSynchQueue}
 * implementations, and their auxiliary interfaces {@link com.thesett.common.util.concurrent.SynchRef} and
 * {@link com.thesett.common.util.concurrent.SynchRecord}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that elements synchronously put into the queue, cause the putting thread to block.
 * <tr><td>Check that the putting threads for a batch of elements can all be released at once.
 * <tr><td>Check that the putting threads for elements can be released individually.
 * <tr><td>Check that a put call can return a SynchException on failed consume of an element.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BatchSynchQueueTestBase extends TestCase
{
    /** The number of test elements to put on the queue. */
    private static final int NUM_TEST_ELEMENTS = 10;

    /** The queue to test. */
    BatchSynchQueue testQueue;

    /**
     * Creates a test case with the named test.
     *
     * @param string        The name of the test to run.
     * @param testQueue     The batch synched queue to test.
     * @param maxQueueDepth If the queue
     */
    public BatchSynchQueueTestBase(String string, BatchSynchQueue testQueue, int maxQueueDepth)
    {
        super(string);

        this.testQueue = testQueue;
    }

    /** Check that elements synchronously put into the queue, cause the putting thread to block. */
    public void testSynchPutsBlockOk()
    {
    }

    /** Check that the putting threads for a batch of elements can all be released at once. */
    public void testReleaseAllBlockedProducersOk()
    {
    }

    /** Check that the putting threads for elements can be released individually. */
    public void testReleaseIndividualBlockedProducersOk()
    {
    }

    /** Check that a put call can return a SynchException on failed consume of an element. */
    public void testFailedConsumeCanPassSynchExceptionToProducer()
    {
    }

    /** Ensures the testQueue is empty for each test. */
    protected void setUp()
    {
        testQueue.clear();
    }
}
