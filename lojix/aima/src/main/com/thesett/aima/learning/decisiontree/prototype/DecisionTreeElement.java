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
package com.thesett.aima.learning.decisiontree.prototype;

import com.thesett.aima.state.OrdinalAttribute;

/**
 * DecisionTreeElement represents the type of elements that a decision tree can store. Nodes of decision trees contain
 * decisions, leafs contain property assignments (or classifications). There is also a special element type that a leaf
 * of a decision tree under construction can contain and that is a marker value that indicates that the branch at that
 * point has not been built yet.
 *
 * <p>Every decision tree element can have an {@link OrdinalAttribute} value associated with it, this is the value of a
 * property tested for by a decision that must match the property value of a data point being decided upon in order to
 * follow that decision branch on the tree.
 *
 * <p>See {@link Decision}, {@link Assignment} and {@link Pending} for implementations of this interface that provide
 * the implementations of the different kinds of decision tree element.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide attribute value to match from parent decision.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DecisionTreeElement
{
    /** The attribute value associated with this decision tree element. */
    protected OrdinalAttribute attributeValue = null;

    /**
     * Returns the attribute value associated with this decision tree element.
     *
     * @return The output attribute value that a preceding decision must find in the input state in order to take the
     *         decision branch for this decision tree element.
     */
    public OrdinalAttribute getAttributeValue()
    {
        return attributeValue;
    }
}
