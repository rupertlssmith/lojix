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
package com.thesett.aima.logic.fol.vam.vam2p.instructions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.BUILTIN;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.CALL;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.CONST;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.CUT;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.FIRST_TEMP;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.FIRST_VAR;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.GOAL;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.LASTCALL;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.LIST;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.NEXT_TEMP;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.NEXT_VAR;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.NIL;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.NOGOAL;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.STRUCT;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.VOID;
import com.thesett.common.util.Sizeable;
import com.thesett.common.util.visitor.Acceptor;
import com.thesett.common.util.visitor.Visitor;

/**
 * VAM2PInstruction provides a structured in-memeory representation of the VAM2P instruction set.
 *
 * <p/>Instructions implement {@link Sizeable} reporting their length in bytes as their 'size of' paramter. This is
 * usefull when putting instructions into a Sizeable data structure, such as a list, for calculating the total space
 * required by all of the instructions in a listing.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encode the VAM2P instruction set.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAM2PInstruction implements Acceptor<VAM2PInstruction>, Sizeable
{
    /** Defines the VAM2P virtual machine instruction set as constants. */
    public enum VAM2PInstructionSet
    {
        /** Integer or Atom. */
        Const(CONST, "const", 6),

        /** Empty list. */
        Nil(NIL, "nil", 2),

        /** List followed by its arguments. */
        List(LIST, "list", 2),

        /** Structure followed by its arguments. */
        Struct(STRUCT, "struct", 6),

        /** Void variable. */
        Void(VOID, "void", 2),

        /** First occurrence of a temporary variable. */
        FirstTemp(FIRST_TEMP, "fsttmp", 6),

        /** Subsequent occurrence of a temporary variable. */
        NextTemp(NEXT_TEMP, "nxttmp", 6),

        /** First occurrence of a local variable. */
        FirstVar(FIRST_VAR, "fstvar", 6),

        /** Subsequent occurrence of a local variable. */
        NextVar(NEXT_VAR, "nxtvar", 6),

        /** Subgoal followed by arguments and a call or lastcall. */
        Goal(GOAL, "goal", 6),

        /** Termination of a fact. */
        NoGoal(NOGOAL, "nogoal", 2),

        /** Cut operator. */
        Cut(CUT, "cut", 2),

        /** Built in predicate followed by its arguments. */
        BuiltIn(BUILTIN, "builtin", 6),

        /** Termination of a goal. */
        Call(CALL, "call", 6),

        /** Termination of a last goal. */
        LastCall(LASTCALL, "lastcall", 6);

        /** Holds a mapping of the instructions by byte code. */
        private static Map<Short, VAM2PInstructionSet> codeToValue =
            new HashMap<Short, VAM2PInstructionSet>()
            {
                {
                    for (VAM2PInstructionSet instruction : EnumSet.allOf(VAM2PInstructionSet.class))
                    {
                        put(instruction.code, instruction);
                    }
                }
            };

        /** Holds the byte representation of the instruction. */
        protected short code;

        /** Holds the human readable form of the instruction. */
        protected String pretty;

        /** Holds the length of the instruction in bytes. */
        protected int length;

        /**
         * Creates a new L0 instruction with the specified byte code.
         *
         * @param i      The byte code for the instruction.
         * @param pretty The human readable form of the isntruction.
         * @param length The length of the instruction plus arguments in bytes.
         */
        VAM2PInstructionSet(short i, String pretty, int length)
        {
            this.code = i;
            this.pretty = pretty;
            this.length = length;
        }

        /**
         * Creates an instruction from a byte code.
         *
         * @param  code The byte coded form of the instruction.
         *
         * @return An instruction matching the byte code, or <tt>null <tt>if no isntruction matches the code.
         */
        public static VAM2PInstructionSet fromCode(short code)
        {
            return codeToValue.get(code);
        }

        /**
         * Gets the byte coded representation of the instruction.
         *
         * @return The byte coded representation of the instruction.
         */
        public short getCode()
        {
            return code;
        }

        /**
         * Calculates the length of this instruction in bytes.
         *
         * @return The length of this instruction in bytes.
         */
        public int length()
        {
            return length;
        }
    }

    /** The instruction. */
    protected VAM2PInstructionSet mnemonic;

    /**
     * Creates an instruction for the specified mnemonic.
     *
     * @param mnemonic The instruction mnemonic.
     */
    public VAM2PInstruction(VAM2PInstructionSet mnemonic)
    {
        this.mnemonic = mnemonic;
    }

    /**
     * Creates an instruction with the mnemonic resolved from its byte encoded form.
     *
     * @param code The byte encoded form of the instruction mnemonic.
     */
    public VAM2PInstruction(short code)
    {
        this.mnemonic = VAM2PInstructionSet.fromCode(code);
    }

    /**
     * Gets the instruction mnemonic.
     *
     * @return The instruction mnemonic.
     */
    public VAM2PInstructionSet getMnemonic()
    {
        return mnemonic;
    }

    /**
     * Calculates the length of this instruction in bytes.
     *
     * @return The length of this instruction in bytes.
     */
    public long sizeof()
    {
        return mnemonic.length();
    }

    /**
     * Accepts a visitor.
     *
     * @param visitor The visitor to accept.
     */
    public void accept(Visitor<VAM2PInstruction> visitor)
    {
        visitor.visit(this);
    }
}
