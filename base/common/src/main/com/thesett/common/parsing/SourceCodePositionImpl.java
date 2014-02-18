/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
 * SourceCodePositionImpl identifiess a position within a text file.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Identify a location within a source file.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SourceCodePositionImpl implements SourceCodePosition
{
    /** The start line position. */
    private int startLine;

    /** That start column position. */
    private int startColumn;

    /** The end line position. */
    private int endLine;

    /** The end column position. */
    private int endColumn;

    /**
     * Creates a source code position from the specified start and end coordinates.
     *
     * @param startLine   The start line position.
     * @param startColumn The start column position.
     * @param endLine     The end line position.
     * @param endColumn   The end column position.
     */
    public SourceCodePositionImpl(int startLine, int startColumn, int endLine, int endColumn)
    {
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    /**
     * Provides the line on which the error begins.
     *
     * @return The line on which the error begins.
     */
    public int getStartLine()
    {
        return startLine;
    }

    /**
     * Provides the column on which the error begins.
     *
     * @return The column on which the error begins.
     */
    public int getStartColumn()
    {
        return startColumn;
    }

    /**
     * Provides the line on which the error ends.
     *
     * @return The line on which the error ends.
     */
    public int getEndLine()
    {
        return endLine;
    }

    /**
     * Provides the column on which the error ends.
     *
     * @return The column on which the error ends.
     */
    public int getEndColumn()
    {
        return endColumn;
    }

    /**
     * Prints the position as a string, mainly for debugging purposes.
     *
     * @return The position as a string.
     */
    public String toString()
    {
        return "SourceCodePositionImpl: [ start = (" + startLine + ", " + startColumn + "), end = (" + endLine + ", " +
            endColumn + ") ]";
    }
}
