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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.thesett.aima.search.QueueBasedSearchMethod;
import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.util.TreeSearchState;
import com.thesett.aima.search.util.uninformed.DepthBoundedSearch;
import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.ReferencableAttribute;
import com.thesett.aima.state.Type;
import com.thesett.aima.state.TypeVisitor;
import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.SimpleTree;
import com.thesett.common.util.Tree;
import com.thesett.common.util.logic.TrueUnaryPredicate;
import com.thesett.common.util.logic.UnaryPredicate;
import com.thesett.common.validate.Validation;

/**
 * HierarchyAttributes are properties that are instances of a path in a tree structured categorisation. An example is
 * the categorisation of life forms, the root is "all life", the next level splits into "plant", "animal", "bacteria",
 * "virus", at the next level "animal" splits into "mamal", "reptile", "fish", ... and so on. Actual instances of life
 * forms fit into this tree at appropriate places and the tree forms a way of selecting sub-categories of life forms at
 * different levels. Broader sub-categories exist further up the tree and more fine grained sub-categories are formed
 * further down. An individual instance of a life form categorisation is a path from the root of the tree down to the
 * appropriate sub-category.
 *
 * <p/>In the life forms example, instances of life forms fit into the tree but only at certain allowable places. For
 * example all animals fit into one of the "animal" sub categories, so no animal can be placed into the tree directly at
 * the "animal" category level. Often only the leaves of a hierarchy tree will be allowable instances but in some cases
 * non-leaf categories may be allowable too. The number of possible values that an hierarchy attribute of a given type
 * can take on is always the number of allowable categories in the type, not the total number of sub-categories and not
 * the number of leaves in the tree.
 *
 * <p/>Often hierarchies are balanced in the number of levels accross all instances; that is to say that the path
 * distance from the root to each allowable leaf is the same for each leaf. This does not have to be the case, however.
 *
 * <p/>The category labels throughout the hierarchy tree will always be strings. The implementation enumerates these
 * strings and uses a more compact representation internally and provides faster equality checks between attribute
 * instances than would be obtained by doing string by string comparisons.
 *
 * <p/>In addition to equality checks this data type also provides sub-category checks to test if one category is a
 * legitimate sub-category of another.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent a single allowable sub-category from a tree of hierarchical categories.
 * <tr><td> Convert a sub-category to a compact representation.
 * <tr><td> Restore a sub-category from a compact representation.
 * <tr><td> Provide a factory for creating hierarchy attributes.
 * <tr><td> Provide fast equality checking on sub-categories.
 * <tr><td> Provide fast sub-category checking.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class HierarchyAttribute implements OrdinalAttribute, ReferencableAttribute, Serializable
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(HierarchyAttribute.class.getName()); */

    /** Used to hold all the different named attribute classes. */
    private static Map<String, HierarchyClassImpl> attributeClasses = new HashMap<String, HierarchyClassImpl>();

    /**
     * Used to hold the compact int representation of the hierarchy attribute. The special value of -1 is used to
     * indicate that this attribute has not had any value assigned to it yet. Other negative values are used to
     * represent partial assignments that have not yet been completed.
     */
    int value;

    /** Holds a reference to the class of hierarchy attribute that this one belongs to. */
    HierarchyClassImpl attributeClass;

    /**
     * Private constructor for a hierarchy attribute. Attributes can only be created with the factory.
     *
     * @param value          The compact integer index that is used to lookup the actual hierarchy value in the creating
     *                       factory.
     * @param attributeClass The factory implementation used to create this class of hierarchy attribute.
     */
    private HierarchyAttribute(int value, HierarchyClassImpl attributeClass)
    {
        // Keep the compact representation and the class.
        this.value = value;
        this.attributeClass = attributeClass;
    }

    /**
     * Generates a factory for building hierarchy attributes of the named class.
     *
     * @param  name The name of the hierarchy attribute class to get a factory for.
     *
     * @return The hierarchy attribute factory for the named hierarchy attribute class.
     */
    public static HierarchyAttributeFactory getFactoryForClass(String name)
    {
        return HierarchyClassImpl.getInstance(name);
    }

    /**
     * Gets the {@link com.thesett.aima.state.Type} for a named class of hierarchy attributes.
     *
     * @param  name The name of the hierarchy attribute class to get the type for.
     *
     * @return The hierarchy attribute type for the named hierarchy attribute class.
     */
    public static HierarchyType getTypeForClass(String name)
    {
        return HierarchyClassImpl.getInstance(name);
    }

    /**
     * Should return a correct instance of the type class for this attribute.
     *
     * @return The attribute type of this attribute.
     */
    public Type<HierarchyAttribute> getType()
    {
        return attributeClass;
    }

    /**
     * Returns the factory that created this attribute.
     *
     * @return The factory that created this attribute.
     */
    public HierarchyAttributeFactory getFactory()
    {
        return attributeClass;
    }

    /**
     * Converts the object representation into a compact in representation.
     *
     * @return The compact in representation of this attribute.
     */
    public int getIntFromAttribute()
    {
        // Return the int representation that has alrady been created.
        return value;
    }

    /**
     * Should return an integer index for the current value of this attribute from 0 to num possible values where the
     * number of possible values is finite.
     *
     * @return An integer index for the current value of this attribute from 0 to num possible values where the number
     *         of possible values is finite.
     *
     * @throws com.thesett.aima.state.InfiniteValuesException If the set of values cannot be indexed because it is
     *                                                        infinite or cannot be ordered.
     */
    public int ordinal() throws InfiniteValuesException
    {
        // Return the int representation that has alrady been created.
        return value;
    }

    /**
     * Tests if two hierarchy attributes are equal. They are equal if they have the sequence of path labels and are of
     * the same hierarchy attribute class.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is a hierarchy attribute and has the same value as this one.
     */
    public boolean equals(Object o)
    {
        // Check that the comparator is also a hierarchy atribute of the same class as this one.
        return (o instanceof HierarchyAttribute) &&
            ((((HierarchyAttribute) o).value == value) &&
                ((HierarchyAttribute) o).attributeClass.attributeClassName.equals(attributeClass.attributeClassName));
    }

    /**
     * Computes a hash code based on the compact int representation, so that hash codes are identical for hierarchy
     * attributes that are equal.
     *
     * @return A hash code.
     */
    public int hashCode()
    {
        return Integer.valueOf(value).hashCode();
    }

    /**
     * Tests if another hierarchy attribute is a sub-category of this one. It is a sub-category if it has the same
     * sequence of path labels as this one as a prefix of its whole path label.
     *
     * @param  comp The hierarchy attribute to compare to.
     *
     * @return True if the comparator is of the same type and is a sub-category of this one.
     */
    public boolean isEqualOrSubCategory(HierarchyAttribute comp)
    {
        // Check that the comparator is of the same type class as this one.
        if (!comp.attributeClass.attributeClassName.equals(attributeClass.attributeClassName))
        {
            return false;
        }

        // Extract the path labels from this and the comparator.
        List<String> otherPath = comp.getPathValue();
        List<String> path = getPathValue();

        // Check that the path length of the comparator is the same as this or longer.
        if (otherPath.size() < path.size())
        {
            return false;
        }

        // Start by assuming that the paths prefixes are the same, then walk down both paths checking they are
        // the same.
        boolean subcat = true;

        for (int i = 0; i < path.size(); i++)
        {
            // Check that the labels really are equal.
            if (!otherPath.get(i).equals(path.get(i)))
            {
                subcat = false;

                break;
            }
        }

        return subcat;
    }

    /**
     * Tests if another hierarchy attribute is strict a sub-category of this one. It is a sub-category if it has the
     * same sequence of path labels as this one as a prefix of its whole path label.
     *
     * @param  comp The hierarchy attribute to compare to.
     *
     * @return True if the comparator is of the same type and is a sub-category of this one.
     */
    public boolean isSubCategory(HierarchyAttribute comp)
    {
        // Check that the comparator is of the same type class as this one.
        if (!comp.attributeClass.attributeClassName.equals(attributeClass.attributeClassName))
        {
            return false;
        }

        // Extract the path labels from this and the comparator.
        List<String> otherPath = comp.getPathValue();
        List<String> path = getPathValue();

        // Check that the path length of the comparator is the same as this plus one or longer.
        if (otherPath.size() <= path.size())
        {
            return false;
        }

        // Start by assuming that the paths prefixes are the same, then walk down both paths checking they are
        // the same.
        boolean subcat = true;

        for (int i = 0; i < path.size(); i++)
        {
            // Check that the labels really are equal.
            if (!otherPath.get(i).equals(path.get(i)))
            {
                subcat = false;

                break;
            }
        }

        return subcat;
    }

    /**
     * Gets the path value of the hierarchy attribute as an array of labels.
     *
     * @return The path value of the hierarchy attribute as an array of labels.
     */
    public List<String> getPathValue()
    {
        return attributeClass.getPathValue(value);
    }

    /**
     * Gets the path of hierarchy attribute from the root down to this hierarchy attribute value and all hierarchy
     * attribute values in between including non-allowable ones.
     *
     * @return The path of hierarchy attribute from the root down to this hierarchy attribute value and all hierarchy
     *         attribute values in between including non-allowable ones.
     */
    public List<HierarchyAttribute> getHierarchyPath()
    {
        return attributeClass.getHierarchyPath(value);
    }

    /**
     * Returns the long id of the attribute.
     *
     * @return The long id of the attribute.
     */
    public long getId()
    {
        // Find the category for this hierarchy attribute value.
        Tree<CategoryNode> category = attributeClass.lookup.get(value);

        // Extract and return the id.
        return category.getElement().id;
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
    public void setId(long id) throws IllegalArgumentException
    {
        // Find the category for this hierarchy attribute value.
        Tree<CategoryNode> category = attributeClass.lookup.get(value);

        // Extract the id.
        long existingId = category.getElement().id;

        // Do nothing if the new id matches the existing one.
        if (id == existingId)
        {
            return;
        }

        // The type is finalized.
        if (attributeClass.finalized)
        {
            // Raise an illegal argument exception if the id is not known.
            HierarchyAttribute newValue = attributeClass.getAttributeFromId(id);

            // Otherwise, change the value of this attribute to that of the new id.
            this.value = newValue.value;
        }

        // The type is unfinalized.
        else
        {
            // Check if another instance of the type already has the id and raise an exception if so.
            Tree<CategoryNode> existingNode = attributeClass.idMap.get(id);

            if (existingNode != null)
            {
                throw new IllegalArgumentException("The id value, " + id +
                    ", cannot be set because another instance of this type with that " + "id already exists.");
            }

            // Assign it to this instance if the type is unfinalized. Also removing the old id mapping from the id
            // map and replacing it with the new one.
            category.getElement().id = id;
            attributeClass.idMap.remove(existingId);
            attributeClass.idMap.put(id, category);
        }
    }

    /**
     * Gets the label value at the specified level of the hierarchy.
     *
     * @param  level The level to get the label at.
     *
     * @return The label value at the specified level of the hierarchy.
     */
    public String getValueAtLevel(int level)
    {
        /*log.fine("public String getValueAtLevel(int level): called");*/

        // throw new NotImplementedException();
        List<String> pathValue = getPathValue();

        /*log.fine("pathValue.size() = " + pathValue.size());*/

        // Check if the requested level exceeds the label path size, and return null for request off the end.
        if (level >= pathValue.size())
        {
            return null;
        }
        else
        {
            return getPathValue().get(level);
        }
    }

    /**
     * Gets the label value at the named level of the hierarchy.
     *
     * @param  level The level to get the label at.
     *
     * @return The label value at the specified level of the hierarchy.
     */
    public String getValueAtLevel(String level)
    {
        /*log.fine("public String getValueAtLevel(String level): called");*/
        /*log.fine("level = " + level);*/

        int index = attributeClass.levels.indexOf(level);

        /*log.fine("index = " + index);*/

        if (index == -1)
        {
            throw new IllegalArgumentException("Level name " + level +
                " is not known to this hierarchy attribute type.");
        }

        return getValueAtLevel(index);
    }

    /**
     * Gets the label value at the last level of the hierarchy.
     *
     * @return The label value at the last level of the hierarchy.
     */
    public String getLastValue()
    {
        List<String> pathValue = getPathValue();

        return pathValue.get(pathValue.size() - 1);
    }

    /**
     * Prints as a string for debugging purposes.
     *
     * @return A string for debugging purposes.
     */
    public String toString()
    {
        String result = "value: " + value + ", " + "id: " + getId() + ", [ ";

        String[] pathValue = getPathValue().toArray(new String[0]);

        for (int i = 0; i < pathValue.length; i++)
        {
            result += pathValue[i] + ((i == (pathValue.length - 1)) ? "" : ", ");
        }

        return result + " ]";
    }

    /**
     * Serialized a hierarchy attribute.
     *
     * @param  out The object output stream.
     *
     * @throws IOException If an IO exception occurs.
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        // Print out some information about the serialized object.
        /*log.fine("Serialized hierarchy attribute = " + this);*/
        /*log.fine("Serialized hierarchy attribute class = " + attributeClass);*/

        /*log.fine("Serialized attribute classes in static class map are: ");*/

        for (HierarchyClassImpl attributeClass : attributeClasses.values())
        {
            /*log.fine(attributeClass.toString());*/
        }

        // Perform default serialization.
        // out.defaultWriteObject();

        // Serialized the attribute by value, that is, its full path and the name of its attribute class.
        List<String> pathValue = getPathValue();
        String[] pathArrayValue = pathValue.toArray(new String[pathValue.size()]);

        out.writeObject(pathArrayValue);
        out.writeObject(attributeClass.getName());
    }

    /**
     * Deserializes a hierarchy attribute.
     *
     * @param  in The object input stream.
     *
     * @throws IOException            If any IO exception occurs.
     * @throws ClassNotFoundException If any classes to deserialize cannot be found.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        // Perform default de-serialization.
        // in.defaultReadObject();

        // Deserialize the attribute by value, from its attribute class and full path.
        String[] pathArrayValue = (String[]) in.readObject();
        String attributeClassName = (String) in.readObject();

        // Re-create the attribute from its value representation.
        HierarchyAttribute attr = getFactoryForClass(attributeClassName).createHierarchyAttribute(pathArrayValue);

        // Copy the fields from the freshly constructed attribute into this one.
        value = attr.value;
        attributeClass = attr.attributeClass;

        // Print out some information about the deserialized object.
        /*log.fine("Deserialized hierarchy attribute = " + this);*/
        /*log.fine("Deserialized hierarchy attribute class = " + attributeClass);*/

        /*log.fine("Deserialized attribute classes in static class map are: ");*/

        for (HierarchyClassImpl attributeClass : attributeClasses.values())
        {
            /*log.fine(attributeClass.toString());*/
        }
    }

    /**
     * Defines the class of hierarchy attributes. Class = Type + Factory.
     */
    public static interface HierarchyClass extends HierarchyType, HierarchyAttributeFactory
    {
    }

    /**
     * Provides the implementation of the hierarchy attribute class.
     */
    private static class HierarchyClassImpl extends BaseType<HierarchyAttribute> implements HierarchyClass, Serializable
    {
        /** The number of possible values this attribute can take on. Defaults to infinity. */
        int numValues = -1;

        /** The hierarchies level names. */
        List<String> levels = null;

        /**
         * Holds a map from int representations to category nodes. This is used while the class is unfinalized, once it
         * is finalized the map if transferred into the lookup array.
         */
        List<Tree<CategoryNode>> lookup = new ArrayList<Tree<CategoryNode>>();

        /**
         * Holds a map from referencable ids to category nodes. This is used to resolve ids into instances of the
         * attribute type. It is a map rather than an array because the ids do not have to be sequentially assigned from
         * 0 but could be a scattered range of values.
         */
        Map<Long, Tree<CategoryNode>> idMap = new HashMap<Long, Tree<CategoryNode>>();

        /** Holds a tree representation of all categories in the class. */
        SimpleTree<CategoryNode> categories = null; // new SimpleTree<CategoryNode>();

        /** Holds the next available integer for the compact representation enumeration. */
        int nextValue = 0;

        /** Keeps track of the maximum number of levels in the type. */
        int maxLevels = 0;

        /** Used to indicate whether the class has been finalized yet. */
        boolean finalized = false;

        /** Used to hold the name of the attribute class that this is a factory for. */
        String attributeClassName;

        /**
         * Builds a new factory for a given class.
         *
         * @param name The name that identifies this class of hierarchy attributes.
         */
        private HierarchyClassImpl(String name)
        {
            // Keep a reference to the attribute class and name.
            this.attributeClassName = name;
        }

        /**
         * Gets an instance of this factory implementation for specified class of hierarchy attribute. If the attribute
         * class is a new one, then a new factory instance is created, otherwise an existing factory for the class is
         * used.
         *
         * @param  name The name that identifies this class of hierarchy attributes.
         *
         * @return A factory implementation for the named hierarchy attribute class.
         */
        public static HierarchyClassImpl getInstance(String name)
        {
            // Try to get the attribute class from the map of those that have already been created.
            HierarchyClassImpl attributeClass = attributeClasses.get(name);

            // Check if this is a new class and create it if so.
            if (attributeClass == null)
            {
                attributeClass = new HierarchyClassImpl(name);
                attributeClasses.put(name, attributeClass);
            }

            return attributeClass;
        }

        /**
         * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
         *
         * @return A new default instance of the type. Always <tt>false</tt>.
         */
        public HierarchyAttribute getDefaultInstance()
        {
            throw new NotImplementedException();
        }

        /**
         * Gets the hierarchy attribute type for this factory.
         *
         * @return The hierarchy attribute type for this factory.
         */
        public HierarchyType getType()
        {
            return this;
        }

        /**
         * Gets the underlying Java class that implements the type.
         *
         * @return The underlying Java class that implements the type.
         */
        public Class<HierarchyAttribute> getBaseClass()
        {
            return HierarchyAttribute.class;
        }

        /** {@inheritDoc} */
        public String getBaseClassName()
        {
            return getBaseClass().getName();
        }

        /**
         * Sets up the level names of the hierarchy.
         *
         * @param levels The level names.
         */
        public void setLevelNames(String[] levels)
        {
            for (String level : levels)
            {
                // Check that none of the level names are null or empty
                if (Validation.isEmpty(level))
                {
                    throw new IllegalArgumentException("Null or empty level names are not allowed.");
                }
            }

            // Keep the level names.
            this.levels = new ArrayList(levels.length);

            for (String level : levels)
            {
                this.levels.add(level);
            }
        }

        /**
         * Gets the level names.
         *
         * @return The level names.
         */
        public String[] getLevelNames()
        {
            return levels.toArray(new String[levels.size()]);
        }

        /**
         * Creates a hierarchy attribute of the class that this is a factory for. If the attribute class has been
         * finalized and the requested value is not in the class then this should raise an exception.
         *
         * @param  value The value that the hierarchy attribute should have.
         *
         * @return A new hierarchy attribute with the specified value.
         *
         * @throws IllegalArgumentException If the type has been finalized but the value to create an attribute for if
         *                                  not a member of the type.
         */
        public HierarchyAttribute createHierarchyAttribute(String[] value) throws IllegalArgumentException
        {
            // Run a search and try to find a category matching the specified sequence of labels.
            SearchState searchState = findMatchingCategory(value);

            // Check for error conditions.
            // Check if the attribute class has been finalized, a match has been found, but it is not allowable.
            if (finalized && searchState.foundMatch && !searchState.currentCategory.getElement().allowable)
            {
                throw new IllegalArgumentException("A hierarchy attribute cannot be created for the specified " +
                    "value as it is not an allowable category.");
            }

            // Check if there has not been a match but the type is finalized.
            if (finalized && !searchState.foundMatch)
            {
                // The value is not already in the attribute class and it is finalized so an attribute
                // cannot be created.
                throw new IllegalArgumentException("A hierarchy element cannot be created because the type, " +
                    attributeClassName + ", has been finalized and does not contain " + "the requested value.");
            }

            // Check if there has been a match and the type is not finalized then make sure that the matched category is
            // made allowable as it may not already be.
            if (!finalized && searchState.foundMatch)
            {
                // Make the matched value allowable if it is not already.
                searchState.currentCategory.getElement().allowable = true;
            }

            // The value is not already in the attribute class but it is not finalized yet, so add the remaining
            // labels to the category tree.
            else if (!finalized && !searchState.foundMatch)
            {
                SearchState rootState = new SearchState();
                rootState.currentCategory = categories;
                rootState.foundMatch = false;
                rootState.lastPosition = 0;

                searchState = insertNewCategory(searchState, value);
            }

            // Create a new hierarcy attribute from the compact int representation of the terminal position in the
            // category tree of the search or search/insert procedures.
            return new HierarchyAttribute(searchState.currentCategory.getElement().value, this);
        }

        /**
         * Creates a hierarchy attribute of the class that this is a factory for. This method differs from the
         * {@link #createHierarchyAttribute} method in that it can create non-allowable hierarchy attributes. This
         * method will only create hierarchy attributes for values that already exist in the hierarchy class, whether or
         * not it has been finalized. Its purpose is to create any path within a hierarchy attribute for equality or
         * sub-type comparision with other hierarchy attributes.
         *
         * @param  value The value that the hierarchy attribute should have.
         *
         * @return A new hierarchy attribute with the specified value.
         *
         * @throws IllegalArgumentException If the value is not already a memeber of the type.
         */
        public HierarchyAttribute createHierarchyAttributeForComparison(String[] value) throws IllegalArgumentException
        {
            // Run a search and try to find a category matching the specified sequence of labels.
            SearchState searchState = findMatchingCategory(value);

            // Check for error conditions.
            // Check if no match was found.
            if (!searchState.foundMatch)
            {
                throw new IllegalArgumentException("A hierarchy attribute for comparison can only be created for " +
                    "value that already exist in the type.");
            }

            // Create a new hierarcy attribute from the compact int representation of the terminal position in the
            // category tree of the search or search/insert procedures.
            return new HierarchyAttribute(searchState.currentCategory.getElement().value, this);
        }

        /**
         * Returns the list of labels from the root of the category tree to the specified category.
         *
         * @param  currentValue The int value of the category to get the list of labels for.
         *
         * @return The list of labels from the root of the category tree to the specified category.
         */
        public List<String> getPathValue(int currentValue)
        {
            /*log.fine("public List<String> getPathValue(int currentValue): called");*/
            /*log.fine("currentValue = " + currentValue);*/

            // Get the point in the tree of categories that corresponds to the specified value.
            Tree<CategoryNode> category = lookup.get(currentValue);
            /*log.fine("Matching category = " + category);*/

            // Push all the labels onto a stack, up to but not including the invisible root of the tree.
            Stack<String> stack = new Stack<String>();

            do
            {
                stack.push(category.getElement().label);

                /*log.fine("Pushed " + category.getElement().label + " onto the stack.");*/

                category = category.getParent();
            }
            while ((category != null) && (category != categories));

            // Reverse the stack by popping everything into a list.
            List<String> result = new ArrayList(stack.size());

            int stackSize = stack.size();

            for (int i = 0; i < stackSize; i++)
            {
                result.add(stack.pop());
            }

            /*log.fine("Complete path value = " + result);*/

            // Return the stack as a list.
            return result;
        }

        /**
         * Gets the path of hierarchy attribute from the root down to this hierarchy attribute value and all hierarchy
         * attribute values in between including non-allowable ones.
         *
         * @param  currentValue The int value of the category to get the list of values for.
         *
         * @return The path of hierarchy attribute from the root down to this hierarchy attribute value and all
         *         hierarchy attribute values in between including non-allowable ones.
         */
        public List<HierarchyAttribute> getHierarchyPath(int currentValue)
        {
            // Get the point in the tree of categories that corresponds to the specified value.
            Tree<CategoryNode> category = lookup.get(currentValue);

            // Push all the labels onto a stack, up to but not including the invisible root of the tree.
            Stack<HierarchyAttribute> stack = new Stack<HierarchyAttribute>();

            do
            {
                stack.push(new HierarchyAttribute(category.getElement().value, this));
                category = category.getParent();
            }
            while ((category != null) && (category != categories));

            // Reverse the stack by popping everything into a list.
            List<HierarchyAttribute> result = new ArrayList(stack.size());

            int stackSize = stack.size();

            for (int i = 0; i < stackSize; i++)
            {
                result.add(stack.pop());
            }

            // Return the stack as a list.
            return result;
        }

        /**
         * Reports the maximum number of levels that this hierarchy attribute type contains.
         *
         * @return The maximum number of levels that this hierarchy attribute type contains.
         */
        public int getMaxLevels()
        {
            return maxLevels;
        }

        /**
         * Converts a compact int representation of a hierarchy attribute into its object representation. If the int is
         * not valid then this will raise an exception.
         *
         * @param  i The compact int index that a hierarchy attribute should be created from.
         *
         * @return A hierarchy attribute looked up by its in index.
         */
        public HierarchyAttribute getAttributeFromInt(int i)
        {
            // Check that the value exists.
            if (i >= lookup.size())
            {
                throw new IllegalArgumentException("The specified hierarchy element cannot be generated from the " +
                    "compact representation, " + i + ", because that does not represent an existing value.");
            }

            // Check that the value is allowable.
            Tree<CategoryNode> matchedCategory = lookup.get(i);

            if (!matchedCategory.getElement().allowable)
            {
                throw new IllegalArgumentException("The specified hierarchy element cannot be generated from the " +
                    "compact representation, " + i + ", because that does not represent an allowable value.");
            }

            return new HierarchyAttribute(i, this);
        }

        /**
         * Converts an id representation of a hierarchy attribute into its object representation. If the id is not valid
         * then this will raise an exception.
         *
         * @param  id The id of the hierarchy attribute value to fetch.
         *
         * @return A hierarchy attribute looked up by its id.
         */
        public HierarchyAttribute getAttributeFromId(long id)
        {
            // The id is only valid if it is in the id map and is allowable.
            Tree<CategoryNode> matchedCategory = idMap.get(id);

            // Check that the value exists.
            if (matchedCategory == null)
            {
                throw new IllegalArgumentException("The specified hierarchy element cannot be generated from the id, " +
                    id + ", because that does not represent an existing value.");
            }

            // Check that the value is allowable.
            if (!matchedCategory.getElement().allowable)
            {
                throw new IllegalArgumentException("The specified hierarchy element cannot be generated from the id, " +
                    id + ", because that does not represent an allowable value.");
            }

            return new HierarchyAttribute(matchedCategory.getElement().value, this);
        }

        /** Finalizes an attribute class. This prevents any new attribute value from being added to the class. */
        public void finalizeAttribute()
        {
            /*log.fine("public void finalizeAttribute(): called");*/
            /*log.fine("categories = " + categories);*/

            // Check if the attribute class has already been finalized and do nothing if so.
            if (finalized)
            {
                // Its already been finalized. Do nothing.
                return;
            }

            // Count how many allowable values there are and set this as the possible value count.
            int allowableValues = 0;

            // Ensure that some categories actually exist to be counted.
            if (categories != null)
            {
                // Used to ensure that the first category, corresponding to the invisible root node is skipped.
                boolean firstCategory = true;

                for (Iterator<SimpleTree<CategoryNode>> i = categories.iterator(Tree.IterationOrder.PreOrder);
                        i.hasNext();)
                {
                    // Skip the invisible root node.
                    if (firstCategory)
                    {
                        firstCategory = false;
                        i.next();

                        continue;
                    }

                    Tree<CategoryNode> nextCategory = i.next();
                    /*log.fine("nextCategory.getElement() = " + nextCategory.getElement());*/

                    // Increment the allowable values only if it is allowable.
                    allowableValues = (nextCategory.getElement().allowable) ? (allowableValues + 1) : allowableValues;
                }
            }

            numValues = allowableValues;

            // Set the finalized flag.
            finalized = true;
        }

        /** Drops an attribute class. The attribute class is explicitly deleted. */
        public void dropAttributeClass()
        {
            attributeClasses.remove(attributeClassName);
        }

        /**
         * Returns a name that uniquely identifies this hierarchy attribute type.
         *
         * @return The name of the hierarchy attribute type.
         */
        public String getName()
        {
            return attributeClassName;
        }

        /**
         * Determines how many different values an attribute of this type can take on.
         *
         * @return The number of possible values that an instance of this attribute can take on.
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
        public Set<HierarchyAttribute> getAllPossibleValuesSet()
        {
            return getAllPossibleValuesSet(true);
        }

        /** {@inheritDoc} */
        public Set<HierarchyAttribute> getAllPossibleValuesSet(boolean failOnNonFinalized)
        {
            // Check if the attribute class is not yet finalized so an infinte values exception must be thrown.
            if (!finalized && failOnNonFinalized)
            {
                throw new InfiniteValuesException("The string attribute class is not finalized yet, " +
                    "so can have infinite values.", null);
            }

            // The attribute class is finalized so return the array of possible values as a set.
            Set<HierarchyAttribute> result = new HashSet<HierarchyAttribute>();

            // Loop over the whole lookup array, extracting just those values that are allowable.
            for (Tree<CategoryNode> nextCategory : lookup)
            {
                CategoryNode nextNode = nextCategory.getElement();

                if (nextNode.allowable)
                {
                    result.add(new HierarchyAttribute(nextNode.value, this));
                }
            }

            return result;
        }

        /**
         * Returns all the different values that an OrdinalAttribute of this type can take on as an iterator over these
         * values. The hierarchy forms a tree the leaves of which are values that it can take on. The iterator returns
         * the leaves 'in-order'.
         *
         * @return An iterator over the set of attributes defining the possible value set for this attribute.
         */
        public Iterator<HierarchyAttribute> getAllPossibleValuesIterator()
        {
            return getAllPossibleValuesSet(true).iterator();
        }

        /** {@inheritDoc} */
        public Iterator<HierarchyAttribute> getAllPossibleValuesIterator(boolean failOnNonFinalized)
        {
            return getAllPossibleValuesSet(failOnNonFinalized).iterator();
        }

        /**
         * Generates the set of all the values (not just the allowable ones) in the hierarchy type, at the named level.
         *
         * @param  level The name of the level to list.
         *
         * @return The set of all the values (not just the allowable ones) in the hierarchy type, at the named level.
         */
        public Set<HierarchyAttribute> getValuesAtLevelSet(String level)
        {
            /*log.fine("public Set<HierarchyAttribute> getValuesAtLevelSet(String level = " + level + "): called");*/

            // Get the maximum depth to list down to.
            int depth = levels.indexOf(level);

            /*log.fine("depth = " + depth);*/

            // Check that the requested level was actually found.
            if (depth == -1)
            {
                throw new IllegalArgumentException("Level, " + level + ", is not recognized.");
            }

            // Search down to the requested depth, including both allowable and non-allowable elements in the result.
            Set<HierarchyAttribute> results = new HashSet<HierarchyAttribute>();
            findAllMatchingAttributes(depth, new TrueUnaryPredicate(), results);

            return results;
        }

        /**
         * Generates the set of all the values (not just the allowable ones) in the hierarchy type that are
         * sub-hierarchies of the specified value, at the named level.
         *
         * @param  parent The parent hierarchy to list the siblings of.
         * @param  level  The name of the level to list.
         *
         * @return The set of all the values (not just the allowable ones) in the hierarchy type, at the named level.
         */
        public Set<HierarchyAttribute> getSubHierarchyValuesSet(HierarchyAttribute parent, String level)
        {
            /*log.fine(
                "public Set<HierarchyAttribute> getSubHierarchyValuesSet(HierarchyAttribute parent, String level): called");*/
            /*log.fine("parent = " + parent);*/
            /*log.fine("level = " + level);*/

            // Get the maximum depth to list down to.
            int depth = levels.indexOf(level);

            /*log.fine("depth = " + depth);*/

            // Check that the requested level was actually found.
            if (depth == -1)
            {
                throw new IllegalArgumentException("Level, " + level + ", is not recognized.");
            }

            // Create a strict sub-category selection predicate.
            final HierarchyAttribute parentRef = parent;
            final HierarchyClassImpl thisRef = this;

            UnaryPredicate<TreeSearchState<CategoryNode>> strictSubCategoryPredicate =
                new UnaryPredicate<TreeSearchState<CategoryNode>>()
                {
                    public boolean evaluate(TreeSearchState<CategoryNode> c)
                    {
                        return parentRef.isSubCategory(new HierarchyAttribute(c.getElement().value, thisRef));
                    }
                };

            // Search down to the requested depth, including both allowable and non-allowable elements in the result.
            // Create a bounded depth first search method.
            Set<HierarchyAttribute> results = new HashSet<HierarchyAttribute>();
            findAllMatchingAttributes(depth, strictSubCategoryPredicate, results);

            return results;
        }

        /**
         * Generates an iterator over all the values (not just the allowable ones) in the hierarchy type, at the named
         * level.
         *
         * @param  level The name of the level to list.
         *
         * @return An iterator over all the values (not just the allowable ones) in the hierarchy type, at the named
         *         level.
         */
        public Iterator<HierarchyAttribute> getValuesAtLevelIterator(String level)
        {
            return getValuesAtLevelSet(level).iterator();
        }

        /**
         * Generates an iterator over all the values (not just the allowable ones) in the hierarchy type that are
         * sub-hierarchies of the specified value, at the named level.
         *
         * @param  parent The parent hierarchy to list the siblings of.
         * @param  level  The name of the level to list.
         *
         * @return An iterator over all the values (not just the allowable ones) in the hierarchy type, at the named
         *         level.
         */
        public Iterator<HierarchyAttribute> getSubHierarchyValuesIterator(HierarchyAttribute parent, String level)
        {
            return getSubHierarchyValuesSet(parent, level).iterator();
        }

        /**
         * Creates a string representation of the hierarchy attribute class for debugging purposes.
         *
         * @return A string representation of the hierarchy attribute class for debugging purposes.
         */
        public String toString()
        {
            // Build a string of all the allowable values.
            String allowableValues = "";

            // Loop over the whole lookup array, extracting just those values that are allowable.
            for (int i = 0; i < lookup.size(); i++)
            {
                Tree<CategoryNode> nextCategory = lookup.get(i);
                CategoryNode nextNode = nextCategory.getElement();

                if (nextNode.allowable)
                {
                    allowableValues += "[" + nextNode.toString() + "]" + ((i == (lookup.size() - 1)) ? "" : ", ");
                }
            }

            return "attributeClassName: " + attributeClassName + ", all allowable values: " + allowableValues;
        }

        /** {@inheritDoc} */
        public void acceptVisitor(TypeVisitor visitor)
        {
            if (visitor instanceof HierarchyTypeVisitor)
            {
                ((HierarchyTypeVisitor) visitor).visit(this);
            }
            else
            {
                super.acceptVisitor(visitor);
            }
        }

        /**
         * Finds all hierarchy attributes matching the specified predicate, down to the specified depth.
         *
         * @param depth     The maximum depth to search to.
         * @param predicate The predicate to match.
         * @param results   A set to add the matching results to.
         */
        private void findAllMatchingAttributes(int depth, UnaryPredicate<TreeSearchState<CategoryNode>> predicate,
            Collection<HierarchyAttribute> results)
        {
            QueueBasedSearchMethod<Tree<CategoryNode>, TreeSearchState<CategoryNode>> search =
                new DepthBoundedSearch<Tree<CategoryNode>, TreeSearchState<CategoryNode>>(depth);
            search.reset();

            // Add only children of the invisible root node as start states to the search.
            for (Tree<CategoryNode> initialChild : categories.getChildren())
            {
                search.addStartState(new TreeSearchState<CategoryNode>(initialChild));
            }

            search.setGoalPredicate(predicate);

            SearchNode goal;

            try
            {
                do
                {
                    goal = search.findGoalPath();

                    // Check if a goal state was found.
                    if (goal != null)
                    {
                        // Get the goal state.
                        TreeSearchState<CategoryNode> goalState = (TreeSearchState<CategoryNode>) goal.getState();

                        // Extract the data element for the goal state.
                        CategoryNode goalNode = goalState.getElement();

                        // Create a hierarchy attribute for the goal.
                        HierarchyAttribute found = new HierarchyAttribute(goalNode.value, this);
                        /*log.fine("Found attribute: " + found);*/

                        results.add(found);
                    }
                }
                while (goal != null);
            }

            // There should be search failure exceptions when the maximum depth is reached. Can safely ignore as the
            // search termination point.
            catch (SearchNotExhaustiveException e)
            {
                /*log.log(java.util.logging.Level.FINE, "Got a search failure exception: ", e);*/
            }
        }

        /**
         * Inserts a new sequence of labels into the category tree starting from the specified search state.
         *
         * @param  searchState The tree search state to insert at.
         * @param  value       The labels to insert.
         *
         * @return The search state at the end of the insertion.
         */
        private SearchState insertNewCategory(SearchState searchState, String[] value)
        {
            /*log.fine("private SearchState insertNewCategory(SearchState searchState, String[] value): called");*/
            /*log.fine("searchState = " + searchState);*/

            // Start from where the matching search left off.
            for (int i = searchState.lastPosition; i < value.length; i++)
            {
                /*log.fine("i = " + i);*/

                // Add a new label to the category tree, assigning it the next available int value.
                CategoryNode newNode = new CategoryNode();
                newNode.label = value[i];
                newNode.value = nextValue++;
                newNode.id = (long) newNode.value;

                // Check if this is the last label and make it allowable if so.
                if (i == (value.length - 1))
                {
                    /*log.fine("Last label, making node allowable.");*/
                    newNode.allowable = true;
                }

                /*log.fine("Created new node to insert: " + newNode);*/

                // Hook it into the tree.
                Tree<CategoryNode> newChild = new SimpleTree<CategoryNode>();
                newChild.setElement(newNode);

                // If this is the first node in the tree then create the initial tree.
                if (categories == null)
                {
                    categories = new SimpleTree<CategoryNode>();
                    categories.addChild(newChild);
                    //categories = (SimpleTree<CategoryNode>) categories.getChild(0);

                    //categories = newChild;
                    /*log.fine("First node in the tree, set as categories root.");*/
                }
                else
                {
                    searchState.currentCategory.addChild(newChild);
                    /*log.fine("Added as child of current search state.");*/
                }

                // Add it to the map from integer values to categories.
                lookup.add(newNode.value, newChild);

                // Add it to the map from ids to categories.
                idMap.put(newNode.id, newChild);

                // Update maxLevels if a higher greater level has been reached than before.
                if ((searchState.lastPosition + 1) > maxLevels)
                {
                    maxLevels = searchState.lastPosition + 1;
                    /*log.fine("Updated maxLevels to " + maxLevels);*/
                }

                // Advance the current position in the category to the child node just created.
                searchState.currentCategory = newChild;
                searchState.lastPosition++;

                /*log.fine("New search state = " + searchState);*/
                /*log.fine("At end of loop, categories = " + categories);*/
            }

            /*log.fine("At end of insert, categories = " + categories);*/

            return searchState;
        }

        /**
         * Searches the tree of categories for a {@link SearchState} that exactly matches the specified list (path) of
         * labels, or the search state corresponding to the longest path of labels that exists already in the tree of
         * categories, that matches some of the values in the specified list.
         *
         * @param  value The path of labels to search for.
         *
         * @return The search state where a match was found or where the search terminated because no match can be
         *         found.
         */
        private SearchState findMatchingCategory(String[] value)
        {
            /*log.fine("private SearchState findMatchingCategory(String[] value): called");*/
            /*log.fine("categories = " + categories);*/

            // Create a search state to encapsulate the state at the end of the search;
            SearchState searchState = new SearchState();

            // Loop over the new value to see if it can be found in the existing category tree.
            // Start at the root category.
            searchState.currentCategory = categories;

            // Used to indicate that a match has been found.
            searchState.foundMatch = false;

            // Holds the last position in the value to be set where the search for a match stopped.
            searchState.lastPosition = 0;

            // Check that some categories do exist, otherwise return the unstarted search state.
            if (categories == null)
            {
                /*log.fine("At end of find, searchState = " + searchState);*/

                return searchState;
            }

            // Make the children of the starting node the first set of child nodes to be examined.
            Collection<Tree<CategoryNode>> initialChildCollection = new ArrayList<Tree<CategoryNode>>();

            for (Tree<CategoryNode> initialChild : categories.getChildren())
            {
                initialChildCollection.add(initialChild);
                /*log.fine("Set " + initialChild + " as an initial child node to examine.");*/
            }

            Iterator<Tree<CategoryNode>> childIterator = initialChildCollection.iterator();

            for (int i = 0; i < value.length; i++)
            {
                // Get the next label in the value.
                String nextLabel = value[i];
                /*log.fine("nextLabel = " + nextLabel);*/

                // Label at current position examined but not yet matched, so record the last position as this label.
                searchState.lastPosition = i;

                // Examine the next set of child nodes for a matching label.
                boolean foundLabelMatch = false;

                while (childIterator.hasNext())
                {
                    Tree<CategoryNode> nextSubCategory = childIterator.next();

                    // Check if the current label matches the sub-category.
                    if (nextSubCategory.getElement().label.equals(nextLabel))
                    {
                        /*log.fine("Found match for nextLabel.");*/

                        // Move the current category down to the matched sub-category.
                        searchState.currentCategory = nextSubCategory;

                        // Record that a match has been found.
                        foundLabelMatch = true;

                        // Label at current position examined and matched, so record the last position as the next
                        // label.
                        searchState.lastPosition = i + 1;

                        // Check if this was the last label to match and set the success flag if so.
                        if (i == (value.length - 1))
                        {
                            /*log.fine("Completed match for whole path.");*/
                            searchState.foundMatch = true;
                        }

                        // Stop scanning the sub-categories.
                        break;
                    }
                }

                // Check if the whole value has been matched and terminate the search if so.
                if (searchState.foundMatch)
                {
                    break;
                }

                // Check if a label match was found and move on to the next label if so, but only if there are
                // child nodes to examine.
                else if (foundLabelMatch)
                {
                    // Set up the children, if any, of the matched child node for the next round of matching.
                    if (!searchState.currentCategory.isLeaf())
                    {
                        childIterator = searchState.currentCategory.getAsNode().getChildIterator();
                    }

                    // There are no more child nodes to match, so stop the search here.
                    else
                    {
                        break;
                    }

                    // Continue searching the child nodes for a match.
                    // continue;
                }

                // A label match was not found so the match cannot be completed. The new value may still be inserted
                // into the category tree provided the type has not been finalized. Terminate the search for a match.
                else
                {
                    break;
                }
            }

            /*log.fine("At end of find, searchState = " + searchState);*/

            return searchState;
        }
    }

    /**
     * A CategoryNode captures all the relevant information about a node within a tree of categories; its label, its
     * enumerated compact int representation, whether or not it is an allowable value.
     */
    private static class CategoryNode implements Serializable
    {
        /** The label of the node. */
        public String label;

        /** The compact int value of the node. */
        public int value;

        /** Flag to indicate whether it is an allowable value. */
        public boolean allowable;

        /** The referencable id of the value. */
        public long id;

        /**
         * Outputs all values as a string for debugging.
         *
         * @return All values as a string for debugging.
         */
        public String toString()
        {
            return "label = " + label + ": value = " + value + ", allowable = " + allowable + ", id = " + id;
        }
    }

    /**
     * Used to encapsulate the state of a search over the tree of categories when a search needs to be stopped because
     * no match has been found, but then restarted to insert a new value where this is permitted.
     */
    private static class SearchState
    {
        /** The point in the tree of categories last examined. */
        Tree<CategoryNode> currentCategory;

        /** Whether or not a succesfull match was found. */
        boolean foundMatch;

        /** The last position seen in the array of labels being scanned for a match. */
        int lastPosition;

        /**
         * Prints search state as a string for debugging purposes.
         *
         * @return search state as a string for debugging purposes.
         */
        public String toString()
        {
            return "currentCategory = " + currentCategory + ", foundMatch = " + foundMatch + ", lastPosition = " +
                lastPosition;
        }
    }
}
