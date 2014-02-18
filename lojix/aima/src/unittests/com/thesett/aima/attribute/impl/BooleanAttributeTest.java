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
package com.thesett.aima.attribute.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.apache.log4j.NDC;

/**
 * BooleanAttributeTest is a pure unit test class for the {@link BooleanAttribute} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BooleanAttributeTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(BooleanAttributeTest.class.getName()); */

    /** Creates a new BooleanAttributeTest object. */
    public BooleanAttributeTest(String testName)
    {
        super(testName);
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("BooleanAttribute Tests");

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(BooleanAttributeTest.class);

        return suite;
    }

    /** Check can generate string attribute from factory ok. */
    public void testCreateBooleanAttributes() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);
    }

    /** Check converting to underlying boolean value is correct. */
    public void testBooleanValuesAreCorrect() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);

        // Check that the reported boolean values are correct.
        assertTrue("BooleanAttribute ttrue should have value true.", ttrue.booleanValue());
        assertTrue("BooleanAttribute ffalse should have value true.", !ffalse.booleanValue());
    }

    /** Check class reports correct possible values. */
    public void testHasCorrectPossibleValues() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);

        // Check that the attribute reports the number of possible values as two.
        assertEquals("Test boolean attribute class should report the number of values can have as 2.",
            ttrue.getType().getNumPossibleValues(), 2);
    }

    /** Check hash code is equal for two identical attributes of the same class. */
    public void testHashCodeEqualForIdenticalAttributesOfSameClass() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ttrue2 = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);
        BooleanAttribute ffalse2 = new BooleanAttribute(false);

        // Check their hash codes are equal.
        assertEquals("Identical attributes should have equal hash codes.", ttrue.hashCode(), ttrue2.hashCode());
        assertEquals("Identical attributes should have equal hash codes.", ffalse.hashCode(), ffalse2.hashCode());
    }

    /** Check equality for identical attributes of the same class. */
    public void testIdentialAttributesOfSameClassEqual() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ttrue2 = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);
        BooleanAttribute ffalse2 = new BooleanAttribute(false);

        // Check their equals method reckons them equal
        assertTrue("Identical attributes from the same class should be equal by the .equal() method.",
            ttrue.equals(ttrue2));
        assertTrue("Identical attributes from the same class should be equal by the .equal() method.",
            ffalse.equals(ffalse2));
    }

    /** Check inequality for different attributes of the same class. */
    public void testDifferentAttributesOfSameClassNotEqual() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);

        // Check their equals method reckons them not equal
        assertTrue("Differnt attributes from the same class should be not be equal by the .equal() method.",
            !ttrue.equals(ffalse));
    }

    /** Check that listing all the values as a set returns all values correctly. */
    public void testListingAllValuesSetReallyListsAll() throws Exception
    {
        String errorMessage = "";

        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);

        // Try to get a listing of all the possible attribute values.
        Set<BooleanAttribute> allValues = ttrue.getType().getAllPossibleValuesSet();

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<BooleanAttribute> testValues = new HashSet<BooleanAttribute>();

        testValues.add(ttrue);
        testValues.add(ffalse);

        int sizeBefore = allValues.size();

        if (sizeBefore != 2)
        {
            errorMessage += "There should be 2 values in the set of all values.\n";
        }

        allValues.retainAll(testValues);

        if (allValues.size() != sizeBefore)
        {
            errorMessage +=
                "There are values listed as possible values of the string attribute class that were not " +
                "created before it was finalized.\n";
        }

        allValues.removeAll(testValues);

        if (allValues.size() != 0)
        {
            errorMessage +=
                "There are values created before the string class was finalized that were not listed as " +
                "possible values of the class.\n";
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that listing all the values by iterator returns all values correctly. */
    public void testListingAllValuesIteratorReallyListsAll() throws Exception
    {
        String errorMessage = "";

        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);

        // Try to get a listing of all the possible attribute values.
        Set<BooleanAttribute> allValues = new HashSet<BooleanAttribute>();

        for (Iterator<BooleanAttribute> allValuesIterator = ttrue.getType().getAllPossibleValuesIterator();
                allValuesIterator.hasNext();)
        {
            allValues.add(allValuesIterator.next());
        }

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<BooleanAttribute> testValues = new HashSet<BooleanAttribute>();

        testValues.add(ttrue);
        testValues.add(ffalse);

        int sizeBefore = allValues.size();

        if (sizeBefore != 2)
        {
            errorMessage +=
                "There should be 2 values in the set of all values but there is actually " + sizeBefore + ".\n";
        }

        allValues.retainAll(testValues);

        if (allValues.size() != sizeBefore)
        {
            errorMessage +=
                "There are values listed as possible values of the string attribute class that were not " +
                "created before it was finalized.\n";
        }

        allValues.removeAll(testValues);

        if (allValues.size() != 0)
        {
            errorMessage +=
                "There are values created before the string class was finalized that were not listed as " +
                "possible values of the class.\n";
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that identical attributes have the same ordinal. */
    public void testIdenticalAttributesHaveSameOrdinal() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ttrue2 = new BooleanAttribute(true);

        // Check their ordinal representations are the same.
        assertTrue("Different attributes of the same class should have differnt ordinals.",
            ttrue.ordinal() == ttrue2.ordinal());
    }

    /** Check that different attributes have different ordinals. */
    public void testDifferentAttributesHaveDifferentOrdinal() throws Exception
    {
        BooleanAttribute ttrue = new BooleanAttribute(true);
        BooleanAttribute ffalse = new BooleanAttribute(false);

        // Check their ordinal representations are not the same.
        assertFalse("Different attributes of the same class should have differnt ordinals.",
            ttrue.ordinal() == ffalse.ordinal());
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
