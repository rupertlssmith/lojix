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
package com.thesett.common.util.doublemaps;

/**
 * SymbolTableImplTest test the {@link SymbolTableImpl} implementation.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check against all symbol table tests.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SymbolTableImplTest extends SymbolTableTestBase<Integer, Object, Object>
{
    /** Used to generate new sequential test keys. */
    private int nextTestKey;

    /**
     * Creates a test with the specified name.
     *
     * @param name The name of the test.
     */
    public SymbolTableImplTest(String name)
    {
        super(name, new SymbolTableImpl<Integer, Object, Object>());
    }

    /** {@inheritDoc} */
    protected Integer createTestKey()
    {
        return nextTestKey++;
    }

    /** {@inheritDoc} */
    protected Object createTestField()
    {
        return new Object();
    }

    /** {@inheritDoc} */
    protected Object createTestValue()
    {
        return new Object();
    }
}
