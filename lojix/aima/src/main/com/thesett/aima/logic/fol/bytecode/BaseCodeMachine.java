/*
 * Copyright The Sett Ltd.
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
package com.thesett.aima.logic.fol.bytecode;

import java.nio.ByteBuffer;

import com.thesett.aima.logic.fol.LinkageException;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.common.util.SizeableList;
import com.thesett.common.util.doublemaps.SymbolTable;
import com.thesett.common.util.visitor.Acceptor;

/**
 * BaseCodeMachine is a base for deriving abstract machines that hold executable code in a byte buffer, and use a
 * compiled entity type that can provide an instruction listing, and an encoder over that type of instructions to
 * provide a set of basic operations for dealing with encoding of the byte code into the machine, and tracking callable
 * entry points into the code.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Accept byte code into the machine.
 * <tr><td> Resolve an interned name onto a call point.
 * <tr><td> Allow a call point for a procedure to be reserved.
 * <tr><td> Provide a byte buffer and current insertion point for new code.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   No garbage collection of replaced call points is done. Should eventually remove unused code, and compact the
 *         still in use code buffer. Compacting the buffer is not straightforward as call points will need to be
 *         translated. Either keep track of all call points within the code buffer and translate them on compaction, or
 *         when a code buffer is to be garbage collected, simply create a new one and re-insert all live procedures into
 *         the new buffer.
 */
public abstract class BaseCodeMachine<I extends Acceptor<I>, C extends InstructionListing<I>> extends BaseMachine
    implements CodeMachine<C>, CallPointResolver
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(BaseCodeMachine.class.getName()); */

    /** Holds the byte code emmitting encoder over the instruction set of the compiled entities. */
    protected InstructionEncoder<I> instructionEncoder;

    /**
     * Creates a base machine using the specified instruction encoder.
     *
     * @param symbolTable        The symbol table for the machine.
     * @param interner           The symbo name interner for the machine.
     * @param instructionEncoder The insutruction encoder for the machine.
     */
    protected BaseCodeMachine(SymbolTable<Integer, String, Object> symbolTable, VariableAndFunctorInterner interner,
        InstructionEncoder<I> instructionEncoder)
    {
        super(symbolTable, interner);

        this.instructionEncoder = instructionEncoder;

        // Set this up as the call point resolver for the instruction encoder. This enables it to call back onto
        // this every time the target address of a 'call' instruction needs to be resolved from its interned name.
        instructionEncoder.setCallPointResolver(this);
    }

    /**
     * Provides the name of the symbol field under which call points are stored in the symbol table.
     *
     * @return The name of the symbol field under which call points are stored in the symbol table.
     */
    public abstract String getCallPointSymbolField();

    /**
     * Provides a byte buffer that is correctly positioned to write fresh code into the code buffer.
     *
     * @param  callPoint The call point to provide the byte buffer for.
     *
     * @return A byte buffer that is correctly positioned to write fresh code into the code buffer.
     */
    public abstract ByteBuffer getCodeBuffer(CallPoint callPoint);

    /**
     * Provides the next available address at which new code may be added to the machine.
     *
     * @return The next available address at which new code may be added to the machine.
     */
    public abstract int getCodeInsertionPoint();

    /**
     * Increments the next available address at which new code may be added to the machine, by the specified amount.
     *
     * @param length The amount to advance the pointer by.
     */
    public abstract void advanceCodeInsertionPoint(int length);

    /**
     * {@inheritDoc}
     *
     * <p/>It is expected that the compiled code being written into the machine, will not have its call resolved onto
     * call-points, and that call will be made to the {@link #resolveCallPoint(int)} method, during this procedure to
     * resolve them.
     */
    public void emmitCode(C compiled) throws LinkageException
    {
        /*log.fine("public void emmitCode(C compiled): called");*/

        SizeableList<I> instructions = compiled.getInstructions();

        // Get the call point for the code to write.
        CallPoint callPoint = resolveCallPoint(compiled.getName());

        if (callPoint == null)
        {
            /*log.fine("call point not resolved, reserving new one.");*/
            callPoint = reserveCallPoint(compiled.getName(), (int) instructions.sizeof());
        }

        /*log.fine("insertion point = " + getCodeInsertionPoint());*/

        // Pass a reference to a buffer set to write to the call point in the code buffer on the encoder.
        instructionEncoder.setCodeBuffer(getCodeBuffer(callPoint));

        // Loop over all instructions encoding them to byte code in the machines code buffer.
        for (I instruction : instructions)
        {
            instruction.accept(instructionEncoder);
        }
    }

    /** {@inheritDoc} */
    public CallPoint resolveCallPoint(int name)
    {
        return (CallPoint) symbolTable.get(name, getCallPointSymbolField());
    }

    /**
     * Reserves a call point for a block of named callable code. The size of the block of code must be known fully in
     * advance. If the named block already has a call point, this will replace it with a new one.
     *
     * <p/>Adding code into a code machine may be a two-stage process, first reserve call-points for all blocks of code,
     * then add the blocks of code into the machine, resolving internal call points within the code onto previously
     * reserved call points as this is done. A two-stage process is required to support recursive or mutually recursive
     * callable procedures.
     *
     * @param  name   The interned name of the procedure to create a call point for.
     * @param  length The length of the procedures code.
     *
     * @return A new call point for the procedure.
     */
    public CallPoint reserveCallPoint(int name, int length)
    {
        // Work out where the code will go and advance the insertion point beyond its end, so that additional code
        // will be added beyond the reserved space.
        int address = getCodeInsertionPoint();
        advanceCodeInsertionPoint(length);

        // Create a call point for the reserved space.
        CallPoint callPoint = new CallPoint(address, length, name);

        // Add the call point to the symbol table under the interned name.
        symbolTable.put(name, getCallPointSymbolField(), callPoint);

        return callPoint;
    }
}
