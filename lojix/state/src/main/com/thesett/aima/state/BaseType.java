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

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import com.thesett.aima.state.restriction.TypeRestriction;

/**
 * BaseType provides type visitor coverage to all types that extend it.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Accept a default type visitor.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseType<T> implements Type<T>, RandomInstanceFactory<T>, Serializable
{
    /** Used for generating random instances. */
    protected static Random random = new Random();

    /** Holds a random instance factory for the type. */
    private RandomInstanceFactory<T> randomFactory;

    /** Holds the extra type resrictions. */
    protected List<TypeRestriction> restrictions;

    /** {@inheritDoc} */
    public void acceptVisitor(TypeVisitor visitor)
    {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>This default implementation will throw a RandomInstanceNotSupportedException when no random instance factory
     * has been set up, otherwise it will use that factory to generate random instances. Sub-classes supporting random
     * instances can either set up a factory, or override this method with a fixed implementation.
     */
    public T getRandomInstance() throws RandomInstanceNotSupportedException
    {
        return this.createRandomInstance();
    }

    /** {@inheritDoc} */
    public T createRandomInstance() throws RandomInstanceNotSupportedException
    {
        throw new RandomInstanceNotSupportedException("Type does not support random instance creation.", null);
    }

    /** {@inheritDoc} */
    public List<TypeRestriction> getRestrictions()
    {
        return restrictions;
    }

    /**
     * Allows a random instance factory to be set up on the type. This is used to generate random instances of the type
     * when the {@link #getRandomInstance()} method is called.
     *
     * @param factory The random instance factory to use.
     */
    public void setRandomInstanceFactory(RandomInstanceFactory<T> factory)
    {
        this.randomFactory = factory;
    }
}
