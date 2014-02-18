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
package com.thesett.aima.state;

import junit.framework.TestCase;

import org.apache.log4j.NDC;

import com.thesett.aima.state.impl.TestExtendableBean;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that getting properties of all types works ok.
 * <tr><td> Check that getting properties that do not exist fails.
 * <tr><td> Check that getting properties for a method that throws an exception fails.
 * <tr><td> Check that setting properties of all types works ok.
 * <tr><td> Check that setting properties that do not exist fails.
 * <tr><td> Check that setting properties for a method that throws an exception fails.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StateTestBase extends TestCase
{
    /** Holds the state to test. */
    State testState;

    /**
     * Creates a test for a test state. The test state must be based on a bean (or map) that implements TestBean or at
     * least has all of the properties that TestBean has.
     *
     * @param s         The name of the test to run.
     * @param testState The state to test.
     */
    public StateTestBase(String s, State testState)
    {
        super(s);

        // Keep the state to test.
        this.testState = testState;
    }

    /** Check that getting properties of all types works ok. */
    public void testGetPropertiesOk() throws Exception
    {
        assertTrue(testState.getProperty("testBoolean").equals(TestBean.TEST_BOOLEAN));
        assertTrue(testState.getProperty("testCharacter").equals(TestBean.TEST_CHARACTER));
        assertTrue(testState.getProperty("testByte").equals(TestBean.TEST_BYTE));
        assertTrue(testState.getProperty("testShort").equals(TestBean.TEST_SHORT));
        assertTrue(testState.getProperty("testInteger").equals(TestBean.TEST_INTEGER));
        assertTrue(testState.getProperty("testLong").equals(TestBean.TEST_LONG));
        assertTrue(testState.getProperty("testFloat").equals(TestBean.TEST_FLOAT));
        assertTrue(testState.getProperty("testDouble").equals(TestBean.TEST_DOUBLE));
        assertTrue(testState.getProperty("testString").equals(TestBean.TEST_STRING));
        assertTrue(testState.getProperty("testObject").equals(TestBean.TEST_OBJECT));
    }

    /** Check that getting properties that do not exist fails. */
    public void testGetNonExistantPropertyFails() throws Exception
    {
        boolean testPassed = false;

        try
        {
            testState.getProperty("testNonExistant");
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Operation should have raised an exception.", testPassed);
    }

    /** Check that getting properties for a method that throws an exception fails. */
    public void testGetterMethodExceptionsCausesFailure() throws Exception
    {
        boolean testPassed = false;

        try
        {
            testState.getProperty("testStringException");
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Operation should have raised an exception.", testPassed);
    }

    /** Check that setting properties of all types works ok. */
    public void testSetPropertiesOk() throws Exception
    {
        testState.setProperty("testBoolean", TestBean.TEST_BOOLEAN);
        testState.setProperty("testCharacter", TestBean.TEST_CHARACTER);
        testState.setProperty("testByte", TestBean.TEST_BYTE);
        testState.setProperty("testShort", TestBean.TEST_SHORT);
        testState.setProperty("testInteger", TestBean.TEST_INTEGER);
        testState.setProperty("testLong", TestBean.TEST_LONG);
        testState.setProperty("testFloat", TestBean.TEST_FLOAT);
        testState.setProperty("testDouble", TestBean.TEST_DOUBLE);
        testState.setProperty("testString", TestBean.TEST_STRING);
        testState.setProperty("testObject", TestBean.TEST_OBJECT);
    }

    /** Check that setting properties that do not exist fails. */
    public void testSetNonExistantPropertiesFails() throws Exception
    {
        TestExtendableBean testState = new TestExtendableBean();

        boolean testPassed = false;

        try
        {
            testState.setProperty("testNonExistant", "");
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Operation should have raised an exception.", testPassed);
    }

    /** Check that setting properties for a method that throws an exception fails. */
    public void testSetterMethodExceptionCausesFailure() throws Exception
    {
        TestExtendableBean testState = new TestExtendableBean();

        boolean testPassed = false;

        try
        {
            testState.setProperty("testStringException", "");
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
}
