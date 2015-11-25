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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * FileUtils provides some simple helper methods for working with files. It follows the convention of wrapping all
 * checked exceptions as runtimes, so code using these methods is free of try-catch blocks but does not expect to
 * recover from errors.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Read a text file as a string.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FileUtils
{
    /**
     * Reads a text file as a string.
     *
     * @param  filename The name of the file.
     *
     * @return The contents of the file.
     */
    public static String readFileAsString(String filename)
    {
        BufferedInputStream is = null;

        try
        {
            is = new BufferedInputStream(new FileInputStream(filename));
        }
        catch (FileNotFoundException e)
        {
            throw new IllegalStateException(e);
        }

        return readStreamAsString(is);
    }

    /**
     * Reads a text file as a string.
     *
     * @param  file The file.
     *
     * @return The contents of the file.
     */
    public static String readFileAsString(File file)
    {
        BufferedInputStream is = null;

        try
        {
            is = new BufferedInputStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            throw new IllegalStateException(e);
        }

        return readStreamAsString(is);
    }

    /**
     * Writes an object, using its 'toString' method, to the named file. The object may optionally be appended to the
     * file, or may overwrite it.
     *
     * @param outputFileName The name of the file to write the output to.
     * @param toWrite        The object to write to the file.
     * @param append         <tt>true</tt> to append to the file, <tt>false</tt> to overwrite.
     */
    public static void writeObjectToFile(String outputFileName, Object toWrite, boolean append)
    {
        // Open the output file.
        Writer resultWriter;

        try
        {
            resultWriter = new FileWriter(outputFileName, append);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Unable to open the output file '" + outputFileName + "' for writing.", e);
        }

        // Write the object into the output file.
        try
        {
            resultWriter.write(toWrite.toString());
            resultWriter.flush();
            resultWriter.close();
        }
        catch (IOException e)
        {
            throw new IllegalStateException("There was an error whilst writing to the output file '" + outputFileName + "'.",
                e);
        }
    }

    /**
     * Reads the contents of a reader, one line at a time until the end of stream is encountered, and returns all
     * together as a string.
     *
     * @param  is The reader.
     *
     * @return The contents of the reader.
     */
    private static String readStreamAsString(BufferedInputStream is)
    {
        try
        {
            byte[] data = new byte[4096];

            StringBuffer inBuffer = new StringBuffer();

            int read;

            while ((read = is.read(data)) != -1)
            {
                String s = new String(data, 0, read);
                inBuffer.append(s);
            }

            return inBuffer.toString();
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
