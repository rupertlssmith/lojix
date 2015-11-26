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
package com.thesett.maven.run;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author       Rupert Smith
 * @noinspection CustomClassloader
 */
public class IsolatedClassLoader extends URLClassLoader
{
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];
    private final ClassLoader parent = ClassLoader.getSystemClassLoader();

    private final Collection urls = new HashSet();

    private boolean childDelegation = true;

    public IsolatedClassLoader()
    {
        super(EMPTY_URL_ARRAY, null);
    }

    public IsolatedClassLoader(ClassLoader parent, boolean childDelegation)
    {
        super(EMPTY_URL_ARRAY, parent);

        this.childDelegation = childDelegation;
    }

    public IsolatedClassLoader(ClassLoader parent)
    {
        super(EMPTY_URL_ARRAY, parent);
    }

    public void addURL(URL url)
    {
        // avoid duplicates
        if (!urls.contains(url))
        {
            super.addURL(url);
            urls.add(url);
        }
    }

    public synchronized Class loadClass(String name) throws ClassNotFoundException
    {
        Class c;

        if (childDelegation)
        {
            c = findLoadedClass(name);

            ClassNotFoundException ex = null;

            if (c == null)
            {
                try
                {
                    c = findClass(name);
                }
                catch (ClassNotFoundException e)
                {
                    ex = e;

                    if (parent != null)
                    {
                        c = parent.loadClass(name);
                    }
                }
            }

            if (c == null)
            {
                throw ex;
            }
        }
        else
        {
            c = super.loadClass(name);
        }

        return c;
    }
}
