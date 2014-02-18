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

import junit.framework.TestCase;


import org.apache.log4j.NDC;

/**
 * Checks that the JavaType Type wrapper works correctly for all basic java types.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check Boolean type can be derived from instance.
 * <tr><td> Check Boolean type can be derived from class.
 * <tr><td> Check Boolean type can produce default instance.
 * <tr><td> Check Boolean type can be derived from instance produces correct underlying class.
 * <tr><td> Check Character type can be derived from instance.
 * <tr><td> Check Character type can be derived from class.
 * <tr><td> Check Character type can produce default instance.
 * <tr><td> Check Character type can be derived from instance produces correct underlying class.
 * <tr><td> Check Byte type can be derived from instance.
 * <tr><td> Check Byte type can be derived from class.
 * <tr><td> Check Byte type can produce default instance.
 * <tr><td> Check Byte type can be derived from instance produces correct underlying class.
 * <tr><td> Check Short type can be derived from instance.
 * <tr><td> Check Short type can be derived from class.
 * <tr><td> Check Short type can produce default instance.
 * <tr><td> Check Short type can be derived from instance produces correct underlying class.
 * <tr><td> Check Integer type can be derived from instance.
 * <tr><td> Check Integer type can be derived from class.
 * <tr><td> Check Integer type can produce default instance.
 * <tr><td> Check Integer type can be derived from instance produces correct underlying class.
 * <tr><td> Check Long type can be derived from instance.
 * <tr><td> Check Long type can be derived from class.
 * <tr><td> Check Long type can produce default instance.
 * <tr><td> Check Long type can be derived from instance produces correct underlying class.
 * <tr><td> Check Float type can be derived from instance.
 * <tr><td> Check Float type can be derived from class.
 * <tr><td> Check Float type can produce default instance.
 * <tr><td> Check Float type can be derived from instance produces correct underlying class.
 * <tr><td> Check Double type can be derived from instance.
 * <tr><td> Check Double type can be derived from class.
 * <tr><td> Check Double type can produce default instance.
 * <tr><td> Check Double type can be derived from instance produces correct underlying class.
 * <tr><td> Check String type can be derived from instance.
 * <tr><td> Check String type can be derived from class.
 * <tr><td> Check String type can produce default instance.
 * <tr><td> Check String type can be derived from instance produces correct underlying class.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class JavaTypeTest extends TestCase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(JavaTypeTest.class.getName()); */

    public JavaTypeTest(String name)
    {
        super(name);
    }

    /** Check Boolean type can be derived from instance. */
    public void testBooleanFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Boolean test = JavaType.DEFAULT_BOOLEAN;

        // Use it to derive the java type wrapper.
        JavaType<Boolean> type = new JavaType<Boolean>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Boolean.", type.getBaseClass().equals(Boolean.class));
    }

    /** Check Boolean type can be derived from class. */
    public void testBooleanFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Boolean> type = new JavaType<Boolean>(Boolean.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Boolean.", type.getBaseClass().equals(Boolean.class));
    }

    /** Check Boolean type can produce default instance. */
    public void testBooleanDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Boolean> type = new JavaType<Boolean>(Boolean.class);

        // Create the default instance.
        Boolean def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_BOOLEAN, def.equals(JavaType.DEFAULT_BOOLEAN));
    }

    /** Check Boolean type can be derived from instance produces correct underlying class. */
    public void testBooleanDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Boolean test = JavaType.DEFAULT_BOOLEAN;

        // Use it to derive the java type wrapper.
        JavaType<Boolean> type = new JavaType<Boolean>(test);

        // Create the default instance.
        Boolean def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_BOOLEAN, def.equals(JavaType.DEFAULT_BOOLEAN));
    }

    /** Check Character type can be derived from instance. */
    public void testCharacterFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Character test = JavaType.DEFAULT_CHARACTER;

        // Use it to derive the java type wrapper.
        JavaType<Character> type = new JavaType<Character>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Character.", type.getBaseClass().equals(Character.class));
    }

    /** Check Character type can be derived from class. */
    public void testCharacterFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Character> type = new JavaType<Character>(Character.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Character.", type.getBaseClass().equals(Character.class));
    }

    /** Check Character type can produce default instance. */
    public void testCharacterDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Character> type = new JavaType<Character>(Character.class);

        // Create the default instance.
        Character def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_CHARACTER,
            def.equals(JavaType.DEFAULT_CHARACTER));
    }

    /** Check Character type can be derived from instance produces correct underlying class. */
    public void testCharacterDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Character test = JavaType.DEFAULT_CHARACTER;

        // Use it to derive the java type wrapper.
        JavaType<Character> type = new JavaType<Character>(test);

        // Create the default instance.
        Character def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_CHARACTER,
            def.equals(JavaType.DEFAULT_CHARACTER));
    }

    /** Check Byte type can be derived from instance. */
    public void testByteFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Byte test = JavaType.DEFAULT_BYTE;

        // Use it to derive the java type wrapper.
        JavaType<Byte> type = new JavaType<Byte>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Byte.", type.getBaseClass().equals(Byte.class));
    }

    /** Check Byte type can be derived from class. */
    public void testByteFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Byte> type = new JavaType<Byte>(Byte.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Byte.", type.getBaseClass().equals(Byte.class));
    }

    /** Check Byte type can produce default instance. */
    public void testByteDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Byte> type = new JavaType<Byte>(Byte.class);

        // Create the default instance.
        Byte def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_BYTE, def.equals(JavaType.DEFAULT_BYTE));
    }

    /** Check Byte type can be derived from instance produces correct underlying class. */
    public void testByteDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Byte test = JavaType.DEFAULT_BYTE;

        // Use it to derive the java type wrapper.
        JavaType<Byte> type = new JavaType<Byte>(test);

        // Create the default instance.
        Byte def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_BYTE, def.equals(JavaType.DEFAULT_BYTE));
    }

    /** Check Short type can be derived from instance. */
    public void testShortFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Short test = JavaType.DEFAULT_SHORT;

        // Use it to derive the java type wrapper.
        JavaType<Short> type = new JavaType<Short>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Short.", type.getBaseClass().equals(Short.class));
    }

    /** Check Short type can be derived from class. */
    public void testShortFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Short> type = new JavaType<Short>(Short.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Short.", type.getBaseClass().equals(Short.class));
    }

    /** Check Short type can produce default instance. */
    public void testShortDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Short> type = new JavaType<Short>(Short.class);

        // Create the default instance.
        Short def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_SHORT, def.equals(JavaType.DEFAULT_SHORT));
    }

    /** Check Short type can be derived from instance produces correct underlying class. */
    public void testShortDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Short test = JavaType.DEFAULT_SHORT;

        // Use it to derive the java type wrapper.
        JavaType<Short> type = new JavaType<Short>(test);

        // Create the default instance.
        Short def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_SHORT, def.equals(JavaType.DEFAULT_SHORT));
    }

    /** Check Integer type can be derived from instance. */
    public void testIntegerFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Integer test = JavaType.DEFAULT_INTEGER;

        // Use it to derive the java type wrapper.
        JavaType<Integer> type = new JavaType<Integer>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Integer.", type.getBaseClass().equals(Integer.class));
    }

    /** Check Integer type can be derived from class. */
    public void testIntegerFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Integer> type = new JavaType<Integer>(Integer.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Integer.", type.getBaseClass().equals(Integer.class));
    }

    /** Check Integer type can produce default instance. */
    public void testIntegerDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Integer> type = new JavaType<Integer>(Integer.class);

        // Create the default instance.
        Integer def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_INTEGER, def.equals(JavaType.DEFAULT_INTEGER));
    }

    /** Check Integer type can be derived from instance produces correct underlying class. */
    public void testIntegerDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Integer test = JavaType.DEFAULT_INTEGER;

        // Use it to derive the java type wrapper.
        JavaType<Integer> type = new JavaType<Integer>(test);

        // Create the default instance.
        Integer def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_INTEGER, def.equals(JavaType.DEFAULT_INTEGER));
    }

    /** Check Long type can be derived from instance. */
    public void testLongFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Long test = JavaType.DEFAULT_LONG;

        // Use it to derive the java type wrapper.
        JavaType<Long> type = new JavaType<Long>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Long.", type.getBaseClass().equals(Long.class));
    }

    /** Check Long type can be derived from class. */
    public void testLongFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Long> type = new JavaType<Long>(Long.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Long.", type.getBaseClass().equals(Long.class));
    }

    /** Check Long type can produce default instance. */
    public void testLongDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Long> type = new JavaType<Long>(Long.class);

        // Create the default instance.
        Long def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_LONG, def.equals(JavaType.DEFAULT_LONG));
    }

    /** Check Long type can be derived from instance produces correct underlying class. */
    public void testLongDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Long test = JavaType.DEFAULT_LONG;

        // Use it to derive the java type wrapper.
        JavaType<Long> type = new JavaType<Long>(test);

        // Create the default instance.
        Long def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_LONG, def.equals(JavaType.DEFAULT_LONG));
    }

    /** Check Float type can be derived from instance. */
    public void testFloatFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Float test = JavaType.DEFAULT_FLOAT;

        // Use it to derive the java type wrapper.
        JavaType<Float> type = new JavaType<Float>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Float.", type.getBaseClass().equals(Float.class));
    }

    /** Check Float type can be derived from class. */
    public void testFloatFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Float> type = new JavaType<Float>(Float.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Float.", type.getBaseClass().equals(Float.class));
    }

    /** Check Float type can produce default instance. */
    public void testFloatDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Float> type = new JavaType<Float>(Float.class);

        // Create the default instance.
        Float def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_FLOAT, def.equals(JavaType.DEFAULT_FLOAT));
    }

    /** Check Float type can be derived from instance produces correct underlying class. */
    public void testFloatDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Float test = JavaType.DEFAULT_FLOAT;

        // Use it to derive the java type wrapper.
        JavaType<Float> type = new JavaType<Float>(test);

        // Create the default instance.
        Float def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_FLOAT, def.equals(JavaType.DEFAULT_FLOAT));
    }

    /** Check Double type can be derived from instance. */
    public void testDoubleFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Double test = JavaType.DEFAULT_DOUBLE;

        // Use it to derive the java type wrapper.
        JavaType<Double> type = new JavaType<Double>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Double.", type.getBaseClass().equals(Double.class));
    }

    /** Check Double type can be derived from class. */
    public void testDoubleFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Double> type = new JavaType<Double>(Double.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be Double.", type.getBaseClass().equals(Double.class));
    }

    /** Check Double type can produce default instance. */
    public void testDoubleDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<Double> type = new JavaType<Double>(Double.class);

        // Create the default instance.
        Double def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_DOUBLE, def.equals(JavaType.DEFAULT_DOUBLE));
    }

    /** Check Double type can be derived from instance produces correct underlying class. */
    public void testDoubleDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        Double test = JavaType.DEFAULT_DOUBLE;

        // Use it to derive the java type wrapper.
        JavaType<Double> type = new JavaType<Double>(test);

        // Create the default instance.
        Double def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + JavaType.DEFAULT_DOUBLE, def.equals(JavaType.DEFAULT_DOUBLE));
    }

    /** Check String type can be derived from instance. */
    public void testStringFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        String test = "";

        // Use it to derive the java type wrapper.
        JavaType<String> type = new JavaType<String>(test);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be String.", type.getBaseClass().equals(String.class));
    }

    /** Check String type can be derived from class. */
    public void testStringFromClassOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<String> type = new JavaType<String>(String.class);

        // Check that the underlying class is correct.
        assertTrue("Underlying class should be String.", type.getBaseClass().equals(String.class));
    }

    /** Check String type can produce default instance. */
    public void testStringDefaultInstanceOk() throws Exception
    {
        // Create the type wrapper from the class.
        JavaType<String> type = new JavaType<String>(String.class);

        // Create the default instance.
        String def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + "\"\"", "".equals(def));
    }

    /** Check String type can be derived from instance produces correct underlying class. */
    public void testStringDefaultInstanceFromInstanceOk() throws Exception
    {
        // Create an example value of the type.
        String test = "";

        // Use it to derive the java type wrapper.
        JavaType<String> type = new JavaType<String>(test);

        // Create the default instance.
        String def = type.getDefaultInstance();

        // Check that the default is not null.
        assertNotNull("The default instance should not be null.", def);

        // Check that the default is of the correct value.
        assertTrue("The default should have value, " + "\"\"", "".equals(def));
    }

    protected void setUp() throws Exception
    {
        // Push a client identifier onto the Nested Diagnostic Context so that Log4J will be able to identify all
        // logging output for this tests.
        NDC.push(getName());
    }

    protected void tearDown() throws Exception
    {
        // Clear the nested diagnostic context for this test.
        NDC.pop();
    }
}
