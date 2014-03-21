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
 * Tests the {@link ExtendableBeanState} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Enforce all test in StateTestBase <th> {@link StateTestBase}
 * <tr><td> Check that getting properties for non-public methods fails.
 * <tr><td> Check that setting properties for non-public methods fails.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ExtendableBeanStateTest extends TestCase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(ExtendableBeanStateTest.class.getName()); */

    /** Creates a new ExtendableBeanStateTest object. */
    public ExtendableBeanStateTest(String testName)
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
        TestSuite suite = new TestSuite("ExtendableBeanState Tests");

        // Add all tests defined in this class.
        suite.addTestSuite(ExtendableBeanStateTest.class);

        // Add all tests defined in the IndexTestBase class.
        suite.addTest(new StateTestBase("testGetPropertiesOk", new TestExtendableBean()));
        suite.addTest(new StateTestBase("testGetNonExistantPropertyFails", new TestExtendableBean()));
        suite.addTest(new StateTestBase("testGetterMethodExceptionsCausesFailure", new TestExtendableBean()));
        suite.addTest(new StateTestBase("testSetPropertiesOk", new TestExtendableBean()));
        suite.addTest(new StateTestBase("testSetNonExistantPropertiesFails", new TestExtendableBean()));
        suite.addTest(new StateTestBase("testSetterMethodExceptionCausesFailure", new TestExtendableBean()));

        return suite;
    }

    /** Check that getting properties for non-public methods fails. */
    public void testGetNonPublicPropertyFails() throws Exception
    {
        TestExtendableBeanWithPrivateFields testBean = new TestExtendableBeanWithPrivateFields();

        boolean testPassed = false;

        try
        {
            testBean.getProperty("testStringPrivate");
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Operation should have raised an exception.", testPassed);
    }

    /** Check that setting properties for non-public methods fails. */
    public void testSetNonPublicPropertyFails() throws Exception
    {
        TestExtendableBeanWithPrivateFields testBean = new TestExtendableBeanWithPrivateFields();

        boolean testPassed = false;

        try
        {
            testBean.setProperty("testStringPrivate", "");
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Operation should have raised an exception.", testPassed);
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

    /**
     * Extends the test bean implementation with some private methods so that failure to call private methods can be
     * tested for.
     */
    public class TestExtendableBeanWithPrivateFields extends TestExtendableBean
    {
        private String getStringPrivate()
        {
            return "private";
        }

        private void setStringPrivate(String test)
        {
        }
    }
}
