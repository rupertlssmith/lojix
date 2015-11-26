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

/**
 * RandomInstanceFactory is used to generate random instance of a {@link Type} over <T>. The base type class,
 * {@link BaseType} will accept a random instance factory, and use it to generate random instances of its type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Generate random instances of a type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface RandomInstanceFactory<T>
{
    /**
     * Gets a random instance of the type. This is intended to be usefull for generating test data, as any type in a
     * data model will be able to generate random data fitting the model. Some times may be impractical or impossible to
     * generate random data for. For example, string patterns fitting a general regular expression cannot in general
     * always be randomly generated. For this reason the method signature allows a checked exception to be raised when
     * this method is not supported.
     *
     * @return A new random instance of the type.
     *
     * @throws RandomInstanceNotSupportedException If a random instance of the type cannot be created.
     */
    T createRandomInstance() throws RandomInstanceNotSupportedException;
}
