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
package com.thesett.aima.attribute.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.aima.state.InfiniteValuesException;

/**
 * Checks that {@link com.thesett.aima.attribute.impl.EnumeratedStringAttribute}s work as expected.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnumeratedStringAttributeTest extends TestCase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(EnumeratedStringAttributeTest.class.getName()); */

    /** A test hierarchy attribute factory. */
    EnumeratedStringAttribute.EnumeratedStringAttributeFactory factory;

    /** Creates a new StringAttributeTest object. */
    public EnumeratedStringAttributeTest(String testName)
    {
        super(testName);
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("EnumeratedStringAttribute Tests");

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(EnumeratedStringAttributeTest.class);

        return suite;
    }

    /** Check can generate hierarchy attribute from factory ok. */
    public void testFactoryWorksOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createStringAttribute("animal");
        factory.createStringAttribute("plant");
        factory.createStringAttribute("bacteria");
        factory.createStringAttribute("virus");
    }

    /** Check can generate hierarchy attributes from finalized factory ok. */
    public void testFinalizedFactoryWorksOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createStringAttribute("animal");

        // Finalize the factory. This is done twice to check that re-finalization is harmless.
        factory.finalizeAttribute();
        factory.finalizeAttribute();

        // Create a hierarchy attribute from the finalized attribute factory.
        EnumeratedStringAttribute s = factory.createStringAttribute("animal");

        // Check that it was created ok.
        assertNotNull("Valid attribute value created from finalized factory should not be null.", s);
    }

    /** Check generating new hierarchy attributes from finalized factory fails. */
    public void testFinalizedFactoryFailsOnNewValues() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createStringAttribute("animal");

        // Finalize the factory.
        factory.finalizeAttribute();

        // Try to create a hierarchy attribute that does not alread exist in the finalized factory.
        boolean testPassed = false;

        try
        {
            EnumeratedStringAttribute s = factory.createStringAttribute("plant");
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }
    }

    /** Check can convert attribute to int. */
    public void testConvertAttributeToInt() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes and convert them into bytes.
        int i1 = factory.createStringAttribute("animal").getByteFromAttribute();
        int i2 = factory.createStringAttribute("plant").getByteFromAttribute();
        int i3 = factory.createStringAttribute("bacteria").getByteFromAttribute();
        int i4 = factory.createStringAttribute("virus").getByteFromAttribute();
    }

    /** Check can convert attribute to int for finalized factory. */
    public void testConvertFinazliedAttributeToInt() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createStringAttribute("animal");
        factory.createStringAttribute("plant");
        factory.createStringAttribute("bacteria");
        factory.createStringAttribute("virus");

        // Finalize the factory.
        factory.finalizeAttribute();

        // Use the test factory to generate some hierarchy attributes and convert them into ints.
        int i1 = factory.createStringAttribute("animal").getByteFromAttribute();
        int i2 = factory.createStringAttribute("plant").getByteFromAttribute();
        int i3 = factory.createStringAttribute("bacteria").getByteFromAttribute();
        int i4 = factory.createStringAttribute("virus").getByteFromAttribute();
    }

    /** Check can convert from int to attribute. */
    public void testConvertIntToAttribute() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some hierarchy attributes and convert them into ints.
        int i1 = factory.createStringAttribute("animal").getByteFromAttribute();
        int i2 = factory.createStringAttribute("plant").getByteFromAttribute();
        int i3 = factory.createStringAttribute("bacteria").getByteFromAttribute();
        int i4 = factory.createStringAttribute("virus").getByteFromAttribute();

        // Now convert the bytes back into hierarchy attributes.
        EnumeratedStringAttribute h1 = factory.getAttributeFromByte((byte) i1);
        EnumeratedStringAttribute h2 = factory.getAttributeFromByte((byte) i2);
        EnumeratedStringAttribute h3 = factory.getAttributeFromByte((byte) i3);
        EnumeratedStringAttribute h4 = factory.getAttributeFromByte((byte) i4);

        // Check that the strings match the original values.
        String value1 = h1.getStringValue();

        if (!"animal".equals(value1))
        {
            errorMessage += "\"animal\" converted to int and then back again does not have correct value.\n";
        }

        String value2 = h2.getStringValue();

        if (!"plant".equals(value2))
        {
            errorMessage += "\"plant\" converted to int and then back again does not have correct value.\n";
        }

        String value3 = h3.getStringValue();

        if (!"bacteria".equals(value3))
        {
            errorMessage += "\"bacteria\" converted to int and then back again does not have correct value.\n";
        }

        String value4 = h4.getStringValue();

        if (!"virus".equals(value4))
        {
            errorMessage += "\"virus\" converted to int and then back again does not have correct value.\n";
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check converting unkown int to attribute fails. */
    public void testConvertingUnknownIntFails() throws Exception
    {
        // Get the only legal int value.
        int i1 = factory.createStringAttribute("animal").getByteFromAttribute();

        // Try to create a hierarchy attribute that does not alread exist in the factory.
        boolean testPassed = false;

        try
        {
            // Use the test factory to convert an unknown int back into a hierarchy attribute.
            EnumeratedStringAttribute unknown = factory.getAttributeFromByte((byte) (i1 + 1));
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that it returned null as the result
        assertTrue("An unkown int, " + (i1 + 1) +
            ", should have resulted in an IllegalArgumentException null attribute but did not.", testPassed);

        try
        {
            // Use the test factory to convert an unknown int back into a hierarchy attribute.
            EnumeratedStringAttribute unknown = factory.getAttributeFromByte((byte) (i1 - 1));
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that it returned null as the result
        assertTrue("An unkown int, " + (i1 - 1) +
            ", should have resulted in an IllegalArgumentException null attribute but did not.", testPassed);
    }

    /** Check converting unkown int to finalized attribute fails. */
    public void testConvertingUnkownIntFailsForFinalizedAttribute() throws Exception
    {
        // Finalize the factory
        factory.finalizeAttribute();

        // Try to create a hierarchy attribute that does not alread exist in the factory.
        boolean testPassed = false;

        try
        {
            // Use the test factory to convert an unknown int back into a hierarchy attribute.
            EnumeratedStringAttribute unknown = factory.getAttributeFromByte((byte) 1);
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that it returned null as the result
        assertTrue("An unkown int, 1, should have resulted in an IllegalArgumentException null attribute but did not.",
            testPassed);
    }

    /** Check unfinalized class reports infinite possible values. */
    public void testUnfinalizedHasInfinitePossibleValues() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");

        // Check that it reports an infinite number of possible values for the class.
        assertEquals("Unfinalized attribute class should report possible values as -1 (infinity).", -1,
            one.getType().getNumPossibleValues());
    }

    /** Check finalized class reports correct possible values. */
    public void testFinalizedHasCorrectPossibleValues() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        factory.createStringAttribute("plant");
        factory.createStringAttribute("bacteria");
        factory.createStringAttribute("virus");

        // Finalize the possible attributes at these three.
        factory.finalizeAttribute();

        // Check that the attributes now report the number of possible values as three.
        assertEquals("Finalized attribute class should report possible values as the number of values it takes, " +
            "in this case 4.", 4, one.getType().getNumPossibleValues());
    }

    /** Check hash code is equal for two identical attributes of the same class. */
    public void testHashCodeEqualForIdenticalAttributesOfSameClass() throws Exception
    {
        // Use the test factory to generate some identical hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        EnumeratedStringAttribute two = factory.createStringAttribute("animal");

        // Check their hash codes are equal.
        assertEquals("Identical attributes should have equal hash codes.", one.hashCode(), two.hashCode());
    }

    /** Check int representation is equal for two identical attributes of the same class. */
    public void testByteEqualForIdenticalAttributesOfSameClass() throws Exception
    {
        // Use the test factory to generate some identical hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        EnumeratedStringAttribute two = factory.createStringAttribute("animal");

        // Check their byte representations are the same.
        assertEquals("Identical attributes of the same class should have the same byte representations.",
            one.getByteFromAttribute(), two.getByteFromAttribute());
    }

    /** Check equality for identical attributes of the same class. */
    public void testIdentialAttributesOfSameClassEqual() throws Exception
    {
        // Use the test factory to generate some identical hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        EnumeratedStringAttribute two = factory.createStringAttribute("animal");

        // Check their equals method reckons them equal
        assertTrue("Identical attributes from the same class should be equal by the .equal() method.", one.equals(two));
    }

    /** Check inequality for identical attributes of different class. */
    public void testIdentialAttributesOfDifferentClassUnequal() throws Exception
    {
        // Get a factory for a different test class.
        EnumeratedStringAttribute.EnumeratedStringAttributeFactory factory2 =
            EnumeratedStringAttribute.getFactoryForClass("test2");

        // Use the test factories to generate some identical hierarchy attributes of different classes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        EnumeratedStringAttribute two = factory2.createStringAttribute("animal");

        // Check their equals method reckons them equal
        assertFalse("Identical attributes from the different classes should not be equal by the .equal() method.",
            one.equals(two));
    }

    /** Check inequality against an object of a different type. */
    public void testOtherObjectTypesUnequal() throws Exception
    {
        // Use the test factories to generate some identical hierarchy attributes of different classes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        Object two = new Object();

        // Check their equals method reckons them equal
        assertFalse("Objects of a different class should not be equal by the .equal() method.", one.equals(two));
    }

    /** Check inequality for non identical attributes of the same class. */
    public void testDifferentAttributesOfSameClassNotEqual() throws Exception
    {
        // Use the test factory to generate some different hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        EnumeratedStringAttribute two = factory.createStringAttribute("plant");

        // Check their equals method reckons them not equal
        assertTrue("Differnt attributes from the same class should be not be equal by the .equal() method.",
            !one.equals(two));
    }

    /** Check can get attribute ids ok. */
    public void testIdOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute h1 = factory.createStringAttribute("animal");
        EnumeratedStringAttribute h2 = factory.createStringAttribute("plant");
        EnumeratedStringAttribute h3 = factory.createStringAttribute("bacteria");
        EnumeratedStringAttribute h4 = factory.createStringAttribute("virus");

        // Get their ids.
        h1.getId();
        h2.getId();
        h3.getId();
        h4.getId();
    }

    /** Check that ids can be set on unfinalized attribute classes. */
    public void testSetIdOnUnfinalizedOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute h1 = factory.createStringAttribute("animal");

        // Set new ids on them.
        h1.setId(h1.getId());
        h1.setId(100L);

        assertEquals("Id was set to 100 but is " + h1.getId(), h1.getId(), 100L);
    }

    /** Check that setting id on unfinalized attribute class fails when another value already has the id. */
    public void testSetIdOnUnfinalizedFailsAlreadyInUse() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute h1 = factory.createStringAttribute("animal");
        EnumeratedStringAttribute h2 = factory.createStringAttribute("plant");

        // Set new ids on them that clash.
        h1.setId(100L);

        boolean testPassed = false;

        try
        {
            h2.setId(100L);
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Calling setId on 100 should have thrown IllegalArgumentException on h2.", testPassed);
    }

    /** Check that ids cannot be set to new values on finalized attribute classes. */
    public void testSetIdOnFinalizedFailsOnNewId() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute h1 = factory.createStringAttribute("animal");

        // Finalize the attribute class.
        factory.finalizeAttribute();

        // Try to set a new id.
        boolean testPassed = false;

        try
        {
            h1.setId(100L);
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Calling setId on 100 should have thrown IllegalArgumentException on finalized class.", testPassed);
    }

    /**
     * Check that setting id on finalized attribute class changes its value to that of attribute of which the id is set.
     */
    public void testSetIdOnFinalizedChangesValueOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute h1 = factory.createStringAttribute("animal");
        EnumeratedStringAttribute h2 = factory.createStringAttribute("plant");

        // Finalize the attribute class.
        factory.finalizeAttribute();

        // Uset setId to turn h2 into h1.
        long id1 = h1.getId();
        h2.setId(id1);

        assertTrue("h2 should have label \"animal\" after being transformed into h1 by setId.",
            "animal".equals(h2.getStringValue()));
    }

    /** Check that non-allowable values of finalized attribute class cannot be created. */
    public void testCannotCreateNonAllowable() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        EnumeratedStringAttribute h1 = factory.createStringAttribute("animal");

        // Finalize the attribute class.
        factory.finalizeAttribute();

        // Try to create an existing but non-allowable attribute.
        boolean testPassed = false;

        try
        {
            factory.createStringAttribute("all life");
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Should have raised IllegalArgumentException when trying to create non allowable value.",
            testPassed);
    }

    /** Check that getName returns the correct name for the attribute class. */
    public void testGetNameOk()
    {
        assertEquals("Test factory class name should be \"test\".", "test", factory.getType().getName());
    }

    /** Check that dropping an attribute class works. */
    public void testDroppingAttributeClassOk() throws Exception
    {
        factory.dropAttributeClass();
    }

    /** Check that listing all the values as a set of an unfinalized class fails with infinite values exception. */
    public void testListingAllValuesSetForUnfinalizedFails() throws Exception
    {
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");

        // Leave the attribute class for the test factory unfinalized.

        // Used to indicate that the test has passed.
        boolean testPassed = false;

        // Try to get a listing of all the possible attribute values.
        try
        {
            one.getType().getAllPossibleValuesSet();
        }
        catch (InfiniteValuesException e)
        {
            testPassed = true;
        }

        // Check that the expected exception was thrown and the test passed.
        assertTrue("An InfiniteValuesException should have been thrown because the test class was not finalized.",
            testPassed);
    }

    /** Check that listing all the values by iterator of an unfinalized class fails with infinite values exception. */
    public void testListingAllValuesIteratorForUnfinalizedFails() throws Exception
    {
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");

        // Leave the attribute class for the test factory unfinalized.

        // Used to indicate that the test has passed.
        boolean testPassed = false;

        // Try to get a listing of all the possible attribute values.
        try
        {
            one.getType().getAllPossibleValuesIterator();
        }
        catch (InfiniteValuesException e)
        {
            testPassed = true;
        }

        // Check that the expected exception was thrown and the test passed.
        assertTrue("An InfiniteValuesException should have been thrown because the test class was not finalized.",
            testPassed);
    }

    /** Check that listing all the values as a set of a finalized class returns all values correctly. */
    public void testListingAllValuesSetReallyListsAll() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some different hierarchy attributes.
        EnumeratedStringAttribute lastCreated = null;

        for (int i = 0; i < 10; i++)
        {
            lastCreated = factory.createStringAttribute(Integer.toString(i));
        }

        // Finalize the test class.
        factory.finalizeAttribute();

        // Try to get a listing of all the possible attribute values.
        Set<EnumeratedStringAttribute> allValues = lastCreated.getType().getAllPossibleValuesSet();

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<EnumeratedStringAttribute> testValues = new HashSet<EnumeratedStringAttribute>();

        for (int j = 0; j < 10; j++)
        {
            testValues.add(factory.createStringAttribute(Integer.toString(j)));
        }

        int sizeBefore = allValues.size();

        allValues.retainAll(testValues);

        if (allValues.size() != sizeBefore)
        {
            errorMessage +=
                "There are values listed as possible values of the hierarchy attribute class that were not " +
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

    /** Check that listing all the values by iterator of a finalized class returns all values correctly. */
    public void testListingAllValuesIteratorReallyListsAll() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some different hierarchy attributes.
        EnumeratedStringAttribute lastCreated = null;

        for (int i = 0; i < 10; i++)
        {
            lastCreated = factory.createStringAttribute(Integer.toString(i));
        }

        // Finalize the test class.
        factory.finalizeAttribute();

        // Try to get a listing of all the possible attribute values.
        Set<EnumeratedStringAttribute> allValues = new HashSet<EnumeratedStringAttribute>();

        for (Iterator<EnumeratedStringAttribute> allValuesIterator =
                    lastCreated.getType().getAllPossibleValuesIterator(); allValuesIterator.hasNext();)
        {
            allValues.add(allValuesIterator.next());
        }

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<EnumeratedStringAttribute> testValues = new HashSet<EnumeratedStringAttribute>();

        for (int j = 0; j < 10; j++)
        {
            testValues.add(factory.createStringAttribute(Integer.toString(j)));
        }

        int sizeBefore = allValues.size();

        allValues.retainAll(testValues);

        if (allValues.size() != sizeBefore)
        {
            errorMessage +=
                "There are values listed as possible values of the hierarchy attribute class that were not " +
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
        // Use the test factory to generate some identical hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        EnumeratedStringAttribute two = factory.createStringAttribute("animal");

        // Check their ordinal representations are the same.
        assertTrue("Different attributes of the same class should have differnt ordinals.",
            one.ordinal() == two.ordinal());
    }

    /** Check that different attributes have different ordinals. */
    public void testDifferentAttributesHaveDifferentOrdinal() throws Exception
    {
        // Use the test factory to generate some differnt hierarchy attributes.
        EnumeratedStringAttribute one = factory.createStringAttribute("animal");
        EnumeratedStringAttribute two = factory.createStringAttribute("plant");

        // Check their ordinal representations are not the same.
        assertFalse("Different attributes of the same class should have different ordinals.",
            one.ordinal() == two.ordinal());
    }

    /** Ensures that the attribute class 'test' is clean for each test. */
    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Explicitly drop the test class to ensure that it is clean.
        factory = EnumeratedStringAttribute.getFactoryForClass("test");
        factory.dropAttributeClass();

        // Create the test factory for the test class.
        factory = EnumeratedStringAttribute.getFactoryForClass("test");
    }

    /** Ensures that the attribute class 'test' is clean for each test. */
    protected void tearDown() throws Exception
    {
        NDC.pop();

        // Explicitly drop the test class.
        factory.dropAttributeClass();
    }
}
