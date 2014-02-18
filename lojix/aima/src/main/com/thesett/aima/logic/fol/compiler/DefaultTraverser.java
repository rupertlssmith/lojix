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
package com.thesett.aima.logic.fol.compiler;

import com.thesett.aima.logic.fol.TermTraverser;

/**
 * DefaultTraverser is a default term traverser, that does not modify the term traversal in any way and provides no
 * positional context. It is a dummy traverser to use when no special traversal is required.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> None.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DefaultTraverser implements TermTraverser
{
}
