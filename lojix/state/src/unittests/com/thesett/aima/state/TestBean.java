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
package com.thesett.aima.state;

/**
 * Defines an interface for a test bean that defines at least one field of each basic Java type, plus some dynamic
 * attribute types, and getter and setter methods that raise exceptions so that the state introspection implemtations
 * can be tested againt it.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TestBean
{
    public static final boolean TEST_BOOLEAN = true;
    public static final char TEST_CHARACTER = '\u0001';
    public static final byte TEST_BYTE = 127;
    public static final short TEST_SHORT = 4096;
    public static final int TEST_INTEGER = 400;
    public static final long TEST_LONG = 999999999;
    public static final float TEST_FLOAT = 3.14159f;
    public static final double TEST_DOUBLE = Math.log(2.0d);
    public static final String TEST_STRING = "test";
    public static final Object TEST_OBJECT = new Object();

    public boolean getTestBoolean();

    public void setTestBoolean(boolean testBoolean);

    public char getTestCharacter();

    public void setTestCharacter(char testChar);

    public byte getTestByte();

    public void setTestByte(byte testByte);

    public short getTestShort();

    public void setTestShort(short testShort);

    public int getTestInteger();

    public void setTestInteger(int testInt);

    public long getTestLong();

    public void setTestLong(long testLong);

    public float getTestFloat();

    public void setTestFloat(float testFloat);

    public double getTestDouble();

    public void setTestDouble(double testDouble);

    public String getTestString();

    public void setTestString(String testString);

    public Object getTestObject();

    public void setTestObject(Object testObject);

    public String getStringException();

    public void setStringException(String test);
}
