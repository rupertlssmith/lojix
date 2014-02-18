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
package com.thesett.aima.logic.fol.vam.vamai.instructions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.ATOM;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.BUILTIN;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.CALL;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.CUT;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.FIRST_TEMP;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.FIRST_VAR;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.GOAL;
import static com.thesett.aima.logic.fol.vam.VAMInstructionCodes.INT;
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
 * VAMAIInstruction provides a structured in-memeory representation of the VAMAI instruction set.
 *
 * <p/>Instructions implement {@link com.thesett.common.util.Sizeable} reporting their length in bytes as their 'size
 * of' paramter. This is usefull when putting instructions into a Sizeable data structure, such as a list, for
 * calculating the total space required by all of the instructions in a listing.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encode the VAMAI instruction set.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMAIInstruction implements Acceptor<VAMAIInstruction>, Sizeable
{
    /** Defines the VAMAI virtual machine instruction set as constants. */
    public enum VAMAIInstructionSet
    {
        /** Integer. */
        Int(INT, "int", 6),

        /** Atom. */
        Atom(ATOM, "atom", 6),

        /** Empty list. */
        Nil(NIL, "nil", 1),

        /** List followed by its arguments. */
        List(LIST, "list", 1),

        /** Structure followed by its arguments. */
        Struct(STRUCT, "struct", 6),

        /** Void variable. */
        Void(VOID, "void", 2),

        /** First occurrence of a temporary variable. */
        FirstTemp(FIRST_TEMP, "fsttmp", 6),

        /** Subsequent occurrence of a temporary variable. */
        NextTemp(NEXT_TEMP, "nxttmp", 6),

        /** First occurrence of a local variable. */
        FirstVar(FIRST_VAR, "fstvar", 11),

        /** Subsequent occurrence of a local variable. */
        NextVar(NEXT_VAR, "nxtvar", 11),

        /** Subgoal followed by arguments and a call or lastcall. */
        Goal(GOAL, "goal", 10),

        /** Termination of a fact. */
        NoGoal(NOGOAL, "nogoal", 2),

        /** Cut operator. */
        Cut(CUT, "cut", 1),

        /** Built in predicate followed by its arguments. */
        BuiltIn(BUILTIN, "builtin", 1),

        /** Termination of a goal. */
        Call(CALL, "call", 2);

        /** Holds a mapping of the instructions by byte code. */
        private static Map<Short, VAMAIInstructionSet> codeToValue =
            new HashMap<Short, VAMAIInstructionSet>()
            {
                {
                    for (VAMAIInstructionSet instruction : EnumSet.allOf(VAMAIInstructionSet.class))
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
        VAMAIInstructionSet(short i, String pretty, int length)
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
        public static VAMAIInstructionSet fromCode(short code)
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

        /**
         * Prints the instructions mnemonic as a string.
         *
         * @return The instructions mnemonic as a string.
         */
        public String toString()
        {
            return pretty;
        }
    }

    /** The instruction. */
    protected VAMAIInstructionSet mnemonic;

    /**
     * Creates an instruction for the specified mnemonic.
     *
     * @param mnemonic The instruction mnemonic.
     */
    public VAMAIInstruction(VAMAIInstructionSet mnemonic)
    {
        this.mnemonic = mnemonic;
    }

    /**
     * Creates an instruction with the mnemonic resolved from its byte encoded form.
     *
     * @param code The byte encoded form of the instruction mnemonic.
     */
    public VAMAIInstruction(short code)
    {
        this.mnemonic = VAMAIInstructionSet.fromCode(code);
    }

    /**
     * Gets the instruction mnemonic.
     *
     * @return The instruction mnemonic.
     */
    public VAMAIInstructionSet getMnemonic()
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
    public void accept(Visitor<VAMAIInstruction> visitor)
    {
        visitor.visit(this);
    }
}
