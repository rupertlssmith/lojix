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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.ReferencableAttribute;
import com.thesett.aima.state.Type;
import com.thesett.common.error.NotImplementedException;

/**
 * IdAttributes are used to represent properties as integers that are symbols described by arbitrary objects. The number
 * of possible values that an IdAttribute can take on is expected to be reasonably large, a 32-bit integer is used to
 * encode them. The underlying objects to be encoded, must implement equals and hashCode correctly.
 *
 * <p/>Working with object comparisons is time consuming and this attribute class provides facilities for working
 * quickly with objects comparisons, particularly when it is known in advance what the set of objects is. The technique
 * is sometimes called 'interning', or just table lookups. An example might be a program language interpreter, where
 * functions and variables are given string names. Comparing these names by full string comparison will be slow and the
 * same string comparisons will be used often. So the strings are enumerated and can be quickly compared by their
 * enumerated values, using fast integer comparison. This class extends the notion of interning strings, over objects in
 * general.
 *
 * <p/>Objects also consume memory space, and encoding them as integers can provide a significant saving. The benefits
 * of this may be particularly felt in communication applications, where raw data is passed over a network. A compact
 * representation may enable the network protocol to run faster, by sending less data, and requiring less decoding on
 * the receiving end (provided the lookup table on the receiving end is up-to-date, of course). Again, the savings will
 * be best where the same set of objects are used repeatedly.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent a single object from a set of objects.
 * <tr><td> Convert objects to compact int representations.
 * <tr><td> Restore objects from compact representations.
 * <tr><td> Provide a factory for creating object attributes.
 * <tr><td> Close object sets into finite sets from extensible open sets.
 * <tr><td> Provide fast equality comparison on objects.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class IdAttribute<T> implements OrdinalAttribute, ReferencableAttribute, Serializable
{
    /** Used to hold all the different named attribute classes. */
    private static final Map<String, IdClassImpl> attributeClasses = new HashMap<String, IdClassImpl>();

    /** Used to hold the byte offset into the object array. */
    int value;

    /** Holds a reference to the class of object attribute that this one belongs to. */
    IdClassImpl<T> attributeClass;

    /**
     * Private constructor for an attribute. Attributes can only be created with a factory for an attribute class.
     *
     * @param value          The compact int index that is used to lookup the actual object value in the creating
     *                       factory instance.
     * @param attributeClass The factory implentation used to create this class of object attribute.
     */
    private IdAttribute(int value, IdClassImpl<T> attributeClass)
    {
        // Keep the int representation and the class.
        this.value = value;
        this.attributeClass = attributeClass;
    }

    /**
     * Generates a factory for building object attributes of the named class.
     *
     * @param  name The name of the object attribute class to get a factory for.
     * @param  <T>  The type of the attributes in the attribute class.
     *
     * @return The object attribute factory for the named object attribute class.
     */
    public static <T> IdAttributeFactory<T> getFactoryForClass(String name)
    {
        return IdClassImpl.getInstance(name);
    }

    /**
     * Returns the attribute type of this attribute.
     *
     * @return The attribute type of this attribute.
     */
    public Type<IdAttribute> getType()
    {
        return attributeClass;
    }

    /**
     * Returns an integer index for the current value of this attribute from 0 to num possible values where the number
     * of possible values is finite.
     *
     * @return An integer index for the current value of this attribute from 0 to num possible values where the number
     *         of possible values is finite.
     *
     * @throws com.thesett.aima.state.InfiniteValuesException If the set of values cannot be indexed because it is
     *                                                        infinite or cannot be ordered.
     */
    public int ordinal() throws InfiniteValuesException
    {
        return value;
    }

    /**
     * Returns the integer id of the attribute.
     *
     * @return The integer id of the attribute.
     */
    public long getId()
    {
        // Check if the attribute class has been finalized yet.
        if (attributeClass.finalized)
        {
            // Fetch the object value from the attribute class array of finalized values.
            return attributeClass.lookupValue[value].id;
        }

        // The attribute class has not been finalized yet.
        else
        {
            // Fetch the object value from the attribute class list of unfinalized values.
            return attributeClass.lookupValueList.get(value).id;
        }
    }

    /**
     * Sets the integer id of the attribute. If the attribute class is finalized this will change the value of this
     * attribute to that of the matched id, or raise an exception if no matching id exists. If the attribute class is
     * unfinalized this will change the id value of this attribute within the attribute class to the new id, provided
     * that the id has not already been assigned to another attribute value. If it has been assigned to another
     * attribute value then an exception is raised.
     *
     * @param  id The new id value.
     *
     * @throws IllegalArgumentException If the type is finalized but the id does not exist. Or if the type is
     *                                  unfinalized if the id has already been assigned to another instance of the type.
     */
    public void setId(long id)
    {
        // Find the enumeration node for this enumeration value.
        EnumerationNode<T> node = null;

        // Check if the attribute class has been finalized yet.
        if (attributeClass.finalized)
        {
            // Fetch the object value from the attribute class array of finalized values.
            node = attributeClass.lookupValue[value];
        }

        // The attribute class has not been finalized yet.
        else
        {
            // Fetch the object value from the attribute class list of unfinalized values.
            node = attributeClass.lookupValueList.get(value);
        }

        // Extract the id from it.
        long existingId = node.id;

        // Do nothing if the new id matches the existing one.
        if (id == existingId)
        {
            return;
        }

        // Check if the type is finalized.
        if (attributeClass.finalized)
        {
            // Raise an illegal argument exception if the id is not known.
            IdAttribute newValue = attributeClass.getAttributeFromId(id);

            // Otherwise, change the value of this attribute to that of the new id.
            this.value = newValue.value;
        }

        // The type is unfinalized.
        else
        {
            // Check if another instance of the type already has the id and raise an exception if so.
            EnumerationNode existingNode = attributeClass.idMap.get(id);

            if (existingNode != null)
            {
                throw new IllegalArgumentException("The id value, " + id +
                    ", cannot be set because another instance of this type with that " + "id already exists.");
            }

            // Assign it to this instance if the type is unfinalized. Also removing the old id mapping from the id
            // map and replacing it with the new one.
            node.id = id;
            attributeClass.idMap.remove(existingId);
            attributeClass.idMap.put(id, node);
        }
    }

    /**
     * Gets the object value of a object attribute.
     *
     * @return The underlying object value of this attribute.
     */
    public T getValue()
    {
        // Check if the attribute class has been finalized yet.
        if (attributeClass.finalized)
        {
            // Fetch the object value from the attribute class.
            return attributeClass.lookupValue[value].label;
        }
        else
        {
            return attributeClass.lookupValueList.get(value).label;
        }
    }

    /**
     * Sets the specified object as the value of this attribute. The value to set must be a legitimate member of this
     * attributes type, when the type has been finalized. If the type has yet to be finalized then the new value is
     * added to the set of possible values for the type.
     *
     * @param  value The new value to set.
     *
     * @throws IllegalArgumentException If the type has been finalized and the new value is not a member of the type.
     */
    public void setValue(T value) throws IllegalArgumentException
    {
        Integer b = attributeClass.lookupInt.get(value);

        // Check if the value is not already a memeber of the attribute class.
        if (b == null)
        {
            // Check if the attribute class has been finalized yet.
            if (attributeClass.finalized)
            {
                throw new IllegalArgumentException("The value to set, " + value +
                    ", is not already a member of the finalized IdType, " + attributeClass.attributeClassName + ".");
            }
            else
            {
                // Add the new value to the attribute class. Delegate to the factory to do this so that strings are
                // interned and so on.
                IdAttribute newAttribute = attributeClass.createIdAttribute(value);
                b = newAttribute.value;
            }
        }

        // Set the new value as the value of this attribute.
        this.value = b;
    }

    /**
     * Generates hashCodes unique to the value of the object value.
     *
     * @return A hashcode which is the hashcode for the object of this attributes value.
     */
    public int hashCode()
    {
        return getValue().hashCode();
    }

    /**
     * Tests if two object attributes are equal. They are equal if they have the same object value. They do not have to
     * be of the same object attribute class although the comparator must be a object attribute.
     *
     * @param  o The object to compare to.
     *
     * @return True if the comparator is a object attribute and has the same value as this one.
     */
    public boolean equals(Object o)
    {
        // The == operator can safely be used because the strings were interned.
        return getValue() == ((IdAttribute) o).getValue();
    }

    /**
     * Returns the string value.
     *
     * @return The string value.
     */
    public String toString()
    {
        return getValue().toString();
    }

    /**
     * Defines the type interface for object attributes.
     */
    public static interface IdType<T> extends Type<IdAttribute>
    {
    }

    /**
     * Defines a factory for creating object attributes of a given class.
     */
    public static interface IdAttributeFactory<T>
    {
        /**
         * Creates a object attribute of the class that this is a factory for. If the attribute class has been finalized
         * and the requested object value is not in the class then this should raise an exception.
         *
         * @param  value The value that the object attribute should have.
         *
         * @return A new object attribute with the specified value.
         */
        public IdAttribute<T> createIdAttribute(T value);

        /**
         * Converts a compact int representation of a object attribute into its object representation. If the int is not
         * valid then this should raise an exception.
         *
         * @param  b The compact int index that a object attribute should be created from.
         *
         * @return A object attribute looked up by its int index.
         */
        public IdAttribute<T> getAttributeFromInt(int b);

        /** Finalizes an attribute class. This prevents any new attribute value from being added to the class. */
        public void finalizeAttribute();

        /** Drops an attribute class. The attribute class is explicitly deleted. */
        public void dropAttributeClass();
    }

    /**
     * Defines the class of enumerated object attributes. Class = Type + Factory.
     */
    public static interface IdClass<T> extends IdType<T>, IdAttributeFactory<T>
    {
    }

    /**
     * Used to hold the lookup Object values and the number of possible values for a object attribute class.
     */
    private static class IdClassImpl<T> extends BaseType<IdAttribute> implements IdClass<T>
    {
        /** The number of possible values this attribute can take on. Defaults to infinity. */
        int numValues = -1;

        /** The Object lookup array for converting ints back into Strings for this class. */
        EnumerationNode<T>[] lookupValue;

        /** A map from Strings to ints for use when creating new attributes with strings that already exist. */
        Map<T, Integer> lookupInt = new HashMap<T, Integer>();

        /** Used to hold a collection of values for this attribute until the attribute is finalized. */
        List<EnumerationNode<T>> lookupValueList = new ArrayList<EnumerationNode<T>>();

        /**
         * Holds a map from referencable ids to enumeration nodes. This is used to resolve ids into instances of the
         * attribute type. It is a map rather than an array because the ids do not have to be sequentially assigned from
         * 0 but could be a scattered range of values.
         */
        Map<Long, EnumerationNode> idMap = new HashMap<Long, EnumerationNode>();

        /** Used to indicate whether the class has been finalized yet. */
        boolean finalized = false;

        /** Used to hold the name of the attribute class that this is a factory for. */
        String attributeClassName;

        /**
         * Builds a new factory for a given class.
         *
         * @param name The name that identifies this class of object attributes.
         */
        private IdClassImpl(String name)
        {
            // Keep a reference to the attribute class and name.
            this.attributeClassName = name;
        }

        /**
         * Gets an instance of this factory implementation for specified class of sting attribute. If the attribute
         * class is a new one, then a new factory instance is created, otherwise an existing factory for the class is
         * used.
         *
         * @param  name The name that identifies this class of object attributes.
         * @param  <T>  The type of the attributes in the attribute class.
         *
         * @return A factory implementation for the named object attribute class.
         */
        public static <T> IdAttributeFactory<T> getInstance(String name)
        {
            // Try to get the attribute class from the map of those that have already been created.
            IdClassImpl<T> attributeClass = attributeClasses.get(name);

            // Check if this is a new class and create it if so.
            if (attributeClass == null)
            {
                attributeClass = new IdClassImpl<T>(name);
                attributeClasses.put(name, attributeClass);
            }

            return attributeClass;
        }

        /**
         * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
         *
         * @return A new default instance of the type.
         */
        public IdAttribute<T> getDefaultInstance()
        {
            throw new NotImplementedException();
        }

        /**
         * Creates a object attribute of the class that this is a factory for.
         *
         * @param  value The object value to create an attribute instance for.
         *
         * @return A new object attribute with the specified value.
         *
         * @throws IllegalArgumentException If the type has been finalized but the value to create an attribute for if
         *                                  not a member of the type.
         */
        public IdAttribute<T> createIdAttribute(T value) throws IllegalArgumentException
        {
            // Check if the value is already in the attribute class.
            Integer i = lookupInt.get(value);

            if (i != null)
            {
                return new IdAttribute<T>(i, this);
            }

            // Check if the attribute class has been finalized yet.
            if (finalized)
            {
                // The value is not already in the attribute class and it is finalized so an attribute cannot be
                // created.
                // return null;
                throw new IllegalArgumentException("The value, " + value +
                    ", is not a member of the finalized IdAttribute class, " + attributeClassName + ".");
            }

            // The attribute class is not finalized yet so work with the list.
            else
            {
                // Work out what position in the list the new value will be at.
                int position = lookupValueList.size();

                // Create a new enumeration node to insert.
                EnumerationNode node = new EnumerationNode();
                node.label = value;
                node.value = position;
                node.id = node.value;

                // Add the new value at the end of the list.
                lookupValueList.add(node);

                // Also add the new value to the map of values.
                lookupInt.put(value, position);

                // Also add the new value to the id map.
                idMap.put(node.id, node);

                // Create a new object attribute from a int representation of the position.
                return new IdAttribute<T>(position, this);
            }
        }

        /**
         * Converts a compact int representation of a object attribute into its object representation. If the int is not
         * valid then this returns null.
         *
         * @param  b The compact int index that a object attribute should be created from.
         *
         * @return A object attribute looked up by its int index.
         */
        public IdAttribute<T> getAttributeFromInt(int b)
        {
            // Check if the attribute class has already been finalized in which case the int is only valid if it is
            // less than num possible values.
            if (finalized && (b >= numValues))
            {
                return null;
            }

            // If the attribute is not finalized the int is only valid if it is less than the attibute list size.
            else if (b >= lookupValueList.size())
            {
                return null;
            }

            return new IdAttribute<T>(b, this);
        }

        /**
         * Converts an id representation of an enumerated attribute into its object representation. If the id is not
         * valid then this will raise an exception.
         *
         * @param  id The id of the enumerated attribute value to fetch.
         *
         * @return An enumerated attribute looked up by its id.
         */
        public IdAttribute<T> getAttributeFromId(long id)
        {
            // The id is only valid if it is in the id map.
            EnumerationNode matchedNode = idMap.get(id);

            // Check that the value exists.
            if (matchedNode == null)
            {
                throw new IllegalArgumentException("The enumerated attribute cannot be generated from the id, " + id +
                    ", because that does not represent an existing value.");
            }

            return new IdAttribute<T>(matchedNode.value, this);
        }

        /** Finalizes an attribute class. This prevents any new attribute value from being added to the class. */
        public void finalizeAttribute()
        {
            // Check if the attribute class has already been finalized.
            if (finalized)
            {
                // Its already been finalized. Do nothing.
                return;
            }

            // Count how many values there are and set this as the possible value count.
            int count = lookupValueList.size();

            numValues = count;

            // Convert the List of values into an array and set the List reference to null.
            lookupValue = lookupValueList.toArray(new EnumerationNode[count]);
            lookupValueList = null;

            // Set the finalized flag.
            finalized = true;
        }

        /** Drops an attribute class. The attribute class is explicitly deleted. */
        public void dropAttributeClass()
        {
            attributeClasses.remove(attributeClassName);
        }

        /**
         * Returns the name of this object attribute class.
         *
         * @return The name of this object attribute class.
         */
        public String getName()
        {
            return attributeClassName;
        }

        /**
         * Gets the underlying Java class that implements the type.
         *
         * @return The underlying Java class that implements the type.
         */
        public Class<IdAttribute> getBaseClass()
        {
            return IdAttribute.class;
        }

        /** {@inheritDoc} */
        public String getBaseClassName()
        {
            return getBaseClass().getName();
        }

        /**
         * Gets the number of possible values that attributes of this object class can take on.
         *
         * @return the number of possible values that an instance of this attribute can take on. If the value is -1 then
         *         this is to be interpreted as infinity.
         */
        public int getNumPossibleValues()
        {
            // Fetch the number of possible values that this attribute class can take.
            return numValues;
        }

        /**
         * Returns all the different values that a object attribute of this class can take on. If the class is finalized
         * this will list all the possible strings in the class. If the class is not yet finalized the set of strings is
         * still open and potentially infinite so this will throw an infinite values exception.
         *
         * @return All the objects that this class of object attribute can take on.
         *
         * @throws InfiniteValuesException If the set of values cannot be listed because the object attribute class is
         *                                 unfinalized.
         */
        public Set<IdAttribute> getAllPossibleValuesSet() throws InfiniteValuesException
        {
            // Check if the attribute class is not yet finalized so an infinte values exception must be thrown.
            if (!finalized)
            {
                throw new InfiniteValuesException("The object attribute class is not finalized yet, " +
                    "so can have infinite values.", null);
            }

            // The attribute class is finalized so return the array of possible values as a set.
            else
            {
                Set<IdAttribute> result = new HashSet<IdAttribute>();

                for (int i = 0; i < numValues; i++)
                {
                    result.add(createIdAttribute(lookupValue[i].label));
                }

                return result;
            }
        }

        /**
         * Returns an iterator over all the different values that a object attribute of this class can take on. If the
         * class is finalized this will list all the possible strings in the class. If the class is not yet finalized
         * the set of strings is still open and potentially infinite so this will throw an infinite values exception.
         *
         * @return An iterator over all the object that this class of object attribute can take on.
         *
         * @throws InfiniteValuesException If the set of values cannot be listed because the object attribute class is
         *                                 unfinalized.
         */
        public Iterator<IdAttribute> getAllPossibleValuesIterator() throws InfiniteValuesException
        {
            return getAllPossibleValuesSet().iterator();
        }
    }

    /**
     * An EnumerationNode captures all the relevant information about a label within an enumeration; its label, its
     * enumerated compact int representation, its referenceable id value.
     */
    private static class EnumerationNode<T> implements Serializable
    {
        /** The label of the node. */
        public T label;

        /** The compact int value of the node. */
        public int value;

        /** The referencable id of the value. */
        public long id;

        /**
         * Outputs all values as a string for debugging.
         *
         * @return All values as a string for debugging.
         */
        public String toString()
        {
            return "IdAttribute: [ label = " + label + ", value = " + value + ", id = " + id + "]";
        }
    }
}
