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
 * Transactional is a context interface for a class that provides transactional interaction with its operations. The
 * effects of operations are not applied until {@link #commit} is called, and are erased if {@link #rollback} is called.
 *
 * <p/>Transactional resources may support one of the following isolation levels:
 *
 * <pre><p/><table><caption>Isolation Levels</caption>
 * <tr><td>None <td>No isolation is required. The resource is non-transactional.
 * <tr><td>ReadUncommitted <td>Writes to the resource are immediately visibile to reads.
 * <tr><td>ReadCommitted <td>Writes to the resource are only visible to reads after a commit.
 * <tr><td>RepeatableRead <td>Reads always return the same value within a transaction, regardless of writes.
 * <tr><td>Serializable <td>All transactions appear to happen in sequence and never in parallel.
 * </table></pre>
 *
 * <p/>Transactional resources should be persisted to permanent storage on commit. This transaction interface also
 * support semi-transactional resources, that only store changes in RAM. This may be usefull, for example for resources
 * that can safely be rebuilt from other resources that are permanently stored, in the event of failures. Commit and
 * rollback on these resources work like 'reveal' and 'undo' operations, so are still usefull. The following persistence
 * modes may be supported by a resource:
 *
 * <pre><p/><table><caption>Persistence Modes</caption>
 * <tr><td>Soft <th>Changes are held in memory only.
 * <tr><td>Hard <th>Changes require real i/o operations to be safely committed.
 * </table></pre>
 *
 * <p/>A 'Soft' transactional resource guarantees that its commit and rollback methods cannot fail, unless there is a
 * runtime exception or error condition on the virtual machine; in which case the transaction is in a unknown state. A
 * failed soft transaction should normally be handled by terminating the program and possibly recovering the
 * transactional resources that are persisted. A 'Hard' transactional resource requires real and potentially failure
 * prone i/o operations to commit its state. Multiple seperate hard transactional resources require a sophisticated
 * transaction manager, using an algorithm such as two phase commit with presumed abort, in order to maintain
 * transaction consitency. Soft and hard resources may be combined into the same transaction; one way to do this would
 * be to use a pattern similar to the last resource gambit of XA, where the hard resources are committed first, and only
 * if they succeed the soft resources are revealed.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr><td>Apply pending operations.
 * <tr><td>Forget pending operations.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Need to add exceptions to the commit and rollback methods. Maybe should also add reveal and undo methods for
 *         soft resources that do not fail?
 */
public interface Transactional
{
    /** Defines the different transaction isolation levels. */
    public enum IsolationLevel
    {
        /** Used to indiciate the 'None' transaction isolation level. */
        None,

        /** Used to indiciate the 'ReadUncommitted' transaction isolation level. */
        ReadUncommitted,

        /** Used to indiciate the 'ReadCommitted' transaction isolation level. */
        ReadCommitted,

        /** Used to indiciate the 'RepeatableRead' transaction isolation level. */
        RepeatableRead,

        /** Used to indiciate the 'Serializable' transaction isolation level. */
        Serializable;
    }

    /** Defines the different transaction persistence modes. */
    public enum PersistenceMode
    {
        /** Used to indicate that soft persistence is used; the transactional resource is in-memory only. */
        Soft,

        /** Used to indicate that hard persistence is used; real i/o is needed to write changes to the resource. */
        Hard;
    }

    /** Applies pending operations. */
    public void commit();

    /** Forgets pending operations. */
    public void rollback();
}
