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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;

/**
 * Provides an implementaion of the JNDI InitialContextFactory that supplies {@link SimpleContext}s.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SimpleInitialContextFactory implements InitialContextFactory
{
    /**
     * Creates an Initial Context for beginning name resolution. Special requirements of this context are supplied using
     * environment.
     *
     * <p/>The environment parameter is owned by the caller. The implementation will not modify the object or keep a
     * reference to it, although it may keep a reference to a clone or copy.
     *
     * @param  env The possibly null environment specifying information to be used in the creation of the initial
     *             context.
     *
     * @return A non-null initial context object that implements the Context interface.
     */
    public Context getInitialContext(Hashtable env)
    {
        return new SimpleContext(env);
    }
}
