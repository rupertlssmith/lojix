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
package com.thesett.aima.state.impl;

import com.thesett.aima.state.TestBean;

/**
 * Provides an implementation of the test bean as an extension of ExtendableBeanState to expose itself as a State.
 */
public class TestExtendableBean extends ExtendableBeanState implements TestBean
{
    public boolean testBoolean = TestBean.TEST_BOOLEAN;

    public char testChar = TestBean.TEST_CHARACTER;

    public byte testByte = TestBean.TEST_BYTE;

    public short testShort = TestBean.TEST_SHORT;

    public int testInt = TestBean.TEST_INTEGER;

    public long testLong = TestBean.TEST_LONG;

    public float testFloat = TestBean.TEST_FLOAT;

    public double testDouble = TestBean.TEST_DOUBLE;

    public String testString = TestBean.TEST_STRING;

    public Object testObject = TestBean.TEST_OBJECT;

    public boolean getTestBoolean()
    {
        return this.testBoolean;
    }

    public void setTestBoolean(boolean testBoolean)
    {
        this.testBoolean = testBoolean;
    }

    public char getTestCharacter()
    {
        return this.testChar;
    }

    public void setTestCharacter(char testChar)
    {
        this.testChar = testChar;
    }

    public byte getTestByte()
    {
        return this.testByte;
    }

    public void setTestByte(byte testByte)
    {
        this.testByte = testByte;
    }

    public short getTestShort()
    {
        return this.testShort;
    }

    public void setTestShort(short testShort)
    {
        this.testShort = testShort;
    }

    public int getTestInteger()
    {
        return this.testInt;
    }

    public void setTestInteger(int testInt)
    {
        this.testInt = testInt;
    }

    public long getTestLong()
    {
        return this.testLong;
    }

    public void setTestLong(long testLong)
    {
        this.testLong = testLong;
    }

    public float getTestFloat()
    {
        return this.testFloat;
    }

    public void setTestFloat(float testFloat)
    {
        this.testFloat = testFloat;
    }

    public double getTestDouble()
    {
        return this.testDouble;
    }

    public void setTestDouble(double testDouble)
    {
        this.testDouble = testDouble;
    }

    public String getTestString()
    {
        return this.testString;
    }

    public void setTestString(String testString)
    {
        this.testString = testString;
    }

    public Object getTestObject()
    {
        return this.testObject;
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
