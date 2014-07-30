package com.thesett.maven.run;

import java.net.URL;
import java.net.URLClassLoader;
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

    private final Set urls = new HashSet();

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
