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
 * Assignment is a leaf data element of a decision tree. It represents the outcome of a decision when the decision path
 * reaches the leaf that contains a given assignment. The assignment is the classification of a property of the input
 * state to the decision, assigning an {@link OrdinalAttribute} value to it. In addition to the actual output attribute
 * an assignment also specifies the output attribute value that a preceding decision must find in the input state in
 * order to match this assignment.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Map a property to an attribute.
 * <tr><td> Specify matching attribute value on previous decision to arrive at this assignment.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Assignment extends DecisionTreeElement
{
    /** Holds the name of the property that this assignment acts on. */
    private final String propertyName;

    /** Holds the attribute value that is assigned. */
    private final OrdinalAttribute toAssign;

    /**
     * Builds an assignment of a value to a property.
     *
     * @param property the name of the property that this assigns to.
     * @param outcome  the attribute value of the assignment.
     * @param matching the matching attribute value that a parent decision must match in order to arrive at this
     *                 assignment.
     */
    public Assignment(String property, OrdinalAttribute outcome, OrdinalAttribute matching)
    {
        this.propertyName = property;
        this.toAssign = outcome;
        attributeValue = matching;
    }

    /**
     * Gets the name of the property that this is an assignment to.
     *
     * @return The name of the property to assign.
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Gets the attribute value that is to be set by this assignment.
     *
     * @return The attribute value to assign to the property.
     */
    public OrdinalAttribute getAttribute()
    {
        return toAssign;
    }
}
