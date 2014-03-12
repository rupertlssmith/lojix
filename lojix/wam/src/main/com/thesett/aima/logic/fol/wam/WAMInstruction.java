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
package com.thesett.aima.logic.fol.wam;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorName;
import com.thesett.aima.logic.fol.LinkageException;
import com.thesett.aima.logic.fol.Term;
import com.thesett.common.util.Sizeable;
import com.thesett.common.util.SizeableLinkedList;
import com.thesett.common.util.SizeableList;
import com.thesett.common.util.doublemaps.SymbolKey;

/**
 * WAMInstruction provides a structured in-memory representation of the WAM instruction set, as well as utilities to
 * emmit the instructions as byte code, disassemble them back into the structured representation, and pretty print them.
 *
 * <p/>Instructions implement {@link Sizeable} reporting their length in bytes as their 'size of'.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide the translation of the WAM instruction set down to bytes.
 * <tr><td> Provide dissasembly of byte encoded instructions back into structured instructions.
 * <tr><td> Calculate the length of an instruction in bytes. <td> {@link com.thesett.common.util.Sizeable}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WAMInstruction implements Sizeable
{
    /** Instruction to write out a struc onto the heap. */
    public static final byte PUT_STRUC = 0x01;

    /** The instruction to set a register as a variable. */
    public static final byte SET_VAR = 0x02;

    /** The instruction to set a register to a heap location. */
    public static final byte SET_VAL = 0x03;

    /** The instruction to compare a register to a structure on the heap. */
    public static final byte GET_STRUC = 0x04;

    /** The instruction to unify a register with a variable. */
    public static final byte UNIFY_VAR = 0x05;

    /** The instruction to unify a register with a location on the heap. */
    public static final byte UNIFY_VAL = 0x06;

    /** The instruction to copy a heap location into an argument register. */
    public static final byte PUT_VAR = 0x07;

    /** The instruction to copy a register into an argument register. */
    public static final byte PUT_VAL = 0x08;

    /** The instruction to unify an argument register with a variable. */
    public static final byte GET_VAR = 0x09;

    /** The instruction to unify a register with a location on the heap. */
    public static final byte GET_VAL = 0x0a;

    /** The instruction to copy a constant into an argument register. */
    public static final byte PUT_CONST = 0x12;

    /** The instruction to compare or bind a reference from a register to a constant. */
    public static final byte GET_CONST = 0x13;

    /** The instruction to write a constant onto the heap. */
    public static final byte SET_CONST = 0x14;

    /** The instruction to unify the heap with a constant. */
    public static final byte UNIFY_CONST = 0x15;

    /** The instruction to copy a list pointer into an argument register. */
    public static final byte PUT_LIST = 0x16;

    /** The instruction to compare or bind a reference from a register to a list pointer. */
    public static final byte GET_LIST = 0x17;

    /** The instruction to write an anonymous variable onto the heap. */
    public static final byte SET_VOID = 0x18;

    /** The instruction to unify with anonymous variables on the heap. */
    public static final byte UNIFY_VOID = 0x19;

    /** The instruction to call a predicate. */
    public static final byte CALL = 0x0b;

    /** The instruction to return from a called predicate. */
    public static final byte PROCEED = 0x0c;

    /** The stack frame allocation instruction. */
    public static final byte ALLOCATE = 0x0d;

    /** The stack frame de-allocation instruction. */
    public static final byte DEALLOCATE = 0x0e;

    /** The first clause try instruction. */
    public static final byte TRY_ME_ELSE = 0x0f;

    /** The middle clause retry instruction. */
    public static final byte RETRY_ME_ELSE = 0x10;

    /** The final clause trust or fail instruction. */
    public static final byte TRUST_ME = 0x11;

    /** The suspend operation. */
    public static final byte SUSPEND = 0x7f;

    // === Defines the addressing modes.

    /** Used to specify addresses relative to registers. */
    public static final byte REG_ADDR = 0x01;

    /** Used to specify addresses relative to the current stack frame. */
    public static final byte STACK_ADDR = 0x02;

    // === Defines the heap cell marker types.

    /** Indicates a reference data type. */
    public static final byte REF = 0x00;

    /** Indicates a structure data type. */
    public static final byte STR = 0x01;

    /** Indicates a constant atom data type. */
    public static final byte CON = 0x02;

    /** Indicates a list data type. */
    public static final byte LIS = 0x03;

    /** Defines the L0 virtual machine instruction set as constants. */
    public enum WAMInstructionSet
    {
        /** Instruction to write out a struc onto the heap. */
        PutStruc(PUT_STRUC, "put_struc", 7)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Fn(code, ip, instruction, machine);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                emmitCodeReg1Fn(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Fn(pretty, instruction);
            }
        },

        /** The instruction to set a register as a variable. */
        SetVar(SET_VAR, "set_var", 3),

        /** The instruction to set a register to a heap location. */
        SetVal(SET_VAL, "set_val", 3),

        /** The instruction to compare a register to a structure on the heap. */
        GetStruc(GET_STRUC, "get_struc", 7)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Fn(code, ip, instruction, machine);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                emmitCodeReg1Fn(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Fn(pretty, instruction);
            }
        },

        /** The instruction to unify a register with a variable. */
        UnifyVar(UNIFY_VAR, "unify_var", 3),

        /** The instruction to unify a register with a location on the heap. */
        UnifyVal(UNIFY_VAL, "unify_val", 3),

        /** The instruction to copy a heap location into an argument register. */
        PutVar(PUT_VAR, "put_var", 4)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Reg2(code, ip, instruction);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                emmitCodeReg1Reg2(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Reg2(pretty, instruction);
            }
        },

        /** The instruction to copy a register into an argument register. */
        PutVal(PUT_VAL, "put_val", 4)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Reg2(code, ip, instruction);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                emmitCodeReg1Reg2(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Reg2(pretty, instruction);
            }
        },

        /** The instruction to unify an argument register with a variable. */
        GetVar(GET_VAR, "get_var", 4)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Reg2(code, ip, instruction);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                emmitCodeReg1Reg2(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Reg2(pretty, instruction);
            }
        },

        /** The instruction to unify a register with a location on the heap. */
        GetVal(GET_VAL, "get_val", 4)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Reg2(code, ip, instruction);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                emmitCodeReg1Reg2(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Reg2(pretty, instruction);
            }
        },

        /** The instruction to copy a constant into an argument register. */
        PutConstant(PUT_CONST, "put_const", 7)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Fn(code, ip, instruction, machine);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
                throws LinkageException
            {
                emmitCodeReg1Fn(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Fn(pretty, instruction);
            }
        },

        /** The instruction to compare or bind a reference from a register to a constant. */
        GetConstant(GET_CONST, "get_const", 7)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleReg1Fn(code, ip, instruction, machine);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
                throws LinkageException
            {
                emmitCodeReg1Fn(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringReg1Fn(pretty, instruction);
            }
        },

        /** The instruction to write a constant onto the heap. */
        SetConstant(SET_CONST, "set_const", 5)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleFn(code, ip, instruction, machine);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
                throws LinkageException
            {
                emmitCodeFn(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringFn(pretty, instruction);
            }
        },

        /** The instruction to unify the heap with a constant. */
        UnifyConstant(UNIFY_CONST, "unify_const", 5)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                disassembleFn(code, ip, instruction, machine);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
                throws LinkageException
            {
                emmitCodeFn(codeBuf, code, instruction, machine);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return toStringFn(pretty, instruction);
            }
        },

        /** The instruction to copy a list pointer into an argument register. */
        PutList(PUT_LIST, "put_list", 3),

        /** The instruction to compare or bind a reference from a register to a list pointer. */
        GetList(GET_LIST, "get_list", 3),

        /** The instruction to write an anonymous variable onto the heap. */
        SetVoid(SET_VOID, "set_void", 2)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                // Do nothing as this instruction takes no arguments.
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                codeBuf.put(code);
                codeBuf.put(instruction.reg1);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty + " " + instruction.reg1;
            }
        },

        /** The instruction to unify with anonymous variables on the heap. */
        UnifyVoid(UNIFY_VOID, "unify_void", 2)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                // Do nothing as this instruction takes no arguments.
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                codeBuf.put(code);
                codeBuf.put(instruction.reg1);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty + " " + instruction.reg1;
            }
        },

        /** The instruction to call a predicate. */
        Call(CALL, "call", 6)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                int fn = code.getInt(ip + 5);
                int f = fn & 0x00ffffff;
                instruction.fn = machine.getDeinternedFunctorName(f);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
                throws LinkageException
            {
                int toCall = machine.internFunctorName(instruction.fn);

                WAMCallPoint callPointToCall = machine.resolveCallPoint(toCall);

                // Ensure that a valid call point was returned, otherwise a linkage error has occurred.
                if (callPointToCall == null)
                {
                    throw new LinkageException("Could not resolve call to " + instruction.fn + ".", null, null,
                        "Unable to resolve call to " + instruction.fn.getName() + "/" + instruction.fn.getArity() +
                        ".");
                }

                int entryPoint = callPointToCall.entryPoint;

                codeBuf.put(code);
                codeBuf.putInt(entryPoint);
                codeBuf.put((byte) instruction.fn.getArity());
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty + " " +
                    ((instruction.fn != null) ? (instruction.fn.getName() + "/" + instruction.fn.getArity()) : "");
            }
        },

        /** The instruction to return from a called predicate. */
        Proceed(PROCEED, "proceed", 1)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                // Do nothing as this instruction takes no arguments.
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                codeBuf.put(code);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty;
            }
        },

        /** The stack frame allocation instruction. */
        Allocate(ALLOCATE, "allocate", 2)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                // Do nothing as this instruction takes no arguments.
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                codeBuf.put(code);
                codeBuf.put(instruction.reg1);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty + " " + instruction.reg1;
            }
        },

        /** The stack frame deallocation instruction. */
        Deallocate(DEALLOCATE, "deallocate", 1)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                // Do nothing as this instruction takes no arguments.
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                codeBuf.put(code);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty;
            }
        },

        /** The first clause try instruction. */
        TryMeElse(TRY_ME_ELSE, "try_me_else", 5)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                int fn = code.getInt(ip + 5);
                int f = fn & 0x00ffffff;
                instruction.fn = machine.getDeinternedFunctorName(f);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
                throws LinkageException
            {
                int ip = codeBuf.position();

                // Intern the alternative forward label, and write it out as zero initially, for later completion.
                int toCall = machine.internFunctorName(instruction.fn);
                machine.reserveReferenceToLabel(toCall, ip + 1);

                codeBuf.put(code);
                codeBuf.putInt(0);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                WAMLabel label = (WAMLabel) instruction.fn;

                return pretty + " " + ((label != null) ? label.toPrettyString() : "");
            }
        },

        /** The middle clause retry instruction. */
        RetryMeElse(RETRY_ME_ELSE, "retry_me_else", 5)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                int fn = code.getInt(ip + 5);
                int f = fn & 0x00ffffff;
                instruction.fn = machine.getDeinternedFunctorName(f);
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
                throws LinkageException
            {
                int ip = codeBuf.position();

                // Resolve any forward reference to the label for this instruction.
                int label = machine.internFunctorName(instruction.label);
                machine.resolveLabelPoint(label, ip);

                // Intern the alternative forward label, and write it out as zero initially, for later completion.
                int toCall = machine.internFunctorName(instruction.fn);
                machine.reserveReferenceToLabel(toCall, ip + 1);

                codeBuf.put(code);
                codeBuf.putInt(0);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                WAMLabel label = (WAMLabel) instruction.fn;

                return pretty + " " + ((label != null) ? label.toPrettyString() : "");
            }
        },

        /** The final clause trust or fail instruction. */
        TrustMe(TRUST_ME, "trust_me", 1)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                // Do nothing as this instruction takes no arguments.
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                int ip = codeBuf.position();

                // Resolve any forward reference to the label for this instruction.
                int label = machine.internFunctorName(instruction.label);
                machine.resolveLabelPoint(label, ip);

                codeBuf.put(code);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty;
            }
        },

        /** The suspend on success instruction. */
        Suspend(SUSPEND, "suspend", 1)
        {
            /** {@inheritDoc} */
            protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
            {
                // Do nothing as this instruction takes no arguments.
            }

            /** {@inheritDoc} */
            public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            {
                codeBuf.put(code);
            }

            /** {@inheritDoc} */
            public String toString(WAMInstruction instruction)
            {
                return pretty;
            }
        };

        /** Holds a mapping of the instruction by byte code. */
        private static Map<Byte, WAMInstructionSet> codeToValue = new HashMap<Byte, WAMInstructionSet>();

        static
        {
            for (WAMInstructionSet instruction : EnumSet.allOf(WAMInstructionSet.class))
            {
                codeToValue.put(instruction.code, instruction);
            }
        }

        /** Holds the byte representation of the instruction. */
        protected byte code;

        /** Holds the human readable form of the instruction. */
        protected String pretty;

        /** Holds the length of the instruction in bytes. */
        protected int length;

        /**
         * Creates a new L0 instruction with the specified byte code.
         *
         * @param i      The byte code for the instruction.
         * @param pretty The human readable form of the instruction.
         * @param length The length of the instruction plus arguments in bytes.
         */
        private WAMInstructionSet(byte i, String pretty, int length)
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
         * @return An instruction matching the byte code, or <tt>null <tt>if no instruction matches the code.
         */
        public static WAMInstructionSet fromCode(byte code)
        {
            return codeToValue.get(code);
        }

        /**
         * Writes out the instruction plus arguments in the byte code format to the specified location within a code
         * buffer.
         *
         * @param  instruction The instruction, including its arguments.
         * @param  codeBuf     The code buffer to write to.
         * @param  machine     The binary machine to write the code into.
         *
         * @throws LinkageException If required symbols to link to cannot be found in the binary machine.
         */
        public void emmitCode(WAMInstruction instruction, ByteBuffer codeBuf, WAMMachine machine)
            throws LinkageException
        {
            codeBuf.put(code);
            codeBuf.put(instruction.mode1);
            codeBuf.put(instruction.reg1);
        }

        /**
         * Prints the human readable form of the instruction for debugging purposes.
         *
         * @param  instruction The instruction, including its arguments.
         *
         * @return The human readable form of the instruction for debugging purposes.
         */
        public String toString(WAMInstruction instruction)
        {
            return toStringReg1(pretty, instruction);
        }

        /**
         * Gets the byte coded representation of the instruction.
         *
         * @return The byte coded representation of the instruction.
         */
        public byte getCode()
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
         * Disassembles the arguments of an instruction. This is a default implementation to handle the most common
         * case; instructions with one register argument.
         *
         * @param instruction The instruction, including its arguments.
         * @param code        The code buffer.
         * @param ip          The address of the instruction to disassemble.
         * @param machine     The binary machine to disassemble from.
         */
        protected void disassembleArguments(WAMInstruction instruction, ByteBuffer code, int ip, WAMMachine machine)
        {
            disassembleReg1(code, ip, instruction, machine);
        }

        /**
         * Writes out the instruction plus arguments in the byte code format to the specified location within a code
         * buffer.
         *
         * @param codeBuf     The code buffer to write to.
         * @param code        The instruction mnemonic.
         * @param instruction The instruction, including its arguments.
         * @param machine     The binary machine to write the code into.
         */
        private static void emmitCodeReg1(ByteBuffer codeBuf, byte code, WAMInstruction instruction, WAMMachine machine)
        {
            codeBuf.put(code);
            codeBuf.put(instruction.mode1);
            codeBuf.put(instruction.reg1);
        }

        /**
         * Writes out the instruction plus arguments in the byte code format to the specified location within a code
         * buffer.
         *
         * @param codeBuf     The code buffer to write to.
         * @param code        The instruction mnemonic.
         * @param instruction The instruction, including its arguments.
         * @param machine     The binary machine to write the code into.
         */
        private static void emmitCodeReg1Reg2(ByteBuffer codeBuf, byte code, WAMInstruction instruction,
            WAMMachine machine)
        {
            codeBuf.put(code);
            codeBuf.put(instruction.mode1);
            codeBuf.put(instruction.reg1);
            codeBuf.put(instruction.reg2);
        }

        /**
         * Writes out the instruction plus arguments in the byte code format to the specified location within a code
         * buffer.
         *
         * @param codeBuf     The code buffer to write to.
         * @param code        The instruction mnemonic.
         * @param instruction The instruction, including its arguments.
         * @param machine     The binary machine to write the code into.
         */
        private static void emmitCodeReg1Fn(ByteBuffer codeBuf, byte code, WAMInstruction instruction,
            WAMMachine machine)
        {
            codeBuf.put(code);
            codeBuf.put(instruction.mode1);
            codeBuf.put(instruction.reg1);

            int arity = instruction.fn.getArity() << 24;
            int name = machine.internFunctorName(instruction.fn) & 0x00ffffff;
            codeBuf.putInt(arity | name);
        }

        /**
         * Writes out the instruction plus arguments in the byte code format to the specified location within a code
         * buffer.
         *
         * @param codeBuf     The code buffer to write to.
         * @param code        The instruction mnemonic.
         * @param instruction The instruction, including its arguments.
         * @param machine     The binary machine to write the code into.
         */
        private static void emmitCodeFn(ByteBuffer codeBuf, byte code, WAMInstruction instruction, WAMMachine machine)
        {
            codeBuf.put(code);

            int arity = instruction.fn.getArity() << 24;
            int name = machine.internFunctorName(instruction.fn) & 0x00ffffff;
            codeBuf.putInt(arity | name);
        }

        /**
         * Helper print function that prints an instruction with one register argument.
         *
         * @param  pretty      The pretty printed instruction mnenomic.
         * @param  instruction The instruction data.
         *
         * @return A pretty printed instruction.
         */
        private static String toStringReg1(String pretty, WAMInstruction instruction)
        {
            return pretty + " X" + instruction.reg1;
        }

        /**
         * Helper print function that prints an instruction with two register arguments.
         *
         * @param  pretty      The pretty printed instruction mnenomic.
         * @param  instruction The instruction data.
         *
         * @return A pretty printed instruction.
         */
        private static String toStringReg1Reg2(String pretty, WAMInstruction instruction)
        {
            return pretty + " X" + instruction.reg1 + ", A" + instruction.reg2;
        }

        /**
         * Helper print function that prints an instruction with one register argument and a functor reference.
         *
         * @param  pretty      The pretty printed instruction mnenomic.
         * @param  instruction The instruction data.
         *
         * @return A pretty printed instruction.
         */
        private static String toStringReg1Fn(String pretty, WAMInstruction instruction)
        {
            return pretty + " X" + instruction.reg1 + ", " +
                ((instruction.fn != null) ? (instruction.fn.getName() + "/" + instruction.fn.getArity()) : "");
        }

        /**
         * Helper print function that prints an instruction with one functor reference.
         *
         * @param  pretty      The pretty printed instruction mnenomic.
         * @param  instruction The instruction data.
         *
         * @return A pretty printed instruction.
         */
        private static String toStringFn(String pretty, WAMInstruction instruction)
        {
            return pretty + " " +
                ((instruction.fn != null) ? (instruction.fn.getName() + "/" + instruction.fn.getArity()) : "");
        }

        /**
         * Disassembles the arguments to an instruction that takes one register and one functor reference.
         *
         * @param code        The code buffer to disassemble from.
         * @param ip          The instruction pointer within the code buffer.
         * @param instruction The instruction to store the disassembles arguments in.
         * @param machine     The binary machine to disassemble from.
         */
        private static void disassembleReg1Fn(ByteBuffer code, int ip, WAMInstruction instruction, WAMMachine machine)
        {
            //instruction.mode1 = code[ip + 1];
            instruction.reg1 = code.get(ip + 1);

            int fn = code.getInt(ip + 2);
            int f = fn >> 8;
            instruction.fn = machine.getDeinternedFunctorName(f);
        }

        /**
         * Disassembles the arguments to an instruction that takes one register argument.
         *
         * @param code        The code buffer to disassemble from.
         * @param ip          The instruction pointer within the code buffer.
         * @param instruction The instruction to store the disassembles arguments in.
         * @param machine     The binary machine to disassemble from.
         */
        private static void disassembleReg1(ByteBuffer code, int ip, WAMInstruction instruction, WAMMachine machine)
        {
            instruction.mode1 = code.get(ip + 1);
            instruction.reg1 = code.get(ip + 2);
        }

        /**
         * Disassembles the arguments to an instruction that takes two register arguments.
         *
         * @param code        The code buffer to disassemble from.
         * @param ip          The instruction pointer within the code buffer.
         * @param instruction The instruction to store the disassembles arguments in.
         */
        private static void disassembleReg1Reg2(ByteBuffer code, int ip, WAMInstruction instruction)
        {
            instruction.mode1 = code.get(ip + 1);
            instruction.reg1 = code.get(ip + 2);
            instruction.reg2 = code.get(ip + 3);
        }

        /**
         * Disassembles the arguments to an instruction that takes one functor argument.
         *
         * @param code        The code buffer to disassemble from.
         * @param ip          The instruction pointer within the code buffer.
         * @param instruction The instruction to store the disassembles arguments in.
         * @param machine     The binary machine to disassemble from.
         */
        private static void disassembleFn(ByteBuffer code, int ip, WAMInstruction instruction, WAMMachine machine)
        {
            int fn = code.getInt(ip + 1);
            int f = fn >> 8;
            instruction.fn = machine.getDeinternedFunctorName(f);
        }
    }

    /** The optional address label of the instruction. */
    protected WAMLabel label;

    /** The instruction. */
    protected WAMInstructionSet mnemonic;

    /** Holds the addressing mode of the first register argument to the instruction. */
    protected byte mode1;

    /** Holds the first register argument to the instruction. */
    protected byte reg1;

    /** Holds the second register argument to the instruction. */
    protected byte reg2;

    /** Holds the functor (or label) argument to the instruction. */
    protected FunctorName fn;

    /** Holds the symbol key of the argument that is held in the first register of this instruction. */
    private SymbolKey symbolKeyReg1;

    /** Holds the functor name of the argument that is assigned to the first register of this instruction. */
    private Integer functorNameReg1;

    /**
     * Creates an instruction for the specified mnemonic.
     *
     * @param mnemonic The instruction mnemonic.
     */
    public WAMInstruction(WAMInstructionSet mnemonic)
    {
        this.mnemonic = mnemonic;
    }

    /**
     * Creates an instruction with the mnemonic resolved from its byte encoded form.
     *
     * @param code The byte encoded form of the instruction mnemonic.
     */
    public WAMInstruction(byte code)
    {
        this.mnemonic = WAMInstructionSet.fromCode(code);
    }

    /**
     * Creates an instruction for the specified mnemonic that takes one register and one functor argument.
     *
     * @param mnemonic The instruction mnemonic.
     * @param mode1    The addressing mode to use with the register argument.
     * @param reg1     The register argument.
     * @param fn       The functor argument.
     * @param reg1Term The term that is assigned or associated with reg1.
     */
    public WAMInstruction(WAMInstructionSet mnemonic, byte mode1, byte reg1, FunctorName fn, Term reg1Term)
    {
        this.mnemonic = mnemonic;
        this.mode1 = mode1;
        this.reg1 = reg1;
        this.fn = fn;

        // Record the symbol keys of the term that resulted in the creation of the instruction, and are associated
        // with reg1 of the instruction.
        symbolKeyReg1 = reg1Term.getSymbolKey();
        functorNameReg1 = reg1Term.isFunctor() ? ((Functor) reg1Term).getName() : null;
    }

    /**
     * Creates an instruction for the specified mnemonic that takes one register and one functor argument.
     *
     * @param mnemonic The instruction mnemonic.
     * @param mode1    The addressing mode to use with the register argument.
     * @param reg1     The register argument.
     * @param fn       The functor argument.
     */
    public WAMInstruction(WAMInstructionSet mnemonic, byte mode1, byte reg1, FunctorName fn)
    {
        this.mnemonic = mnemonic;
        this.mode1 = mode1;
        this.reg1 = reg1;
        this.fn = fn;
    }

    /**
     * Creates an instruction for the specified mnemonic that takes two register arguments.
     *
     * @param mnemonic The instruction mnemonic.
     * @param mode1    The addressing mode to use with the first register argument.
     * @param reg1     The first register argument.
     * @param reg2     The second register argument.
     */
    public WAMInstruction(WAMInstructionSet mnemonic, byte mode1, byte reg1, byte reg2)
    {
        this.mnemonic = mnemonic;
        this.mode1 = mode1;
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    /**
     * Creates an instruction for the specified mnemonic that takes a single register argument.
     *
     * @param mnemonic The instruction mnemonic.
     * @param mode1    The addressing mode to use with the register argument.
     * @param reg1     The single register argument.
     * @param reg1Term The term that is assigned or associated with reg1.
     */
    public WAMInstruction(WAMInstructionSet mnemonic, byte mode1, byte reg1, Term reg1Term)
    {
        this.mnemonic = mnemonic;
        this.mode1 = mode1;
        this.reg1 = reg1;

        // Record the symbol keys of the term that resulted in the creation of the instruction, and are associated
        // with reg1 of the instruction.
        symbolKeyReg1 = reg1Term.getSymbolKey();
        functorNameReg1 = reg1Term.isFunctor() ? ((Functor) reg1Term).getName() : null;
    }

    /**
     * Creates an instruction for the specified mnemonic that takes a single register argument.
     *
     * @param mnemonic The instruction mnemonic.
     * @param mode1    The addressing mode to use with the register argument.
     * @param reg1     The single register argument.
     */
    public WAMInstruction(WAMInstructionSet mnemonic, byte mode1, byte reg1)
    {
        this.mnemonic = mnemonic;
        this.mode1 = mode1;
        this.reg1 = reg1;
    }

    /**
     * Creates an instruction for the specified mnemonic that takes a single functor argument.
     *
     * @param mnemonic The instruction mnemonic.
     * @param fn       The functor argument.
     */
    public WAMInstruction(WAMInstructionSet mnemonic, FunctorName fn)
    {
        this.mnemonic = mnemonic;
        this.fn = fn;
    }

    /**
     * Creates a labelled instruction for the specified mnemonic that takes a single functor argument.
     *
     * @param label    The instructions address label.
     * @param mnemonic The instruction mnemonic.
     * @param fn       The functor argument.
     */
    public WAMInstruction(WAMLabel label, WAMInstructionSet mnemonic, WAMLabel fn)
    {
        this.label = label;
        this.mnemonic = mnemonic;
        this.fn = fn;
    }

    /**
     * Creates a labelled instruction for the specified mnemonic.
     *
     * @param label    The instructions address label.
     * @param mnemonic The instruction mnemonic.
     */
    public WAMInstruction(WAMLabel label, WAMInstructionSet mnemonic)
    {
        this.label = label;
        this.mnemonic = mnemonic;
    }

    /**
     * Disassembles the instructions from the specified byte buffer, starting at a given location (ip). An interner for
     * the functor names encountered in the instruction buffer must also be supplied, in order to look up the functor
     * names by encoded value.
     *
     * @param  ip      The start instruction pointer into the buffer.
     * @param  code    The code buffer.
     * @param  machine The binary machine to disassemble from.
     *
     * @return A list of instructions disassembles from the code buffer.
     */
    public static SizeableList<WAMInstruction> dissasemble(int ip, ByteBuffer code, WAMMachine machine)
    {
        SizeableList<WAMInstruction> result = new SizeableLinkedList<WAMInstruction>();

        int end = code.position();

        while (ip < end)
        {
            byte iCode = code.get(ip);

            WAMInstruction instruction = new WAMInstruction(iCode);

            //instruction.mnemonic = WAMInstructionSet.fromCode(iCode);
            instruction.mnemonic.disassembleArguments(instruction, code, ip, machine);

            result.add(instruction);

            ip += instruction.mnemonic.length();
        }

        return result;
    }

    /**
     * Gets the instruction mnemonic.
     *
     * @return The instruction mnemonic.
     */
    public WAMInstructionSet getMnemonic()
    {
        return mnemonic;
    }

    /**
     * Gets the addressing mode of the first register argument to the instruction.
     *
     * @return The addressing mode of the first register argument to the instruction.
     */
    public byte getMode1()
    {
        return mode1;
    }

    /**
     * Gets the first register to which the instruction applies.
     *
     * @return The first register to which the instruction applies.
     */
    public byte getReg1()
    {
        return reg1;
    }

    /**
     * Sets the first register to which the instruction applies.
     *
     * @param reg1 The first register to which the instruction applies.
     */
    public void setReg1(byte reg1)
    {
        this.reg1 = reg1;
    }

    /**
     * Gets the second register to which the instruction applies.
     *
     * @return The second register to which the instruction applies.
     */
    public byte getReg2()
    {
        return reg2;
    }

    /**
     * Sets the second register to which the instruction applies.
     *
     * @param reg2 The second register to which the instruction applies.
     */
    public void setReg2(byte reg2)
    {
        this.reg2 = reg2;
    }

    /**
     * Provides the symbol key of the argument that is assigned to the first register of this instruction.
     *
     * @return The symbol key, or <tt>null</tt> if none was associated with reg1.
     */
    public SymbolKey getSymbolKeyReg1()
    {
        return symbolKeyReg1;
    }

    /**
     * Provides the functor name of the argument that is assigned to the first register of this instruction.
     *
     * @return The functor name, or <tt>null</tt> if none was associated with reg1.
     */
    public Integer getFunctorNameReg1()
    {
        return functorNameReg1;
    }

    /**
     * Gets the functor argument, if any, to which the instruction applies.
     *
     * @return The functor argument to which the instruction applies, or <tt>null <tt>if there is none.
     */
    public FunctorName getFn()
    {
        return fn;
    }

    /**
     * Sets the functor argument to which the instruction applies.
     *
     * @param fn The functor argument to which the instruction applies.
     */
    public void setFn(FunctorName fn)
    {
        this.fn = fn;
    }

    /**
     * Gets the label for the instruction, if any is set.
     *
     * @return The label for the instruction, or <tt>null</tt> if none is set.
     */
    public WAMLabel getLabel()
    {
        return label;
    }

    /**
     * Writes out the instruction plus arguments in the byte code format to the specified location within a code buffer.
     *
     * @param  codeBuffer The code buffer to write to.
     * @param  machine    The binary machine to write the code into.
     *
     * @throws LinkageException If required symbols to link to cannot be found in the binary machine.
     */
    public void emmitCode(ByteBuffer codeBuffer, WAMMachine machine) throws LinkageException
    {
        mnemonic.emmitCode(this, codeBuffer, machine);
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
     * Prints the human readable form of the instruction for debugging purposes.
     *
     * @return The human readable form of the instruction for debugging purposes.
     */
    public String toString()
    {
        return mnemonic.toString(this);
    }
}
