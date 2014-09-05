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
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.aima.state.InfiniteValuesException;

/**
 * Checks that {@link HierarchyAttribute}s work as expected.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * Check that roots of the hierarchy are created correctly.
 * <tr><th> Check can generate hierarchy attribute from factory ok.
 * <tr><td> Check can generate hierarchy attributes from finalized factory ok.
 * <tr><td> Check generating new hierarchy attributes from finalized factory fails.
 * <tr><td> Check can convert attribute to int.
 * <tr><td> Check can convert attribute to int for finalized factory.
 * <tr><td> Check can convert from int to attribute.
 * <tr><td> Check converting unkown int to attribute fails.
 * <tr><td> Check converting unkown int to finalized attribute fails.
 * <tr><td> Check converting to string path value is correct.
 * <tr><td> Check converting to path value is correct for finalized attribute.
 * <tr><td> Check unfinalized class reports infinite possible values.
 * <tr><td> Check finalized class reports correct possible values.
 * <tr><td> Check hash code is equal for two identical attributes of the same class.
 * <tr><td> Check int representation is equal for two identical attributes of the same class.
 * <tr><td> Check equality for identical attributes of the same class.
 * <tr><td> Check inequality for identical attributes of different class.
 * <tr><td> Check inequality against an object of a diffferent type.
 * <tr><td> Check inequality for non identical attributes of the same class.
 * <tr><td> Check that sub-category checking is true for sub-categories of the same type.
 * <tr><td> Check that sub-category checking is false for non sub-categories of the same type.
 * <tr><td> Check that sub-category checking is false for non sub-categories of the same type.
 * <tr><td> Check that sub-category checking does not work for otherwise legal sub-categories of different types.
 * <tr><td> Check that ids can be set on unfinalized attribute classes.
 * <tr><td> Check can get attribute ids ok.
 * <tr><td> Check that setting id on unfinalized attribute class fails when another value already has the id.
 * <tr><td> Check that ids cannot be set to new values on finalized attribute classes.
 * <tr><td> Check that setting id on finalized attribute class changes its value to that of attribute of which the id is set.
 * <tr><td> Check that getValueAtLevel returns the correct results.
 * <tr><td> Check that getMaxLevels returns the correct results.
 * <tr><td> Check that non-allowable values of finalized attribute class cannot be created.
 * <tr><td> Check that getName returns the correct name for the attribute class.
 * <tr><td> Check that dropping an attribute class works.
 * <tr><td> Check that listing all the values as a set of an unfinalized class fails with infinite values exception.
 * <tr><td> Check that listing all the values by iterator of an unfinalized class fails with infinite values exception.
 * <tr><td> Check that listing all the values as a set of a finalized class returns all values correctly.
 * <tr><td> Check that listing all the values by iterator of a finalized class returns all values correctly.
 * <tr><td> Check that identical attributes have the same ordinal.
 * <tr><td> Check that different attributes have different ordinals.
 * <tr><td> Check that a hierarchy level can be listed.
 * <tr><td> Check that a sub hierarchy can be listed.
 * <tr><td> Check that listing the complete hierarchy path from root to tip works ok.
 * <tr><td> Check that getting the last label value works ok.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class HierarchyAttributeTest extends TestCase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(HierarchyAttributeTest.class.getName()); */

    /** A test hierarchy attribute factory. */
    HierarchyAttributeFactory factory;

    /** Creates a new StringAttributeTest object. */
    public HierarchyAttributeTest(String testName)
    {
        super(testName);
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("HierarchyAttribute Tests");

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(HierarchyAttributeTest.class);

        return suite;
    }

    /** Check that roots of the hierarchy are created correctly. */
    public void testRootsCreatedCorrectly() throws Exception
    {
        HierarchyAttribute firstRoot = factory.createHierarchyAttribute(new String[] { "first_root" });
        HierarchyAttribute secondRoot = factory.createHierarchyAttribute(new String[] { "second_root" });

        List<String> rootPath = firstRoot.getPathValue();

        assertEquals("The firstRoot does not have the correct path length.", 1, rootPath.size());
        assertEquals("The firstRoot does not have the correct label.", "first_root", rootPath.get(0));

        List<String> secondRootPath = secondRoot.getPathValue();

        assertEquals("The secondRoot does not have the correct path length.", 1, secondRootPath.size());
        assertEquals("The secondRoot does not have the correct label.", "second_root", secondRootPath.get(0));
    }

    /** Check can generate hierarchy attribute from factory ok. */
    public void testFactoryWorksOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        factory.createHierarchyAttribute(new String[] { "all life", "plant" });
        factory.createHierarchyAttribute(new String[] { "all life", "bacteria" });
        factory.createHierarchyAttribute(new String[] { "all life", "virus" });
    }

    /** Check can generate hierarchy attributes from finalized factory ok. */
    public void testFinalizedFactoryWorksOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Finalize the factory. This is done twice to check that re-finalization is harmless.
        factory.finalizeAttribute();
        factory.finalizeAttribute();

        // Create a hierarchy attribute from the finalized attribute factory.
        HierarchyAttribute s = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Check that it was created ok.
        assertNotNull("Valid attribute value created from finalized factory should not be null.", s);
    }

    /** Check generating new hierarchy attributes from finalized factory fails. */
    public void testFinalizedFactoryFailsOnNewValues() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Finalize the factory.
        factory.finalizeAttribute();

        // Try to create a hierarchy attribute that does not alread exist in the finalized factory.
        boolean testPassed = false;

        try
        {
            HierarchyAttribute s = factory.createHierarchyAttribute(new String[] { "all life", "plant" });
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that an illegal argument exception was thrown.
        assertTrue(
            "Invalid attribute value created from finalized factory should have thrown IllegalArgumentException.",
            testPassed);

        testPassed = false;

        try
        {
            HierarchyAttribute s = factory.createHierarchyAttribute(new String[] { "all life", "animal", "mamal" });
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that an illegal argument exception was thrown.
        assertTrue(
            "Invalid attribute value created from finalized factory should have thrown IllegalArgumentException.",
            testPassed);

        testPassed = false;

        try
        {
            HierarchyAttribute s = factory.createHierarchyAttribute(new String[] { "all life" });
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that an illegal argument exception was thrown.
        assertTrue(
            "Invalid attribute value created from finalized factory should have thrown IllegalArgumentException.",
            testPassed);
    }

    /** Check can convert attribute to int. */
    public void testConvertAttributeToInt() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes and convert them into bytes.
        int i1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" }).getIntFromAttribute();
        int i2 = factory.createHierarchyAttribute(new String[] { "all life", "plant" }).getIntFromAttribute();
        int i3 = factory.createHierarchyAttribute(new String[] { "all life", "bacteria" }).getIntFromAttribute();
        int i4 = factory.createHierarchyAttribute(new String[] { "all life", "virus" }).getIntFromAttribute();
    }

    /** Check can convert attribute to int for finalized factory. */
    public void testConvertFinazliedAttributeToInt() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        factory.createHierarchyAttribute(new String[] { "all life", "plant" });
        factory.createHierarchyAttribute(new String[] { "all life", "bacteria" });
        factory.createHierarchyAttribute(new String[] { "all life", "virus" });

        // Finalize the factory.
        factory.finalizeAttribute();

        // Use the test factory to generate some hierarchy attributes and convert them into ints.
        int i1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" }).getIntFromAttribute();
        int i2 = factory.createHierarchyAttribute(new String[] { "all life", "plant" }).getIntFromAttribute();
        int i3 = factory.createHierarchyAttribute(new String[] { "all life", "bacteria" }).getIntFromAttribute();
        int i4 = factory.createHierarchyAttribute(new String[] { "all life", "virus" }).getIntFromAttribute();
    }

    /** Check can convert from int to attribute. */
    public void testConvertIntToAttribute() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some hierarchy attributes and convert them into ints.
        int i1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" }).getIntFromAttribute();
        int i2 = factory.createHierarchyAttribute(new String[] { "all life", "plant" }).getIntFromAttribute();
        int i3 = factory.createHierarchyAttribute(new String[] { "all life", "bacteria" }).getIntFromAttribute();
        int i4 = factory.createHierarchyAttribute(new String[] { "all life", "virus" }).getIntFromAttribute();

        // Now convert the bytes back into hierarchy attributes.
        HierarchyAttribute h1 = factory.getAttributeFromInt(i1);
        HierarchyAttribute h2 = factory.getAttributeFromInt(i2);
        HierarchyAttribute h3 = factory.getAttributeFromInt(i3);
        HierarchyAttribute h4 = factory.getAttributeFromInt(i4);

        // Check that the strings match the original values.
        String[] pathValue1 = h1.getPathValue().toArray(new String[0]);

        if (!"all life".equals(pathValue1[0]) || !"animal".equals(pathValue1[1]))
        {
            errorMessage +=
                "\"all life\", \"animal\" converted to int and then back again does not have correct value.\n";
        }

        String[] pathValue2 = h2.getPathValue().toArray(new String[0]);

        if (!"all life".equals(pathValue2[0]) || !"plant".equals(pathValue2[1]))
        {
            errorMessage +=
                "\"all life\", \"plant\" converted to int and then back again does not have correct value.\n";
        }

        String[] pathValue3 = h3.getPathValue().toArray(new String[0]);

        if (!"all life".equals(pathValue3[0]) || !"bacteria".equals(pathValue3[1]))
        {
            errorMessage +=
                "\"all life\", \"bacteria\" converted to int and then back again does not have correct value.\n";
        }

        String[] pathValue4 = h4.getPathValue().toArray(new String[0]);

        if (!"all life".equals(pathValue4[0]) || !"virus".equals(pathValue4[1]))
        {
            errorMessage +=
                "\"all life\", \"virus\" converted to int and then back again does not have correct value.\n";
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check converting unkown int to attribute fails. */
    public void testConvertingUnknownIntFails() throws Exception
    {
        // Get the only legal int value.
        int i1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" }).getIntFromAttribute();

        // Try to create a hierarchy attribute that does not alread exist in the factory.
        boolean testPassed = false;

        try
        {
            // Use the test factory to convert an unknown int back into a hierarchy attribute.
            HierarchyAttribute unknown = factory.getAttributeFromInt(i1 + 1);
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
            HierarchyAttribute unknown = factory.getAttributeFromInt(i1 - 1);
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that it returned null as the result
        assertTrue("An unkown int, " + (i1 - 1) +
            "1, should have resulted in an IllegalArgumentException null attribute but did not.", testPassed);
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
            HierarchyAttribute unknown = factory.getAttributeFromInt(1);
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        // Check that it returned null as the result
        assertTrue("An unkown int, 1, should have resulted in an IllegalArgumentException null attribute but did not.",
            testPassed);
    }

    /** Check converting to string path value is correct. */
    public void testPathValueIsCorrect() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        String[] pathValue = one.getPathValue().toArray(new String[0]);

        // Check that the reported value is correct.
        assertTrue("HierarchyAttribute one should have value \"all life\", \"animal\".",
            "all life".equals(pathValue[0]) && "animal".equals(pathValue[1]));
    }

    /** Check converting to path value is correct for finalized attribute. */
    public void testPathValueIsCorrectForFinalizedAttribute() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Finalize the factory
        factory.finalizeAttribute();

        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        String[] pathValue = one.getPathValue().toArray(new String[0]);

        // Check that the reported value is correct.
        assertTrue("HierarchyAttribute one should have string value \"all life\", \"animal\".",
            "all life".equals(pathValue[0]) && "animal".equals(pathValue[1]));
    }

    /** Check unfinalized class reports infinite possible values. */
    public void testUnfinalizedHasInfinitePossibleValues() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Check that it reports an infinite number of possible values for the class.
        assertEquals("Unfinalized attribute class should report possible values as -1 (infinity).", -1,
            one.getType().getNumPossibleValues());
    }

    /** Check finalized class reports correct possible values. */
    public void testFinalizedHasCorrectPossibleValues() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        factory.createHierarchyAttribute(new String[] { "all life", "plant" });
        factory.createHierarchyAttribute(new String[] { "all life", "bacteria" });
        factory.createHierarchyAttribute(new String[] { "all life", "virus" });

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
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Check their hash codes are equal.
        assertEquals("Identical attributes should have equal hash codes.", one.hashCode(), two.hashCode());
    }

    /** Check int representation is equal for two identical attributes of the same class. */
    public void testByteEqualForIdenticalAttributesOfSameClass() throws Exception
    {
        // Use the test factory to generate some identical hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Check their byte representations are the same.
        assertEquals("Identical attributes of the same class should have the same byte representations.",
            one.getIntFromAttribute(), two.getIntFromAttribute());
    }

    /** Check equality for identical attributes of the same class. */
    public void testIdentialAttributesOfSameClassEqual() throws Exception
    {
        // Use the test factory to generate some identical hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Check their equals method reckons them equal
        assertTrue("Identical attributes from the same class should be equal by the .equal() method.", one.equals(two));
    }

    /** Check inequality for identical attributes of different class. */
    public void testIdentialAttributesOfDifferentClassUnequal() throws Exception
    {
        // Get a factory for a different test class.
        HierarchyAttributeFactory factory2 = HierarchyAttribute.getFactoryForClass("test2");

        // Use the test factories to generate some identical hierarchy attributes of different classes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory2.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Check their equals method reckons them equal
        assertFalse("Identical attributes from the different classes should not be equal by the .equal() method.",
            one.equals(two));
    }

    /** Check inequality against an object of a diffferent type. */
    public void testOtherObjectTypesUnequal() throws Exception
    {
        // Use the test factories to generate some identical hierarchy attributes of different classes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        Object two = new Object();

        // Check their equals method reckons them equal
        assertFalse("Objects of a different class should not be equal by the .equal() method.", one.equals(two));
    }

    /** Check inequality for non identical attributes of the same class. */
    public void testDifferentAttributesOfSameClassNotEqual() throws Exception
    {
        // Use the test factory to generate some different hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "plant" });

        // Check their equals method reckons them not equal
        assertTrue("Differnt attributes from the same class should be not be equal by the .equal() method.",
            !one.equals(two));
    }

    /** Check that sub-category checking is true for sub-categories of the same type. */
    public void testSubCategoryTrueForSubCategories() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life" });

        // Check that the sub-category is a sub-category of the parent one.
        assertTrue("Sub-category \"all life\", \"animal\" should be a sub-category of \"all life\".",
            two.isEqualOrSubCategory(one));

        assertTrue("Sub-category \"all life\", \"animal\" should be a sub-category of \"all life\", \"animal\".",
            one.isEqualOrSubCategory(one));

        assertTrue("Sub-category \"all life\" should be a sub-category of \"all life\".",
            two.isEqualOrSubCategory(two));
    }

    /** Check that sub-category checking is false for non sub-categories of the same type. */
    public void testEqualsOrSubCategoryFalseForNonEqualsOrSubCategories() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life" });
        HierarchyAttribute three = factory.createHierarchyAttribute(new String[] { "all life", "plant" });

        // Check that the parent is equal or a sub-cateogy of itself.
        assertTrue("Category \"all life\" should be equal or a sub-category of \"all life\".",
            one.isEqualOrSubCategory(one));

        // Check that the sub-category is a sub-category of the parent one.
        assertFalse("Category \"all life\" should not be a sub-category of \"all life\", \"animal\".",
            one.isEqualOrSubCategory(two));

        assertFalse("Category \"all life\", \"plant\" should not be a sub-category of \"all life\", \"animal\".",
            one.isEqualOrSubCategory(three));
    }

    /** Check that sub-category checking is false for non sub-categories of the same type. */
    public void testSubCategoryFalseForNonSubCategories() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life" });
        HierarchyAttribute three = factory.createHierarchyAttribute(new String[] { "all life", "plant" });

        // Check that the parent is not a strict sub-cateogy of itself.
        assertFalse("Category \"all life\" should be equal or a sub-category of \"all life\".", one.isSubCategory(one));

        // Check that the sub-category is a sub-category of the parent one.
        assertFalse("Category \"all life\" should not be a sub-category of \"all life\", \"animal\".",
            one.isSubCategory(two));

        assertFalse("Category \"all life\", \"plant\" should not be a sub-category of \"all life\", \"animal\".",
            one.isSubCategory(three));
    }

    /** Check that sub-category checking does not work for otherwise legal sub-categories of different types. */
    public void testSubCategoryFalseForSubCategoriesOfDifferentType() throws Exception
    {
        // Get a factory for a different test class.
        HierarchyAttributeFactory factory2 = HierarchyAttribute.getFactoryForClass("test2");

        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory2.createHierarchyAttribute(new String[] { "all life" });

        // Check that the sub-category is a sub-category of the parent one.
        assertFalse("Sub-category \"all life\", \"animal\" should not be a sub-category of \"all life\" as it is " +
            "of a different type class.", two.isEqualOrSubCategory(one));
    }

    /** Check can get attribute ids ok. */
    public void testIdOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute h1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute h2 = factory.createHierarchyAttribute(new String[] { "all life", "plant" });
        HierarchyAttribute h3 = factory.createHierarchyAttribute(new String[] { "all life", "bacteria" });
        HierarchyAttribute h4 = factory.createHierarchyAttribute(new String[] { "all life", "virus" });

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
        HierarchyAttribute h1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Set new ids on them.
        h1.setId(h1.getId());
        h1.setId(100L);

        assertEquals("Id was set to 100 but is " + h1.getId(), h1.getId(), 100L);
    }

    /** Check that setting id on unfinalized attribute class fails when another value already has the id. */
    public void testSetIdOnUnfinalizedFailsAlreadyInUse() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute h1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute h2 = factory.createHierarchyAttribute(new String[] { "all life", "plant" });

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
        HierarchyAttribute h1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

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
        HierarchyAttribute h1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute h2 = factory.createHierarchyAttribute(new String[] { "all life", "plant" });

        // Finalize the attribute class.
        factory.finalizeAttribute();

        // Uset setId to turn h2 into h1.
        long id1 = h1.getId();
        h2.setId(id1);

        assertTrue("h2 should have labe \"animal\" at level after being transformed into h1 by setId.",
            "animal".equals(h2.getValueAtLevel(1)));
    }

    /** Check that getValueAtLevel returns the correct results. */
    public void testGetValueAtLevelOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute h1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        assertTrue("h2 should have label \"animal\" at 1.", "animal".equals(h1.getValueAtLevel(1)));
    }

    /** Check that getMaxLevels returns the correct results. */
    public void testMaxLevelsOk() throws Exception
    {
        factory.createHierarchyAttribute(new String[] { "all life" });
        assertEquals("Hierarchy type with one level should have max levels 1.", 1, factory.getMaxLevels());

        factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        assertEquals("Hierarchy type with two levels should have max levels 2.", 2, factory.getMaxLevels());

        factory.createHierarchyAttribute(new String[] { "all life", "animal", "mamal" });
        assertEquals("Hierarchy type with three levels should have max levels 3.", 3, factory.getMaxLevels());

        // Drop the the type from the factory and start again.
        factory.dropAttributeClass();
        factory = HierarchyAttribute.getFactoryForClass("test");

        // Try jumping in at the deep end and creating a 3 deep straight off.
        factory.createHierarchyAttribute(new String[] { "all life", "animal", "mamal" });
        assertEquals("Hierarchy type with three levels should have max levels 3.", 3, factory.getMaxLevels());
    }

    /** Check that non-allowable values of finalized attribute class cannot be created. */
    public void testCannotCreateNonAllowable() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute h1 = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Finalize the attribute class.
        factory.finalizeAttribute();

        // Try to create an existing but non-allowable attribute.
        boolean testPassed = false;

        try
        {
            factory.createHierarchyAttribute(new String[] { "all life" });
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
        assertEquals("Test factory class name should be \"test\".", "test", factory.getName());
    }

    /** Check that dropping an attribute class works. */
    public void testDroppingAttributeClassOk() throws Exception
    {
        factory.dropAttributeClass();
    }

    /** Check that listing all the values as a set of an unfinalized class fails with infinite values exception. */
    public void testListingAllValuesSetForUnfinalizedFails() throws Exception
    {
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

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
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

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
        HierarchyAttribute lastCreated = null;

        for (int i = 0; i < 10; i++)
        {
            lastCreated = factory.createHierarchyAttribute(new String[] { "top", Integer.toString(i) });
        }

        // Finalize the test class.
        factory.finalizeAttribute();

        // Try to get a listing of all the possible attribute values.
        Set<HierarchyAttribute> allValues = lastCreated.getType().getAllPossibleValuesSet();

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<HierarchyAttribute> testValues = new HashSet<HierarchyAttribute>();

        for (int j = 0; j < 10; j++)
        {
            testValues.add(factory.createHierarchyAttribute(new String[] { "top", Integer.toString(j) }));
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
        HierarchyAttribute lastCreated = null;

        for (int i = 0; i < 10; i++)
        {
            lastCreated = factory.createHierarchyAttribute(new String[] { "top", Integer.toString(i) });
        }

        // Finalize the test class.
        factory.finalizeAttribute();

        // Try to get a listing of all the possible attribute values.
        Set<HierarchyAttribute> allValues = new HashSet<HierarchyAttribute>();

        for (Iterator<HierarchyAttribute> allValuesIterator = lastCreated.getType().getAllPossibleValuesIterator();
                allValuesIterator.hasNext();)
        {
            allValues.add(allValuesIterator.next());
        }

        // Check that the set of allValues contains all of the strings in the class and no more.
        Set<HierarchyAttribute> testValues = new HashSet<HierarchyAttribute>();

        for (int j = 0; j < 10; j++)
        {
            testValues.add(factory.createHierarchyAttribute(new String[] { "top", Integer.toString(j) }));
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
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "animal" });

        // Check their ordinal representations are the same.
        assertTrue("Different attributes of the same class should have differnt ordinals.",
            one.ordinal() == two.ordinal());
    }

    /** Check that different attributes have different ordinals. */
    public void testDifferentAttributesHaveDifferentOrdinal() throws Exception
    {
        // Use the test factory to generate some differnt hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "animal" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "plant" });

        // Check their ordinal representations are not the same.
        assertFalse("Different attributes of the same class should have different ordinals.",
            one.ordinal() == two.ordinal());
    }

    /** Check that a hierarchy level can be listed. */
    public void testLevelListingOk() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "plant" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "animal", "mamal" });
        HierarchyAttribute three = factory.createHierarchyAttribute(new String[] { "all life", "animal", "reptile" });

        // Set up some level names.
        factory.setLevelNames(new String[] { "top", "group", "subgroup" });

        // Try listing the top level.
        Set<HierarchyAttribute> topLevel = factory.getType().getValuesAtLevelSet("top");

        HierarchyAttribute comp = factory.createHierarchyAttributeForComparison(new String[] { "all life" });

        if (topLevel.size() != 1)
        {
            errorMessage += "The top level should contain only one element, but contains " + topLevel.size() + ".\n";
        }

        if (!comp.equals(topLevel.iterator().next()))
        {
            errorMessage += "The top level should only contain \"all life\".\n";
        }

        // Try listing the group level.
        Set<HierarchyAttribute> groupLevel = factory.getType().getValuesAtLevelSet("group");

        if (groupLevel.size() != 3)
        {
            errorMessage += "The group level should contain 3 elements, but contains " + groupLevel.size() + ".\n";
        }

        HierarchyAttribute comp1 = factory.createHierarchyAttributeForComparison(new String[] { "all life", "animal" });
        HierarchyAttribute comp2 = factory.createHierarchyAttributeForComparison(new String[] { "all life", "plant" });

        for (HierarchyAttribute nextGroupAttr : groupLevel)
        {
            if (!nextGroupAttr.isEqualOrSubCategory(comp1) && !nextGroupAttr.isEqualOrSubCategory(comp2))
            {
                errorMessage += "The group level should only contain super categories of \"animal\" or \"plant\".\n";
            }
        }

        // Try listing the subgroup level.
        Set<HierarchyAttribute> subgroupLevel = factory.getType().getValuesAtLevelSet("subgroup");

        if (subgroupLevel.size() != 5)
        {
            errorMessage += "The subgroup level should contain 5 elements, but contains " + groupLevel.size() + ".\n";
        }

        // Report any errors.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that a sub hierarchy can be listed. */
    public void testSubCategoryLevelListingOk() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute one = factory.createHierarchyAttribute(new String[] { "all life", "plant" });
        HierarchyAttribute two = factory.createHierarchyAttribute(new String[] { "all life", "animal", "mamal" });
        HierarchyAttribute three = factory.createHierarchyAttribute(new String[] { "all life", "animal", "reptile" });

        // Set up some level names.
        factory.setLevelNames(new String[] { "top", "group", "subgroup" });

        // Try listing the just the group level strictly below "all life".
        HierarchyAttribute comp = factory.createHierarchyAttributeForComparison(new String[] { "all life" });
        Set<HierarchyAttribute> groupLevel = factory.getType().getSubHierarchyValuesSet(comp, "group");

        if (groupLevel.size() != 2)
        {
            errorMessage += "The group level should contain 2 elements, but contains " + groupLevel.size() + ".\n";
        }

        HierarchyAttribute comp1 = factory.createHierarchyAttributeForComparison(new String[] { "all life", "animal" });
        HierarchyAttribute comp2 = factory.createHierarchyAttributeForComparison(new String[] { "all life", "plant" });

        for (HierarchyAttribute nextGroupAttr : groupLevel)
        {
            if (!nextGroupAttr.isEqualOrSubCategory(comp1) && !nextGroupAttr.isEqualOrSubCategory(comp2))
            {
                errorMessage += "The group level should only contain super categories of \"animal\" or \"plant\".\n";
            }
        }

        for (HierarchyAttribute nextGroupAttr : groupLevel)
        {
            if (!comp.isSubCategory(nextGroupAttr))
            {
                errorMessage += "The group level should only contain strict sub-categories of \"all life\".\n";
            }
        }

        // Try listing the subgroup level below "animal".
        HierarchyAttribute comp3 = factory.createHierarchyAttributeForComparison(new String[] { "all life", "animal" });
        Set<HierarchyAttribute> subgroupLevel = factory.getType().getSubHierarchyValuesSet(comp3, "subgroup");

        if (subgroupLevel.size() != 2)
        {
            errorMessage += "The subgroup level should contain 2 elements, but contains " + groupLevel.size() + ".\n";
        }

        // Report any errors.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that listing the complete hierarchy path from root to tip works ok. */
    public void testListingHierarchyPathOk() throws Exception
    {
        String errorMessage = "";

        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute test = factory.createHierarchyAttribute(new String[] { "all life", "animal", "reptile" });

        HierarchyAttribute comp1 = factory.createHierarchyAttributeForComparison(new String[] { "all life" });
        HierarchyAttribute comp2 = factory.createHierarchyAttributeForComparison(new String[] { "all life", "animal" });
        HierarchyAttribute comp3 =
            factory.createHierarchyAttributeForComparison(new String[] { "all life", "animal", "reptile" });

        List<HierarchyAttribute> listing = test.getHierarchyPath();

        // Check the listing is correct.
        if (!listing.get(0).equals(comp1))
        {
            errorMessage += "Was expecting, " + comp1 + ", at position 0.\n";
        }

        if (!listing.get(1).equals(comp2))
        {
            errorMessage += "Was expecting, " + comp2 + ", at position 1.\n";
        }

        if (!listing.get(2).equals(comp3))
        {
            errorMessage += "Was expecting, " + comp3 + ", at position 2.\n";
        }

        // Report any errors.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that getting the last label value works ok. */
    public void testLastLabelOk() throws Exception
    {
        // Use the test factory to generate some hierarchy attributes.
        HierarchyAttribute test = factory.createHierarchyAttribute(new String[] { "all life", "animal", "reptile" });

        assertTrue("Was expecting the last label value to be \"reptile\".", "reptile".equals(test.getLastValue()));
    }

    /** Ensures that the attribute class 'test' is clean for each test. */
    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Explicitly drop the test class to ensure that it is clean.
        factory = HierarchyAttribute.getFactoryForClass("test");
        factory.dropAttributeClass();

        // Create the test factory for the test class.
        factory = HierarchyAttribute.getFactoryForClass("test");
    }

    /** Ensures that the attribute class 'test' is clean for each test. */
    protected void tearDown() throws Exception
    {
        NDC.pop();

        // Explicitly drop the test class.
        factory.dropAttributeClass();
    }
}
