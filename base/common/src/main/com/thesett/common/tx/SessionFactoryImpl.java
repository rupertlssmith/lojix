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
package com.thesett.common.tx;

/**
 * A SessionFactory supplies transactional sessions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Supply transactional sessions.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SessionFactoryImpl implements SessionFactory
{
    /**
     * Creates a new transactional session.
     *
     * @return A new transactional session, not bound to the current thread.
     */
    public TxSession createSession()
    {
        return new TxSessionImpl();
    }

    /**
     * Creates a new transactional session and associates it with the current thread.
     *
     * @return A transactional session associated with the current thread.
     */
    public TxSession createAndBindSession()
    {
        TxSession result = new TxSessionImpl();
        result.bind();

        return result;
    }
}
