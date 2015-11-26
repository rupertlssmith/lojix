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
package com.thesett.aima.logic.fol;

/**
 * Defines the sentence type for a logical language build over terms in first order logic. Sentences are usually marker
 * interfaces to capture a languages abstract syntax tree (Term) or compiled form, and mark it as belonging to a
 * particular type. The method {@link #getT()} is used to get the wrapped expression.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Signifies a sentence over logical terms.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Sentence<T>
{
    /**
     * Gets the wrapped sentence in the logical language over T.
     *
     * @return The wrapped sentence in the logical language.
     */
    T getT();
}
