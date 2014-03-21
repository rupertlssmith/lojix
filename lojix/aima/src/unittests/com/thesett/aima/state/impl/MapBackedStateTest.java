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
package com.thesett.aima.state.impl;

import com.thesett.aima.state.StateTestBase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MapBackedStateTest extends TestCase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(MapBackedStateTest.class.getName()); */

    /** Creates a new MapBackedStateTest object. */
    public MapBackedStateTest(String testName)
    {
        super(testName);
    }

    /**
     * Compile all the tests for the default test for test states into a test suite plus any tests defined in this test
     * class.
     */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("WrappedBeanState Tests");

        // Add all tests defined in the IndexTestBase class.
        suite.addTest(new StateTestBase("testGetPropertiesOk", new TestMapBackedState()));
        suite.addTest(new StateTestBase("testSetPropertiesOk", new TestMapBackedState()));

        return suite;
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
        NDC.push(getName());
    }

    /** @throws Exception */
    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
