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
package com.thesett.common.webapp.beans;

/**
 * Simple bean used to record the currently sorted state of an object. There are three sort states: unsorted, forward
 * and reverse. The bean also records the name of a property (or field) that the sort is by. This is useful for
 * recording the sort state of object that can be sorted by multiple properties where a sort can be applied to only one
 * property at once. If an object can be simultaneously sorted by several properties at once this state can be recorded
 * by using more than one sort state bean. By implication an object is unsorted by a particular field if the sort state
 * field does not match that field.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain sort state
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SortStateBean
{
    /** The unsorted state. */
    public static final String UNSORTED = "unsorted";

    /** The forward sorted state. */
    public static final String FORWARD = "forward";

    /** The reverse sorted state. */
    public static final String REVERSE = "reverse";

    /** The sort state, initially unsorted. */
    private String state = UNSORTED;

    /** The property of the object being sorted that the sort is by. */
    private String sortProperty;

    /** Creates a new SortStateBean object. */
    public SortStateBean()
    {
    }

    /**
     * Creates a new SortStateBean object.
     *
     * @param state The initial sort state.
     */
    public SortStateBean(String state)
    {
        setState(state);
    }

    /**
     * Gets the sort state.
     *
     * @return The sort state.
     */
    public String getState()
    {
        return state;
    }

    /**
     * Sets the sort state.
     *
     * @param state The new sort state.
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * Gets the property being sorted by.
     *
     * @return The property being sorted by.
     */
    public String getSortProperty()
    {
        return sortProperty;
    }

    /**
     * Sets the property being sorted by.
     *
     * @param sortProperty The property that the sort is by.
     */
    public void setSortProperty(String sortProperty)
    {
        this.sortProperty = sortProperty;
    }
}
