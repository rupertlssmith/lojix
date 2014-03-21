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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

/**
 * EnumAttributeTest is a pure unit test class for the {@link EnumAttribute} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check can generate string attribute from factory ok.
 * <tr><td> Check can generate string attributes from finalized factory ok.
 * <tr><td> Check generating new string attributes from finalized factory ok fails.
 * <tr><td> Check can convert attribute to byte.
 * <tr><td> Check can convert attribute to byte for finalized factory.
 * <tr><td> Check can convert from byte to attribute.
 * <tr><td> Check converting unkown byte to attribute fails.
 * <tr><td> Check converting unkown byte to finalized attribute fails.
 * <tr><td> Check converting to string value is correct.
 * <tr><td> Check converting to string value is correct for finalized attribute.
 * <tr><td> Check unfinalized class reports infinite possible values.
 * <tr><td> Check finalized class reports correct possible values.
 * <tr><td> Check hash code is equal for two identical attributes of the same class.
 * <tr><td> Check hash code is equal for two identical attributes of different class.
 * <tr><td> Check byte representation is equal for two identical attributes of the same class.
 * <tr><td> Check equality for identical attributes of the same class.
 * <tr><td> Check equality for identical attributes of different class.
 * <tr><td> Check inequality for identical attributes of the same class.
 * <tr><td> Check inequality for identical attributes of different class.
 * <tr><td> Check that dropping an attribute class works.
 * <tr><td> Check that listing all the values as a set of an unfinalized class fails with infinite values exception.
 * <tr><td> Check that listing all the values by iterator of an unfinalized class fails with infinite values exception.
 * <tr><td> Check that listing all the values as a set of a finalized class returns all values correctly.
 * <tr><td> Check that listing all the values by iterator of a finalized class returns all values correctly.
 * <tr><td> Check that identical attributes have the same ordinal.
 * <tr><td> Check that different attributes have different ordinals.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnumAttributeTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(EnumAttributeTest.class.getName()); */

    /** A test enum to use. */
    enum Numbers
    {
        One, Two, Three
    }

    /** A test string attribute factory. */
    EnumAttribute.EnumAttributeFactory factory;

    /** Creates a new EnumAttributeTest object. */
    public EnumAttributeTest(String testName)
    {
        super(testName);
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("EnumAttribute Tests");

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(EnumAttributeTest.class);

        return suite;
    }

    /** Check can generate string attribute from factory ok. */
    public void testFactoryWorksOk() throws Exception
    {
        factory = EnumAttribute.getFactoryForClass(Numbers.class);

        // Use the test factory to generate some string attributes.
        factory.createEnumAttribute(Numbers.One);
        factory.createEnumAttribute(Numbers.Two);
        factory.createEnumAttribute(Numbers.Three);
    }

    /** Check converting to underlying enum value is correct. */
    public void testEnumValueIsCorrect() throws Exception
    {
        // Use the test factory to generate some string attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);

        // Check that the reported enum value is correct.
        assertTrue("EnumAttribute one should have enum value named \"One\" the is equals to Numbers.One",
            one.getEnumValue().equals(Numbers.One));
    }

    /** Check class reports correct possible values. */
    public void testHasCorrectPossibleValues() throws Exception
    {
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);

        // Check that the attribute reports the number of possible values as three.
        Assert.assertEquals(
            "Test enum attribute class should report possible values as the number of values it takes, " +
            "in this case 3.", one.getType().getNumPossibleValues(), 3);
    }

    /** Check hash code is equal for two identical attributes of the same class. */
    public void testHashCodeEqualForIdenticalAttributesOfSameClass() throws Exception
    {
        // Use the test factory to generate some identical string attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);
        EnumAttribute two = factory.createEnumAttribute(Numbers.One);

        // Check their hash codes are equal.
        assertEquals("Identical attributes should have equal hash codes.", one.hashCode(), two.hashCode());
    }

    /** Check equality for identical attributes of the same class. */
    public void testIdentialAttributesOfSameClassEqual() throws Exception
    {
        // Use the test factory to generate some identical string attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);
        EnumAttribute two = factory.createEnumAttribute(Numbers.One);

        // Check their equals method reckons them equal
        assertTrue("Identical attributes from the same class should be equal by the .equal() method.", one.equals(two));
    }

    /** Check inequality for different attributes of the same class. */
    public void testDifferentAttributesOfSameClassNotEqual() throws Exception
    {
        // Use the test factory to generate some differnt string attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);
        EnumAttribute two = factory.createEnumAttribute(Numbers.Two);

        // Check their equals method reckons them not equal
        assertTrue("Differnt attributes from the same class should be not be equal by the .equal() method.",
            !one.equals(two));
    }

    /** Check that dropping an attribute class works. */
    public void testDroppingAttributeClassOk() throws Exception
    {
        factory.dropAttributeClass();
    }

    /** Check that listing all the values as a set returns all values correctly. */
    public void testListingAllValuesSetReallyListsAll() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some different enum attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);
        EnumAttribute two = factory.createEnumAttribute(Numbers.Two);
        EnumAttribute three = factory.createEnumAttribute(Numbers.Three);

        // Try to get a listing of all the possible attribute values.
        Set<EnumAttribute> allValues = one.getType().getAllPossibleValuesSet();

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<EnumAttribute> testValues = new HashSet<EnumAttribute>();

        testValues.add(one);
        testValues.add(two);
        testValues.add(three);

        int sizeBefore = allValues.size();

        if (sizeBefore != 3)
        {
            errorMessage += "There should be 3 values in the set of all values.\n";
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

        // Use the test factory to generate some different string attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);
        EnumAttribute two = factory.createEnumAttribute(Numbers.Two);
        EnumAttribute three = factory.createEnumAttribute(Numbers.Three);

        // Try to get a listing of all the possible attribute values.
        Set<EnumAttribute> allValues = new HashSet<EnumAttribute>();

        for (Iterator<EnumAttribute> allValuesIterator = one.getType().getAllPossibleValuesIterator();
                allValuesIterator.hasNext();)
        {
            allValues.add(allValuesIterator.next());
        }

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<EnumAttribute> testValues = new HashSet<EnumAttribute>();

        testValues.add(one);
        testValues.add(two);
        testValues.add(three);

        int sizeBefore = allValues.size();

        if (sizeBefore != 3)
        {
            errorMessage += "There should be 3 values in the set of all values.\n";
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
        // Use the test factory to generate some identical string attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);
        EnumAttribute two = factory.createEnumAttribute(Numbers.One);

        // Check their ordinal representations are the same.
        assertTrue("Different attributes of the same class should have differnt ordinals.",
            one.ordinal() == two.ordinal());
    }

    /** Check that different attributes have different ordinals. */
    public void testDifferentAttributesHaveDifferentOrdinal() throws Exception
    {
        // Use the test factory to generate some differnt string attributes.
        EnumAttribute one = factory.createEnumAttribute(Numbers.One);
        EnumAttribute two = factory.createEnumAttribute(Numbers.Two);

        // Check their ordinal representations are not the same.
        assertFalse("Different attributes of the same class should have differnt ordinals.",
            one.ordinal() == two.ordinal());
    }

    /** Check that trying to create an enum attribute factory for a non-enum class fails. */
    public void testCreateEnumFactoryForNonEnumFails() throws Exception
    {
        // Used to indicate that the test has passed.
        boolean testPassed = false;

        // Try to get a non-enum factory.
        try
        {
            EnumAttribute.getFactoryForClass(Object.class);
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that the expected exception was thrown and the test passed.
        assertTrue("An IllegalArgumentException should have been thrown because enum attributes can only be created " +
            "for enum classes.", testPassed);
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Create the test factory for the test class.
        factory = EnumAttribute.getFactoryForClass(Numbers.class);
    }

    /** @throws Exception */
    protected void tearDown() throws Exception
    {
        NDC.pop();

        // Explicitly drop the test class.
        factory.dropAttributeClass();
    }
}
