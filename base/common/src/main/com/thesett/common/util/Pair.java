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
package com.thesett.common.util;

/**
 * Pair implements a simple tuple of data elements.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Hold a pair of data elements.
 * </table></pre>
 *
 * @param  <A> The type of the first element.
 * @param  <B> The type of the second element.
 *
 * @author Rupert Smith
 */
public class Pair<A, B>
{
    /** Holds the first data element. */
    private A first;

    /** Holds the second data element. */
    private B second;

    /**
     * Creates a pair of data elements.
     *
     * @param first  The first element of the tuple.
     * @param second The second element of the tuple.
     */
    public Pair(A first, B second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * Provides the first element of the tuple.
     *
     * @return The first element of the tuple.
     */
    public A getFirst()
    {
        return first;
    }

    /**
     * Sets the first element if the tuple.
     *
     * @param first The first element of the tuple.
     */
    public void setFirst(A first)
    {
        this.first = first;
    }

    /**
     * Provides the second element of the tuple.
     *
     * @return The second element of the tuple.
     */
    public B getSecond()
    {
        return second;
    }

    /**
     * Sets the second element if the tuple.
     *
     * @param second The second element of the tuple.
     */
    public void setSecond(B second)
    {
        this.second = second;
    }
}
