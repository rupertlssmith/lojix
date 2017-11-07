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
 * Function2 specifies the interface of a function from a pair of types to another.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th></tr>
 * <tr><td> Provide a functional mapping from X, Y to Z. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Function2<X, Y, Z>
{
    /**
     * Returns the result of type Z from applying this function to an arguments of type X, Y.
     *
     * @param  x The argument to the function.
     * @param  y The argument to the function.
     *
     * @return The result of applying the function to its arguments.
     */
    Z apply(X x, Y y);
}
