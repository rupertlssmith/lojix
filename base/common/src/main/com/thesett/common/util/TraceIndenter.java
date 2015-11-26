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
package com.thesett.common.util;

/**
 * TraceIndenter generates indents as spaces, to assist with pretty printing trace execution code that benefits from
 * using a nested indentation layout.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Generate indents and maintain a current indent count.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TraceIndenter
{
    /** Used to maintain a nesting index on method call traces, to neatly pad the output with spaces by. */
    private int traceIndent = 0;

    /** Flag to indicate whether or not the indent should actually be generated. */
    private final boolean useIndent;

    /** Holds the current stack of indents to the current position. */
    private final Stack<Integer> indentStack = new LifoStack<Integer>();

    /**
     * Creates a trace indenter, the flag specifies whether or not to actually generate the indents.
     *
     * @param useIndent <tt>true</tt> to generate indents.
     */
    public TraceIndenter(boolean useIndent)
    {
        this.useIndent = useIndent;
    }

    /** Resets the indentation to zero. */
    public void reset()
    {
        traceIndent = 0;
    }

    /** Provides the current latest delta to the current position. */
    public int getLastDelta()
    {
        return indentStack.peek();
    }

    /**
     * Generates a sequence of spaces to indent debugging output with, without applying a delta to the indentation
     * level.
     *
     * @return A sequence of spaces to use for nesting call traces in debug output with.
     */
    public String generateTraceIndent()
    {
        return generateTraceIndent(0);
    }

    /**
     * Generates a sequence of spaces to indent debugging output with.
     *
     * @param  delta The amount to change the current trace indent by. If this is negative it is subtracted before the
     *               sequence of spaces is generated, if it is positive it is added after.
     *
     * @return A sequence of spaces to use for nesting call traces in debug output with.
     */
    public String generateTraceIndent(int delta)
    {
        if (!useIndent)
        {
            return "";
        }
        else
        {
            if (delta >= 1)
            {
                indentStack.push(delta);
            }
            else if (delta < 0)
            {
                indentStack.pop();
            }

            StringBuffer result = new StringBuffer();

            traceIndent += (delta < 0) ? delta : 0;

            for (int i = 0; i < traceIndent; i++)
            {
                result.append(" ");
            }

            traceIndent += (delta > 0) ? delta : 0;

            return result.toString();
        }
    }
}
