/*
 * Copyright The Sett Ltd.
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

import java.util.Iterator;
import java.util.Set;

import com.thesett.aima.state.Type;

/**
 * Defines the type interface for hierarchy attributes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Supply the name of the type.
 * <tr><td> Supply the Java class for the type.
 * <tr><td> Report how many different values instances of the type can take on.
 * <tr><td> Supply all the different values that instances of the type can take on, where there are a finite number.
 * <tr><td> Supply all values of the hierarchy type at a given level.
 * <tr><td> Supply all values of the hierarchy type that are sub-nodes of a given value.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface HierarchyType extends Type<HierarchyAttribute>
{
    /**
     * Gets the level names.
     *
     * @return The level names.
     */
    public String[] getLevelNames();

    /**
     * Generates an iterator over all the values (not just the allowable ones) in the hierarchy type, at the named
     * level.
     *
     * @param  level The name of the level to list.
     *
     * @return An iterator over all the values (not just the allowable ones) in the hierarchy type, at the named level.
     */
    public Iterator<HierarchyAttribute> getValuesAtLevelIterator(String level);

    /**
     * Generates an iterator over all the values (not just the allowable ones) in the hierarchy type that are
     * sub-hierarchies of the specified value, at the named level.
     *
     * @param  parent The parent hierarchy to list the siblings of.
     * @param  level  The name of the level to list.
     *
     * @return An iterator over all the values (not just the allowable ones) in the hierarchy type, at the named level.
     */
    public Iterator<HierarchyAttribute> getSubHierarchyValuesIterator(HierarchyAttribute parent, String level);

    /**
     * Generates an set over all the values (not just the allowable ones) in the hierarchy type, at the named level.
     *
     * @param  level The name of the level to list.
     *
     * @return An set over all the values (not just the allowable ones) in the hierarchy type, at the named level.
     */
    public Set<HierarchyAttribute> getValuesAtLevelSet(String level);

    /**
     * Generates an set over all the values (not just the allowable ones) in the hierarchy type that are sub-hierarchies
     * of the specified value, at the named level.
     *
     * @param  parent The parent hierarchy to list the siblings of.
     * @param  level  The name of the level to list.
     *
     * @return An set over all the values (not just the allowable ones) in the hierarchy type, at the named level.
     */
    public Set<HierarchyAttribute> getSubHierarchyValuesSet(HierarchyAttribute parent, String level);

    /**
     * Returns all the different values that an OrdinalAttribute of this type can take on as an iterator over these
     * values. The hierarchy forms a tree the leaves of which are values that it can take on. The iterator returns the
     * leaves 'in-order'.
     *
     * @param  failOnNonFinalized <tt>true</tt> if this should throw an infinite values exception when the type is not
     *                            finalized, <tt>false</tt> if it should list all values defined so far anyway.
     *
     * @return An iterator over the set of attributes defining the possible value set for this attribute.
     */
    public Iterator<HierarchyAttribute> getAllPossibleValuesIterator(boolean failOnNonFinalized);

    /**
     * Returns all the different values that an OrdinalAttribute of this type can take on.
     *
     * @param  failOnNonFinalized <tt>true</tt> if this should throw an infinite values exception when the type is not
     *                            finalized, <tt>false</tt> if it should list all values defined so far anyway.
     *
     * @return A set of attributes defining the possible value set for this attribute.
     */
    public Set<HierarchyAttribute> getAllPossibleValuesSet(boolean failOnNonFinalized);
}
