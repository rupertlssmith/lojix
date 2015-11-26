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
package com.thesett.common.parsing;

/**
 * SourceCodePosition represents a position in a text based source code file, often during parsing or compilation of
 * program code, but could be used for any kink of text file. It can be used to identify the position of an error in the
 * source code.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Identify a location within a source file.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface SourceCodePosition
{
    /**
     * Provides the line on which the error begins.
     *
     * @return The line on which the error begins.
     */
    int getStartLine();

    /**
     * Provides the column on which the error begins.
     *
     * @return The column on which the error begins.
     */
    int getStartColumn();

    /**
     * Provides the line on which the error ends.
     *
     * @return The line on which the error ends.
     */
    int getEndLine();

    /**
     * Provides the column on which the error ends.
     *
     * @return The column on which the error ends.
     */
    int getEndColumn();

    /**
     * By default source code positions are reported offset from line 1, column 1, as this convention is how positions
     * are identified to users in text editors. This returns a zero offset version of the position, which is more useful
     * from a coding stand point.
     *
     * @return The zero offset source code position.
     */
    SourceCodePosition asZeroOffsetPosition();
}
