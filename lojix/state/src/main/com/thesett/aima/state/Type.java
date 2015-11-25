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
package com.thesett.aima.state;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.thesett.aima.state.restriction.TypeRestriction;

/**
 * A Type is a defined set from which attribute values are drawn. The type encapsulates information that applies accross
 * the whole set of attributes that make up the type, such as, the cardinality of the set, the name of the set and so
 * on. The type also supplies methods to enumerate or iterate over all the possible attributes that make up the set,
 * where this is possible.
 *
 * <p/>A type is a set of possible values that a data item can take. For example the Java int type, { Integer.MIN_VALUE,
 * ..., 0, 1, ..., Integer.MAX_VALUE }, defines the possible values that an int can take.
 *
 * <p/>It is only possible to enumerate or iterate over the attributes in a type where the number of attributes is
 * finite or there exists some ordering over the attributes that maps them onto the natural numbers. In the second case
 * there may be infinitely many but the iterator can lazily generate them as requested. Some algorithms can work with
 * attributes that take on an infinite number of different values; real numbers, big decimals, all possible strings etc.
 * In practice real numbers will be represented as floats or doubles which can only express a bounded number of the
 * possible reals but for practical purposes this can be considered an infinity. Usually, algorithms that can only work
 * with a fixed number of attributes can only do so where this is a small number, ideally two or three; in the tens can
 * be too many in some cases. {@link InfiniteValuesException} will be thrown whenever a type cannot be enumerated
 * because it is infinite or not able to be generated lazily as a sequence.
 *
 * <p/>Note that it is not the class that implements this interface that represents the attribute type but individual
 * instances of it that represent different types. For example, there may be two string enumeration types, one that
 * represents { Dog, Cat, Cow } and one that represents { Car, Van, Bus }, both will be represented by instances of the
 * same class for string enumeration types but with different names. Such types are considered to be dynamic or runtime
 * classes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Supply the name of the type.
 * <tr><td> Supply the Java class for the type.
 * <tr><td> Report how many different values instances of the type can take on.
 * <tr><td> Supply all the different values that instances of the type can take on, where there are a finite number.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Type<T>
{
    /**
     * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
     *
     * @return A new default instance of the type.
     */
    T getDefaultInstance();

    /**
     * Gets a random instance of the type. This is intended to be usefull for generating test data, as any type in a
     * data model will be able to generate random data fitting the model. Some times may be impractical or impossible to
     * generate random data for. For example, string patterns fitting a general regular expression cannot in general
     * always be randomly generated. For this reason the method signature allows a checked exception to be raised when
     * this method is not supported.
     *
     * @return A new random instance of the type.
     *
     * @throws RandomInstanceNotSupportedException If the implementation does not support random instance creation.
     */
    T getRandomInstance() throws RandomInstanceNotSupportedException;

    /**
     * Should return a name that uniquely identifies the type.
     *
     * @return The name of the attribute type.
     */
    String getName();

    /**
     * Returns the underlying Java class that this is the type for, if there is one.
     *
     * @return The underlying Java class that this is the type for, if there is one.
     */
    Class<T> getBaseClass();

    /**
     * Provides the fully qualified name of the underlying Java class that this is the type for, if there is one.
     *
     * @return The fully qualified name of underlying Java class that this is the type for, if there is one.
     */
    String getBaseClassName();

    /**
     * Provides a list of restrictions that reduce the possible values that instances of this type can take.
     *
     * @return A list of resctrictions, empty or <tt>null</tt> indicates that the type has no extra restrictions.
     */
    List<TypeRestriction> getRestrictions();

    /**
     * Should determine how many different values an instance of the implementations type can take on.
     *
     * @return The number of possible values that an instance of this attribute can take on. If the value is -1 then
     *         this is to be interpreted as infinity.
     */
    int getNumPossibleValues();

    /**
     * Should return all the different values that an instance of this type can take on.
     *
     * @return A set of values defining the possible value set for this attribute if this is finite.
     *
     * @throws InfiniteValuesException If the set of values cannot be listed because it is infinite.
     */
    Set<T> getAllPossibleValuesSet() throws InfiniteValuesException;

    /**
     * Should return all the different values that an instance of this type can take on as an iterator over these
     * values. The set of values may be infinte if the iterator can lazily generate them as needed. If the number is
     * expected to be large it may be better to use this method to list the values than the
     * {@link #getAllPossibleValuesSet} if a lazy iterator is used because this will avoid generating a large collection
     * to hold all the possible values.
     *
     * @return An iterator over the set of attributes defining the possible value set for this attribute if this is
     *         finite or can be generated as required.
     *
     * @throws InfiniteValuesException If the set of values cannot be listed because it is infinite.
     */
    Iterator<T> getAllPossibleValuesIterator() throws InfiniteValuesException;

    /**
     * Accepts a visitor, using a visitor pattern, to extend type behaviour with visitors.
     *
     * @param visitor The visitor to accept.
     */
    void acceptVisitor(TypeVisitor visitor);
}
