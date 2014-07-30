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
package com.thesett.common.util.logic;

import java.util.Comparator;

/**
 * PartialOrdering is an adapter class that turns a binary prediciate that is also a partial ordering into a Comparator.
 * A partial ordering is a binary predicate on the cross product of a set with itself, that is reflexive, antisymetric,
 * and transitive. That is to say that for the relation, R, the following rules hold:
 *
 * <p/>
 * <pre><ul>
 * <li>aRa</li>
 * <li>aRb && bRa => a = b (though not strictly that a.equals(b)</li>
 * <li>aRb && bRc => aRc</li>
 * </ul></pre>
 *
 * <p/>The rules for Comparators in Java are that:
 *
 * <p/>
 * <pre><ul>
 * <li>sgn(compare(x, y)) == -sgn(compare(y, x))</li>
 * <li>((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0</li>
 * <li>compare(x, y)==0  implies that sgn(compare(x, z))==sgn(compare(y, z))</li>
 * </ul></pre>
 *
 * <p/>To express a binary predicate that is also a partial ordering as a Comparator, the following rules for the
 * evaluation of the compare method are used:
 *
 * <p/>
 * <pre><ul>
 * <li>if aRb and bRa then compare(a, b) == 0</li>
 * <li>if aRb then compare(a, b) == 1</li>
 * <li>if !aRb then compare(a, b) == -1</li>
 * </ul></pre>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Express a partial ordering as a comparator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PartialOrdering<T> implements Comparator<T>
{
    /** Holds the partial ordering to use as a comparator. */
    BinaryPredicate<T, T> partialOrdering;

    /**
     * Creates a partial ordering comparator from a binary predicate that is a partial ordering.
     *
     * @param pred The binary predicate, this must be a partial order.
     */
    public PartialOrdering(BinaryPredicate<T, T> pred)
    {
        partialOrdering = pred;
    }

    /**
     * Translates the partial order into the +1, 0, -1 convention needed by Comparators.
     *
     * @param  a The first item to compare.
     * @param  b The second item to compare.
     *
     * @return 0 if the two elements are equal under the partial ordering, 1 if aRb, and -1 if !aRb.
     */
    public int compare(T a, T b)
    {
        boolean aRb = partialOrdering.evaluate(a, b);

        if (!aRb)
        {
            return -1;
        }

        boolean bRa = partialOrdering.evaluate(b, a);

        return (aRb && bRa) ? 0 : 1;
    }
}
