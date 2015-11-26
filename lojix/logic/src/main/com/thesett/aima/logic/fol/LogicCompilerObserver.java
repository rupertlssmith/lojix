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

import com.thesett.common.parsing.SourceCodeException;

/**
 * LogicCompilerObserver observers all compiled outputs of a {@link LogicCompilerObserver}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Accept notification of the generation of a compiled output.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface LogicCompilerObserver<T, Q>
{
    /**
     * Accepts notification of the completion of the compilation of a sentence into a (binary) form.
     *
     * @param  sentence The compiled form of the sentence.
     *
     * @throws SourceCodeException If there is an error in the compiled code that prevents its further processing.
     */
    void onCompilation(Sentence<T> sentence) throws SourceCodeException;

    /**
     * Accepts notification of the completion of the compilation of a query into binary form.
     *
     * @param  sentence The compiled query.
     *
     * @throws SourceCodeException If there is an error in the compiled code that prevents its further processing.
     */
    void onQueryCompilation(Sentence<Q> sentence) throws SourceCodeException;
}
