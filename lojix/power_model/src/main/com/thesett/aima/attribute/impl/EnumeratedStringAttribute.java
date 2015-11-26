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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.ReferencableAttribute;
import com.thesett.aima.state.Type;
import com.thesett.aima.state.TypeVisitor;
import com.thesett.aima.state.restriction.TypeRestriction;
import com.thesett.aima.state.restriction.ValidEnumRestriction;
import com.thesett.common.error.NotImplementedException;

/**
 * EnumeratedStringAttributes are used to represent properties that are symbols described by strings. The number of
 * possible values that an EnumeratedStringAttribute can take on is expected to be small. Typically less than ten. The
 * number of supported data points using this attribute that can be held in memory at one time is to be as large as
 * possible. Data sets in the hundreds of megabytes should be possible.
 *
 * <p>Working with string comparisons is time consuming and this attribute class provides facilities for working quickly
 * with strings when it is known in advance that only a limited set of string will be used to describe a given
 * attribute.
 *
 * <p>Storing strings can require a large amount of space. For example, if we know that a given attribute will only take
 * on the values 'Red', 'Green' and 'Blue' and the data for this attribute is to be loaded from a large text data file
 * it would not be efficient to create new String objects for every value. The strings are interned using
 * {@link java.lang.String#intern} to store them efficiently.
 *
 * <p>The number of supported data points using this attribute that can be held in memory at one time is to be as large
 * as possible. A string attribute is described using a single byte and there are methods to convert between a byte and
 * an EnumeratedStringAttribute and back again so that algorithms using StringAttributes can work with arrays of bytes
 * to eliminate the overhead of using an object representation. Typically, an object is 8 bytes, on 8 byte alignments,
 * so storing the object representation takes 13 (8 for object, 1 for the byte, 4 for the ref to the attribute class)
 * rounded up to 16 bytes, whereas the byte representation takes just 1.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent a single string from a set of strings.
 * <tr><td> Convert string to compact byte representation.
 * <tr><td> Restore string from compact representation.
 * <tr><td> Provide factory for creating string attributes.
 * <tr><td> Close string set into finite set from extensible open set.
 * <tr><td> Provide fast equality on strings.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnumeratedStringAttribute implements OrdinalAttribute, ReferencableAttribute, Serializable
{
    /** Used to hold all the different named attribute classes. */
    private static final Map<String, EnumeratedStringClassImpl> attributeClasses =
        new HashMap<String, EnumeratedStringClassImpl>();

    /** Used to hold the byte offset into the string array. */
    byte value;

    /** Holds a reference to the class of string attribute that this one belongs to. */
    EnumeratedStringClassImpl attributeClass;

    /**
     * Private constructor for an attribute. Attributes can only be created with a factory for an attribute class.
     *
     * @param value          The compact byte index that is used to lookup the actual string value in the creating
     *                       factory instance.
     * @param attributeClass The factory implentation used to create this class of string attribute.
     */
    private EnumeratedStringAttribute(byte value, EnumeratedStringClassImpl attributeClass)
    {
        // Keep the byte representation and the class.
        this.value = value;
        this.attributeClass = attributeClass;
    }

    /**
     * Generates a factory for building string attributes of the named class.
     *
     * @param  name The name of the string attribute class to get a factory for.
     *
     * @return The string attribute factory for the named string attribute class.
     */
    public static EnumeratedStringAttributeFactory getFactoryForClass(String name)
    {
        return EnumeratedStringClassImpl.getInstance(name);
    }

    /**
     * Returns the attribute type of this attribute.
     *
     * @return The attribute type of this attribute.
     */
    public Type<EnumeratedStringAttribute> getType()
    {
        return attributeClass;
    }

    /**
     * Converts the object representation into a compact byte representation.
     *
     * @return The compact byte representation of this attribute.
     */
    public byte getByteFromAttribute()
    {
        // Just return the byte representation that has already been found.
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
            // Fetch the string value from the attribute class array of finalized values.
            return attributeClass.lookupValue[value].id;
        }

        // The attribute class has not been finalized yet.
        else
        {
            // Fetch the string value from the attribute class list of unfinalized values.
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
        EnumerationNode node = null;

        // Check if the attribute class has been finalized yet.
        if (attributeClass.finalized)
        {
            // Fetch the string value from the attribute class array of finalized values.
            node = attributeClass.lookupValue[value];
        }
        else
        {
            // The attribute class has not been finalized yet.
            // Fetch the string value from the attribute class list of unfinalized values.
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
            EnumeratedStringAttribute newValue = attributeClass.getAttributeFromId(id);

            // Otherwise, change the value of this attribute to that of the new id.
            this.value = newValue.value;
        }
        else
        {
            // The type is un-finalized.
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
     * Gets the string value of a string attribute.
     *
     * @return The underlying string value of this attribute.
     */
    public String getStringValue()
    {
        // Check if the attribute class has been finalized yet.
        if (attributeClass.finalized)
        {
            // Fetch the string value from the attribute class.
            return attributeClass.lookupValue[value].label;
        }
        else
        {
            return attributeClass.lookupValueList.get(value).label;
        }
    }

    /**
     * Sets the specified string as the value of this attribute. The value to set must be a legitimate member of this
     * attributes type, when the type has been finalized. If the type has yet to be finalized then the new value is
     * added to the set of possible values for the type.
     *
     * @param  value The new value to set.
     *
     * @throws IllegalArgumentException If the type has been finalized and the new value is not a member of the type.
     */
    public void setStringValue(String value) throws IllegalArgumentException
    {
        Byte b = attributeClass.lookupByte.get(value);

        // Check if the value is not already a memeber of the attribute class.
        if (b == null)
        {
            // Check if the attribute class has been finalized yet.
            if (attributeClass.finalized)
            {
                throw new IllegalArgumentException("The value to set, " + value +
                    ", is not already a member of the finalized EnumeratedStringType, " +
                    attributeClass.attributeClassName + ".");
            }
            else
            {
                // Add the new value to the attribute class. Delegate to the factory to do this so that strings are
                // interned and so on.
                EnumeratedStringAttribute newAttribute = attributeClass.createStringAttribute(value);
                b = newAttribute.value;
            }
        }

        // Set the new value as the value of this attribute.
        this.value = b;
    }

    /**
     * Generates hashCodes unique to the value of the string value.
     *
     * @return A hashcode which is the java.lang.String hashcode for the string value of this atttributes value.
     */
    public int hashCode()
    {
        return getStringValue().hashCode();
    }

    /**
     * Tests if two string attributes are equal. They are equal if they have the same string value. They do no have to
     * be of the same string attribute class although the comparator must be a string attribute.
     *
     * @param  o The object to compare to.
     *
     * @return True if the comparator is a string attribute and has the same value as this one.
     */
    public boolean equals(Object o)
    {
        // return getValue().equals(((EnumeratedStringAttribute)o).getValue());
        // The == operator can safely be used because the strings were interned.
        return (o instanceof EnumeratedStringAttribute) &&
            (getStringValue() == ((EnumeratedStringAttribute) o).getStringValue()) &&
            ((EnumeratedStringAttribute) o).attributeClass.attributeClassName.equals(attributeClass.attributeClassName);
    }

    /**
     * Should return an integer index for the current value of this attribute from 0 to num possible values where the
     * number of possible values is finite.
     *
     * @return An integer index for the current value of this attribute from 0 to num possible values of this string
     *         attribute class.
     */
    public int ordinal()
    {
        return (int) value;
    }

    /**
     * Returns the string value.
     *
     * @return The string value.
     */
    public String toString()
    {
        return getStringValue();
    }

    /**
     * Defines the type interface for string attributes.
     */
    public static interface EnumeratedStringType extends Type<EnumeratedStringAttribute>
    {
        /**
         * Returns all the different values that an OrdinalAttribute of this type can take on as an iterator over these
         * values. The hierarchy forms a tree the leaves of which are values that it can take on. The iterator returns
         * the leaves 'in-order'.
         *
         * @param  failOnNonFinalized <tt>true</tt> if this should throw an infinite values exception when the type is
         *                            not finalized, <tt>false</tt> if it should list all values defined so far anyway.
         *
         * @return An iterator over the set of attributes defining the possible value set for this attribute.
         */
        public Iterator<EnumeratedStringAttribute> getAllPossibleValuesIterator(boolean failOnNonFinalized);

        /**
         * Returns all the different values that an OrdinalAttribute of this type can take on.
         *
         * @param  failOnNonFinalized <tt>true</tt> if this should throw an infinite values exception when the type is
         *                            not finalized, <tt>false</tt> if it should list all values defined so far anyway.
         *
         * @return A set of attributes defining the possible value set for this attribute.
         */
        public Set<EnumeratedStringAttribute> getAllPossibleValuesSet(boolean failOnNonFinalized);

        /**
         * Provides a map of all possible values, keyed by their string values.
         *
         * @param  failOnNonFinalized <tt>true</tt> if this should throw an infinite values exception when the type is
         *                            not finalized, <tt>false</tt> if it should list all values defined so far anyway.
         *
         * @return A map of all possible values, keyed by their string values.
         */
        public Map<String, EnumeratedStringAttribute> getAllPossibleValuesMap(boolean failOnNonFinalized);
    }

    /**
     * Defines a factory for creating string attributes of a given class.
     */
    public static interface EnumeratedStringAttributeFactory
    {
        /**
         * Gets the type for this factory.
         *
         * @return The type for this factory.
         */
        public EnumeratedStringType getType();

        /**
         * Creates a string attribute of the class that this is a factory for. If the attribute class has been finalized
         * and the requested string value is not in the class then this should raise an exception.
         *
         * @param  value The value that the string attribute should have.
         *
         * @return A new string attribute with the specified value.
         */
        public EnumeratedStringAttribute createStringAttribute(String value);

        /**
         * Converts a compact byte representation of a string attribute into its object representation. If the byte is
         * not valid then this should raise an exception.
         *
         * @param  b The compact byte index that a string attribute should be created from.
         *
         * @return A string attribute looked up by its byte index.
         */
        public EnumeratedStringAttribute getAttributeFromByte(byte b);

        /**
         * Looks up an attribute by its id.
         *
         * @param  id The id of the attribute to look up.
         *
         * @return A string attribute looked up by its id.
         */
        public EnumeratedStringAttribute getAttributeFromId(long id);

        /** Finalizes an attribute class. This prevents any new attribute value from being added to the class. */
        public void finalizeAttribute();

        /** Drops an attribute class. The attribute class is explicitly deleted. */
        public void dropAttributeClass();
    }

    /**
     * Defines the class of enumerated string attributes. Class = Type + Factory.
     */
    public static interface EnumeratedStringClass extends EnumeratedStringType, EnumeratedStringAttributeFactory
    {
    }

    /**
     * Used to hold the lookup String values and the number of possible values for a string attribute class.
     */
    private static class EnumeratedStringClassImpl extends BaseType<EnumeratedStringAttribute>
        implements EnumeratedStringClass
    {
        /** The number of possible values this attribute can take on. Defaults to infinity. */
        int numValues = -1;

        /** The String lookup array for converting bytes back into Strings for this class. */
        EnumerationNode[] lookupValue;

        /**
         * A map from Strings to bytes for use when creating new attributes with strings that already exist. It is safe
         * to use an IdentityHashMap here because all the String keys will have been interned.
         */
        Map<String, Byte> lookupByte = new IdentityHashMap<String, Byte>();

        /** Used to hold a collection of values for this attribute until the attribute is finalized. */
        List<EnumerationNode> lookupValueList = new ArrayList<EnumerationNode>();

        /**
         * Holds a map from referencable ids to enumeration nodes. This is used to resolve ids into instances of the
         * attribute type. It is a map rather than an array because the ids do not have to be sequentially assigned from
         * 0 but could be a scattered range of values.
         */
        Map<Long, EnumerationNode> idMap = new HashMap<Long, EnumerationNode>();

        /** Used to indicate whether the class has been finalized yet. */
        boolean finalized;

        /** Used to hold the name of the attribute class that this is a factory for. */
        String attributeClassName;

        /**
         * Builds a new factory for a given class.
         *
         * @param name The name that identifies this class of string attributes.
         */
        private EnumeratedStringClassImpl(String name)
        {
            // Keep a reference to the attribute class and name.
            this.attributeClassName = name;

            restrictions = new LinkedList<TypeRestriction>();
            restrictions.add(new ValidEnumRestriction(name));
        }

        /**
         * Gets an instance of this factory implementation for specified class of sting attribute. If the attribute
         * class is a new one, then a new factory instance is created, otherwise an existing factory for the class is
         * used.
         *
         * @param  name The name that identifies this class of string attributes.
         *
         * @return A factory implementation for the named string attribute class.
         */
        public static EnumeratedStringAttributeFactory getInstance(String name)
        {
            // Try to get the attribute class from the map of those that have already been created.
            EnumeratedStringClassImpl attributeClass = attributeClasses.get(name);

            // Check if this is a new class and create it if so.
            if (attributeClass == null)
            {
                attributeClass = new EnumeratedStringClassImpl(name);
                attributeClasses.put(name, attributeClass);
            }

            return attributeClass;
        }

        /**
         * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
         *
         * @return A new default instance of the type. Always <tt>false</tt>.
         */
        public EnumeratedStringAttribute getDefaultInstance()
        {
            throw new NotImplementedException();
        }

        /** {@inheritDoc} */
        public EnumeratedStringType getType()
        {
            return this;
        }

        /**
         * Creates a string attribute of the class that this is a factory for.
         *
         * @param  value The string value to create an attribute instance for.
         *
         * @return A new string attribute with the specified value.
         *
         * @throws IllegalArgumentException If the type has been finalized but the value to create an attribute for if
         *                                  not a member of the type.
         */
        public EnumeratedStringAttribute createStringAttribute(String value) throws IllegalArgumentException
        {
            // Intern the string to ensure memory is not wasted on duplicate strings and that fast string equality can
            // be used safely.
            String internValue = value.intern();

            // Check if the value is already in the attribute class.
            Byte b = lookupByte.get(internValue);

            if (b != null)
            {
                return new EnumeratedStringAttribute(b, this);
            }

            // Check if the attribute class has been finalized yet.
            if (finalized)
            {
                // The value is not already in the attribute class and it is finalized so an attribute cannot be
                // created.
                // return null;
                throw new IllegalArgumentException("The value, " + value +
                    ", is not a member of the finalized EnumeratedStringAttribute class, " + attributeClassName + ".");
            }
            else
            {
                // The attribute class is not finalized yet so work with the list.
                // Work out what position in the list the new value will be at.
                int position = lookupValueList.size();

                // Create a new enumeration node to insert.
                EnumerationNode node = new EnumerationNode();
                node.label = internValue;
                node.value = (byte) position;
                //node.id = node.value;

                // Add the new value at the end of the list.
                lookupValueList.add(node);

                // Also add the new value to the map of values.
                lookupByte.put(internValue, (byte) position);

                // Also add the new value to the id map.
                //idMap.put(node.id, node);

                // Create a new string attribute from a byte representation of the position.
                return new EnumeratedStringAttribute((byte) position, this);
            }
        }

        /**
         * Converts a compact byte representation of a string attribute into its object representation. If the byte is
         * not valid then this returns null.
         *
         * @param  b The compact byte index that a string attribute should be created from.
         *
         * @return A string attribute looked up by its byte index.
         */
        public EnumeratedStringAttribute getAttributeFromByte(byte b)
        {
            // Check if the attribute class has already been finalized in which case the byte is only valid if it is
            // less than num possible values.
            if (finalized && (b >= numValues))
            {
                throw new IllegalArgumentException("The specified enum element cannot be generated from the " +
                    "compact representation, " + b + ", because that does not represent an existing value.");
            }

            // If the attribute is not finalized the byte is only valid if it is less than the attribute list size.
            else if (b >= lookupValueList.size())
            {
                throw new IllegalArgumentException("The specified enum element cannot be generated from the " +
                    "compact representation, " + b + ", because that does not represent an existing value.");
            }

            return new EnumeratedStringAttribute(b, this);
        }

        /**
         * Converts an id representation of an enumerated attribute into its object representation. If the id is not
         * valid then this will raise an exception.
         *
         * @param  id The id of the enumerated attribute value to fetch.
         *
         * @return An enumerated attribute looked up by its id.
         */
        public EnumeratedStringAttribute getAttributeFromId(long id)
        {
            // The id is only valid if it is in the id map.
            EnumerationNode matchedNode = idMap.get(id);

            // Check that the value exists.
            if (matchedNode == null)
            {
                throw new IllegalArgumentException("The enumerated attribute cannot be generated from the id, " + id +
                    ", because that does not represent an existing value.");
            }

            return new EnumeratedStringAttribute(matchedNode.value, this);
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
         * Returns the name of this string attribute class.
         *
         * @return The name of this string attribute class.
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
        public Class<EnumeratedStringAttribute> getBaseClass()
        {
            return EnumeratedStringAttribute.class;
        }

        /** {@inheritDoc} */
        public String getBaseClassName()
        {
            return getBaseClass().getName();
        }

        /**
         * Gets the number of possible values that attributes of this string class can take on.
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
         * Returns all the different values that an OrdinalAttribute of this type can take on.
         *
         * @return A set of attributes defining the possible value set for this attribute.
         */
        public Set<EnumeratedStringAttribute> getAllPossibleValuesSet()
        {
            return getAllPossibleValuesSet(true);
        }

        /**
         * Returns all the different values that a string attribute of this class can take on. If the class is finalized
         * this will list all the possible strings in the class. If the class is not yet finalized the set of strings is
         * still open and potentially infinite and this will throw an infinite values exception if the fail flag is set.
         * Clearing the fail flag allows all non-finalized values to be listed.
         *
         * @param  failOnNonFinalized <tt>true</tt> to fail if the type is not yet finalized, and therefore potentially
         *                            infinite.
         *
         * @return All the string that this class of string attribute can take on.
         *
         * @throws InfiniteValuesException If the set of values cannot be listed because the string attribute class is
         *                                 unfinalized.
         */
        public Set<EnumeratedStringAttribute> getAllPossibleValuesSet(boolean failOnNonFinalized)
        {
            // Check if the attribute class is not yet finalized so an infinte values exception must be thrown.
            if (!finalized && failOnNonFinalized)
            {
                throw new InfiniteValuesException("The string attribute class is not finalized yet, " +
                    "so can have infinite values.", null);
            }

            // The attribute class is finalized so return the array of possible values as a set.
            else
            {
                Set<EnumeratedStringAttribute> result = new HashSet<EnumeratedStringAttribute>();

                if (finalized)
                {
                    for (EnumerationNode node : lookupValue)
                    {
                        result.add(createStringAttribute(node.label));
                    }
                }
                else
                {
                    for (EnumerationNode node : lookupValueList)
                    {
                        result.add(createStringAttribute(node.label));
                    }
                }

                return result;
            }
        }

        /** {@inheritDoc} */
        public Map<String, EnumeratedStringAttribute> getAllPossibleValuesMap(boolean failOnNonFinalized)
        {
            // Get the set of already existing values, so that they will not be created again.
            Iterator<EnumeratedStringAttribute> allPossibleValuesIterator = getAllPossibleValuesIterator(false);
            Map<String, EnumeratedStringAttribute> existingValues = new HashMap<String, EnumeratedStringAttribute>();

            for (; allPossibleValuesIterator.hasNext();)
            {
                EnumeratedStringAttribute existingAttribute = allPossibleValuesIterator.next();
                existingValues.put(existingAttribute.getStringValue(), existingAttribute);
            }

            return Collections.unmodifiableMap(existingValues);
        }

        /**
         * Returns an iterator over all the different values that a string attribute of this class can take on. If the
         * class is finalized this will list all the possible strings in the class. If the class is not yet finalized
         * the set of strings is still open and potentially infinite so this will throw an infinite values exception.
         *
         * @return An iterator over all the string that this class of string attribute can take on.
         *
         * @throws InfiniteValuesException If the set of values cannot be listed because the string attribute class is
         *                                 unfinalized.
         */
        public Iterator<EnumeratedStringAttribute> getAllPossibleValuesIterator() throws InfiniteValuesException
        {
            return getAllPossibleValuesSet().iterator();
        }

        /** {@inheritDoc} */
        public Iterator<EnumeratedStringAttribute> getAllPossibleValuesIterator(boolean failOnNonFinalized)
        {
            return getAllPossibleValuesSet(failOnNonFinalized).iterator();
        }

        /** {@inheritDoc} */
        public void acceptVisitor(TypeVisitor visitor)
        {
            if (visitor instanceof EnumeratedStringTypeVisitor)
            {
                ((EnumeratedStringTypeVisitor) visitor).visit(this);
            }
            else
            {
                super.acceptVisitor(visitor);
            }
        }
    }

    /**
     * An EnumerationNode captures all the relevant information about a label within an enumeration; its label, its
     * enumerated compact byte representation, its referenceable id value.
     */
    private static class EnumerationNode implements Serializable
    {
        /** The label of the node. */
        public String label;

        /** The compact byte value of the node. */
        public byte value;

        /** The referencable id of the value. */
        public long id = -1L;

        /**
         * Outputs all values as a string for debugging.
         *
         * @return All values as a string for debugging.
         */
        public String toString()
        {
            return "label = " + label + ": value = " + value + ", id = " + id;
        }
    }
}
