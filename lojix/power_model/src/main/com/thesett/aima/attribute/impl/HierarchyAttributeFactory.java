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
package com.thesett.aima.attribute.impl;

/**
 * Defines a factory for creating hierarchy attributes of a given type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Define level names for levels in a hierarchy.
 * <tr><td> Create instances of hierarchy attributes from a defined path name.
 * <tr><td> Convert an interned ordinal representation of a value into an instance of the value.
 * <tr><td> Count the maximum number of levels in the hierarchy.
 * <tr><td> Finalize the hierarchy type so that no new legal values of it can be created.
 * <tr><td> Delete a hierarchy type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface HierarchyAttributeFactory
{
    /**
     * Gets the hierarchy attribute type for this factory.
     *
     * @return The hierarchy attribute type for this factory.
     */
    HierarchyType getType();

    /**
     * Sets up the level names of the hierarchy.
     *
     * @param levels The level names.
     */
    void setLevelNames(String[] levels);

    /**
     * Gets the level names.
     *
     * @return The level names.
     */
    String[] getLevelNames();

    /**
     * Creates a hierarchy attribute of the class that this is a factory for. If the attribute class has been finalized
     * and the requested value is not in the class then this should raise an exception.
     *
     * @param  value The value that the hierarchy attribute should have.
     *
     * @return A new hierarchy attribute with the specified value.
     */
    HierarchyAttribute createHierarchyAttribute(String[] value);

    /**
     * Creates a hierarchy attribute of the class that this is a factory for. This method differs from the
     * {@link #createHierarchyAttribute} method in that it can create non-allowable hierarchy attributes. This method
     * will only create hierarchy attributes for values that already exist in the hierarchy class, whether or not it has
     * been finalized. Its purpose is to create any path within a hierarchy attribute for equality or sub-type
     * comparision with other hierarchy attributes.
     *
     * @param  value The value that the hierarchy attribute should have.
     *
     * @return A new hierarchy attribute with the specified value.
     */
    HierarchyAttribute createHierarchyAttributeForComparison(String[] value);

    /**
     * Converts a compact int representation of a hierarchy attribute into its object representation. If the int is not
     * valid then this should raise an exception.
     *
     * @param  i The compact int index that a hierarchy attribute should be created from.
     *
     * @return A hierarchy attribute looked up by its in index.
     */
    HierarchyAttribute getAttributeFromInt(int i);

    /**
     * Looks up an attribute by its id.
     *
     * @param  id The id of the attribute to look up.
     *
     * @return A string attribute looked up by its id.
     */
    HierarchyAttribute getAttributeFromId(long id);

    /**
     * Reports the maximum number of levels that this hierarchy attribute type contains.
     *
     * @return The maximum number of levels that this hierarchy attribute type contains.
     */
    int getMaxLevels();

    /** Finalizes an attribute class. This prevents any new attribute value from being added to the class. */
    void finalizeAttribute();

    /** Drops an attribute class. The attribute class is explicitly deleted. */
    void dropAttributeClass();

    /**
     * Returns a name that uniquely identifies this hierarchy attribute type.
     *
     * @return The name of the hierarchy attribute type.
     */
    String getName();
}
