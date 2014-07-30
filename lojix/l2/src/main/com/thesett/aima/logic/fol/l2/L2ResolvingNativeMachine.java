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
package com.thesett.aima.logic.fol.l2;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;

import com.thesett.aima.logic.fol.LinkageException;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.common.error.ImplementationUnavailableException;
import com.thesett.common.util.SequenceIterator;

/**
 * L2ResolvingNativeMachine is a byte code interpreter for L2 implemented externally as native code. The code here is a
 * wrapper for the native library. It provides a method to get an instance of this l2 machine that can throw a checked
 * exception when the native library is not available.
 *
 * <p/>The other interesting feature of this machine, is that the byte code is passed to the native library in a direct
 * byte buffer. The intention is that the compiler (or in the future a loader) outputs compiled code into the machine
 * which holds it in a direct byte buffer. No copying accross will be done prior to execution.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Execute compiled L2 programs and queries.
 * <tr><td> Provide access to the heap.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class L2ResolvingNativeMachine extends L2ResolvingMachine
{
    /** Used for tracing instruction executions. */
    /* private static final Logger trace = Logger.getLogger("TRACE.L2ResolvingNativeMachine"); */

    /** Defines the initial code area size for the virtual machine. */
    private static final int CODE_SIZE = 10000;

    /** Used to record whether the native implementation library was successfully loaded. */
    private static boolean libraryFound = false;

    /** Used to record whether an attempt to load the native library has been made. */
    private static boolean libraryLoadAttempted = false;

    /**
     * Holds the buffer of executable code in a direct byte buffer so that no copying out to the native machine needs to
     * be done.
     */
    ByteBuffer codeBuffer;

    /** Creates a unifying virtual machine for L2 with default heap sizes. */
    public L2ResolvingNativeMachine()
    {
        // Reset the machine to its initial state.
        reset();
    }

    /**
     * Creates an instance of this machine, loading and checking for availability of the native implementation library
     * as required.
     *
     * @return An instance of the native l2 machine.
     *
     * @throws ImplementationUnavailableException If the native library cannot be loaded and linked.
     */
    public static L2ResolvingNativeMachine getInstance() throws ImplementationUnavailableException
    {
        try
        {
            if (!libraryLoadAttempted)
            {
                libraryLoadAttempted = true;

                System.loadLibrary("aima_native");

                libraryFound = true;
            }

            if (libraryFound)
            {
                return new L2ResolvingNativeMachine();
            }
            else
            {
                throw new ImplementationUnavailableException("The native library could not be found.", null, null,
                    null);
            }
        }
        catch (UnsatisfiedLinkError e)
        {
            libraryFound = false;
            throw new ImplementationUnavailableException("The native library could not be found.", e, null, null);
        }
    }

    /** {@inheritDoc} */
    public void emmitCode(L2CompiledClause clause) throws LinkageException
    {
        // Keep track of the offset into which the code was loaded.
        int entryPoint = codeBuffer.position();
        int length = (int) clause.sizeof();
        byte[] code = new byte[length];

        // If the code is for a program clause, store the programs entry point in the call table.
        L2CallPoint callPoint;

        if (!clause.isQuery())
        {
            callPoint = setCodeAddress(clause.getHead().getName(), entryPoint, length);
        }
        else
        {
            callPoint = new L2CallPoint(entryPoint, length, -1);
        }

        // Emmit code for the clause into this machine.
        clause.emmitCode(0, code, this, callPoint);

        // Notify the native machine of the addition of new code.
        codeBuffer.put(code, 0, length);
        codeAdded(codeBuffer, entryPoint, length);
    }

    /**
     * Extracts the raw byte code from the machine for a given call table entry.
     *
     * @param  callPoint The call table entry giving the location and length of the code.
     *
     * @return The byte code at the specified location.
     */
    public byte[] retrieveCode(L2CallPoint callPoint)
    {
        byte[] result = new byte[callPoint.length];

        codeBuffer.get(result, callPoint.entryPoint, callPoint.length);

        return result;
    }

    /**
     * Resets the machine, to its initial state. This clears any programs from the machine, and clears all of its stacks
     * and heaps.
     */
    public void reset()
    {
        // Clear the code buffer.
        codeBuffer = ByteBuffer.allocateDirect(CODE_SIZE);

        // Reset the native part of the machine.
        nativeReset();

        // Ensure that the overridden reset method of L2BaseMachine is run too, to clear the call table.
        super.reset();
    }

    /**
     * Provides an iterator that generates all solutions on demand as a sequence of variable bindings.
     *
     * @return An iterator that generates all solutions on demand as a sequence of variable bindings.
     */
    public Iterator<Set<Variable>> iterator()
    {
        return new SequenceIterator<Set<Variable>>()
            {
                public Set<Variable> nextInSequence()
                {
                    return resolve();
                }
            };
    }

    /**
     * Resets the machine, to its initial state. This clears any programs from the machine, and clears all of its stacks
     * and heaps.
     */
    public native void nativeReset();

    /** {@inheritDoc} */
    protected boolean execute(L2CallPoint callPoint)
    {
        return execute(codeBuffer, callPoint.entryPoint);
    }

    /**
     * Notified whenever code is added to the machine. This provides a hook in point at which the machine may, if
     * required, compile the code down below the byte code level.
     *
     * @param codeBuffer The code buffer.
     * @param codeOffset The start offset of the new code.
     * @param length     The length of the new code.
     */
    protected native void codeAdded(ByteBuffer codeBuffer, int codeOffset, int length);

    /**
     * Executes a compiled byte code returning an indication of whether or not a unification was found.
     *
     * @param  codeBuffer A reference to the byte code buffer to run the machine on.
     * @param  codeOffset The start address of the compiled byte code to execute.
     *
     * @return <tt>true</tt> if a unification was found, <tt>false</tt> if the search for one failed.
     */
    protected native boolean execute(ByteBuffer codeBuffer, int codeOffset);

    /** {@inheritDoc} */
    protected native int deref(int a);

    /** {@inheritDoc} */
    protected native int derefStack(int a);

    /**
     * Gets the heap cell tag for the most recent dereference operation.
     *
     * @return The heap cell tag for the most recent dereference operation.
     */
    protected native byte getDerefTag();

    /**
     * Gets the heap call value for the most recent dereference operation.
     *
     * @return The heap call value for the most recent dereference operation.
     */
    protected native int getDerefVal();

    /**
     * Gets the value of the heap cell at the specified location.
     *
     * @param  addr The address to fetch from the heap.
     *
     * @return The heap cell at the specified location.
     */
    protected native int getHeap(int addr);

    /**
     * This is a call back onto the trace logger, that the native code can use to do any trace logging through Java,
     * instead of calling 'printf' for example.
     *
     * @param message The string to log to the tracer.
     */
    private void trace(String message)
    {
        /*trace.fine(message);*/
    }
}
