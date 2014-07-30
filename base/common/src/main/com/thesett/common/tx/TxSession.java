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
 * A session is a context in which transactional resources may be registered, and within which transactional sequences
 * of operations on transactional resources may be scripted, with transactional encapsulation of the entire sequence of
 * operation over all resources involved.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Apply commit or rollback over many transactional resources.
 * <tr><td>Bind a session to the current thread.
 * <tr><td>Unbind a session from the current thread.
 * <tr><td>Accept transactional resource enlist notifications.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TxSession extends Transactional, TxResourceEnlister
{
    /** Binds the session as a transactional context to the current thread, if it is not already bound. */
    public void bind();

    /** Unbinds the session as a transactional context from the current thread. */
    public void unbind();
}
