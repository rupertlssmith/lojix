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
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestBeanImpl implements TestBean
{
    public boolean testBoolean = TestBean.TEST_BOOLEAN;

    public char testCharacter = TestBean.TEST_CHARACTER;

    public byte testByte = TestBean.TEST_BYTE;

    public short testShort = TestBean.TEST_SHORT;

    public int testInteger = TestBean.TEST_INTEGER;

    public long testLong = TestBean.TEST_LONG;

    public float testFloat = TestBean.TEST_FLOAT;

    public double testDouble = TestBean.TEST_DOUBLE;

    public String testString = TestBean.TEST_STRING;

    public Object testObject = TestBean.TEST_OBJECT;

    public boolean getTestBoolean()
    {
        return testBoolean;
    }

    public void setTestBoolean(boolean testBoolean)
    {
        this.testBoolean = testBoolean;
    }

    public char getTestCharacter()
    {
        return testCharacter;
    }

    public void setTestCharacter(char testChar)
    {
        this.testCharacter = testChar;
    }

    public byte getTestByte()
    {
        return testByte;
    }

    public void setTestByte(byte testByte)
    {
        this.testByte = testByte;
    }

    public short getTestShort()
    {
        return testShort;
    }

    public void setTestShort(short testShort)
    {
        this.testShort = testShort;
    }

    public int getTestInteger()
    {
        return testInteger;
    }

    public void setTestInteger(int testInteger)
    {
        this.testInteger = testInteger;
    }

    public long getTestLong()
    {
        return testLong;
    }

    public void setTestLong(long testLong)
    {
        this.testLong = testLong;
    }

    public float getTestFloat()
    {
        return testFloat;
    }

    public void setTestFloat(float testFloat)
    {
        this.testFloat = testFloat;
    }

    public double getTestDouble()
    {
        return testDouble;
    }

    public void setTestDouble(double testDouble)
    {
        this.testDouble = testDouble;
    }

    public String getTestString()
    {
        return testString;
    }

    public void setTestString(String testString)
    {
        this.testString = testString;
    }

    public Object getTestObject()
    {
        return testObject;
    }

    public void setTestObject(Object testObject)
    {
        this.testObject = testObject;
    }

    public String getStringException()
    {
        throw new IllegalStateException();
    }

    public void setStringException(String test)
    {
        throw new IllegalStateException();
    }
}
