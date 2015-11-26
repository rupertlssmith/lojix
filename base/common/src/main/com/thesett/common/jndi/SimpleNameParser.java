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
package com.thesett.common.jndi;

import java.util.Properties;

import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

/**
 * Provides a simple implementation of the JNDI NameParser interface.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SimpleNameParser implements NameParser
{
    /** Holds the JNDI name syntax options. */
    static final Properties syntax = new Properties();

    static
    {
        syntax.put("jndi.syntax.direction", "flat");
        syntax.put("jndi.syntax.ignorecase", "false");
    }

    /**
     * Parses a name into its components.
     *
     * @param  name The name.
     *
     * @return A non-null parsed form of the name using the naming convention of this parser.
     *
     * @throws NamingException If the name is illegal.
     */
    public Name parse(String name) throws NamingException
    {
        return new CompoundName(name, syntax);
    }
}
