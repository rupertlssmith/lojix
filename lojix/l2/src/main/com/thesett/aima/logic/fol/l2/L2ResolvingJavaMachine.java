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

import java.util.Iterator;
import java.util.Set;

import com.thesett.aima.logic.fol.LinkageException;
import com.thesett.aima.logic.fol.Variable;
import static com.thesett.aima.logic.fol.l2.L2Instruction.ALLOCATE;
import static com.thesett.aima.logic.fol.l2.L2Instruction.CALL;
import static com.thesett.aima.logic.fol.l2.L2Instruction.DEALLOCATE;
import static com.thesett.aima.logic.fol.l2.L2Instruction.GET_STRUC;
import static com.thesett.aima.logic.fol.l2.L2Instruction.GET_VAL;
import static com.thesett.aima.logic.fol.l2.L2Instruction.GET_VAR;
import static com.thesett.aima.logic.fol.l2.L2Instruction.PROCEED;
import static com.thesett.aima.logic.fol.l2.L2Instruction.PUT_STRUC;
import static com.thesett.aima.logic.fol.l2.L2Instruction.PUT_VAL;
import static com.thesett.aima.logic.fol.l2.L2Instruction.PUT_VAR;
import static com.thesett.aima.logic.fol.l2.L2Instruction.REF;
import static com.thesett.aima.logic.fol.l2.L2Instruction.SET_VAL;
import static com.thesett.aima.logic.fol.l2.L2Instruction.SET_VAR;
import static com.thesett.aima.logic.fol.l2.L2Instruction.STACK_ADDR;
import static com.thesett.aima.logic.fol.l2.L2Instruction.STR;
import static com.thesett.aima.logic.fol.l2.L2Instruction.UNIFY_VAL;
import static com.thesett.aima.logic.fol.l2.L2Instruction.UNIFY_VAR;
import com.thesett.common.util.ByteBufferUtils;
import com.thesett.common.util.SequenceIterator;

/**
 * L2ResolvingJavaMachine is a byte code interpreter for L2 written in java. This is a direct implementation of the
 * instruction interpretations given in "Warren's Abstract Machine: A Tutorial Reconstruction". The pseudo algorithm
 * presented there can be read in the comments interspersed with the code. There are a couple of challenges to be solved
 * that are not presented in the book:
 *
 * <p/>
 * <ul>
 * <li>The book describes a STORE[addr] operation that loads or stores a heap, register or stack address. In the L1 and
 * L0 machines, only heap and register addresses had to be catered for. This made things easier because the registers
 * could be held at the top of the heap and a single common address range used for both. With increasing numbers of data
 * areas in the machine, and Java unable to use direct pointers into memory, a choice between having separate arrays for
 * each data area, or building all data areas within a single array has to be made. The single array approach was
 * chosen, because otherwise the addressing mode would need to be passed down into the 'deref' and 'unify' operations,
 * which would be complicated by having to choose amongst which of several arrays to operate on. An addressing mode has
 * had to be added to the instruction set, so that instructions loading data from registers or stack, can specify which.
 * Once addresses are resolved relative to the register or stack basis, the plain addresses offset to the base of the
 * whole data area are used, and it is these addresses that are passed to the 'deref' and 'unify' operations.
 * <li>The memory layout for the WAM is described in Appendix B.3. of the book. The same layout is usd for this machine
 * with the exception that the code area is held in a separate array. This follows the x86 machine convention of
 * separating code and data segments in memory, and also caters well for the sharing of the code area with the JVM as a
 * byte buffer.
 * <li>The deref operation is presented in the book as a recursive function. It was turned into an equivalent iterative
 * looping function instead. The deref operation returns multiple parameters, but as Java only supports single return
 * types, a choice had to be made between creating a simple class to hold the return types, or storing the return values
 * in member variables, and reading them from there. The member variables solution was chosen.</li>
 * </ul>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Execute compiled L2 programs and queries.
 * <tr><td> Provide access to the heap.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Think about unloading of byte code as well as insertion of of byte code. For example, would it be possible to
 *         unload a program, and replace it with a different one? This would require re-linking of any references to the
 *         original. So maybe want to add an index to reverse references. Call instructions should have their jumps
 *         pre-calculated for speed, but perhaps should also put the bare f/n into them, for the case where they may
 *         need to be updated. Also, add a semaphore to all call instructions, or at the entry point of all programs,
 *         this would be used to synchronize live updates to programs in a running machine, as well as to add debugging
 *         break points.
 * @todo   Think about ability to grow (and shrink?) the heap. Might be best to do this at the same time as the first
 *         garbage collector.
 */
public class L2ResolvingJavaMachine extends L2ResolvingMachine
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(L2ResolvingJavaMachine.class.getName()); */

    /** Used for tracing instruction executions. */
    /* private static final Logger trace = Logger.getLogger("TRACE.L2ResolvingJavaMachine"); */

    /** Defines the register capacity for the virtual machine. */
    private static final int REG_SIZE = 10;

    /* Defines the offset of the first register in the data area. */
    //private static final int REG_BASE = 0;

    /** Defines the heap size to use for the virtual machine. */
    private static final int HEAP_SIZE = 10000;

    /** Defines the offset of the base of the heap in the data area. */
    private static final int HEAP_BASE = REG_SIZE;

    /** Defines the stack size to use for the virtual machine. */
    private static final int STACK_SIZE = 10000;

    /** Defines the offset of the base of the stack in the data area. */
    private static final int STACK_BASE = HEAP_BASE + HEAP_SIZE;

    /** Defines the max unification stack depth for the virtual machine. */
    private static final int PDL_SIZE = 1000;

    /* Defines the offset of the base of the PDL in the data area. */
    //private static final int PDL_BASE = REG_SIZE + HEAP_SIZE + STACK_SIZE;

    /** Defines the highest address in the data area of the virtual machine. */
    private static final int TOP = REG_SIZE + HEAP_SIZE + STACK_SIZE + PDL_SIZE;

    /** Defines the initial code area size for the virtual machine. */
    private static final int CODE_SIZE = 10000;

    /** Holds the byte code. */
    private byte[] code;

    /** Holds the current load offset within the code area. */
    private int loadPoint;

    /** Holds the current instruction pointer into the code. */
    private int ip;

    /** Holds the enire data segment of the machine. All registers, heaps and stacks are held in here. */
    private int[] data;

    /** Holds the heap pointer. */
    private int hp;

    /** Holds the secondary heap pointer, used for the heap address of the next term to match. */
    private int sp;

    /** Holds the unification stack pointer. */
    private int up;

    /** Holds the environment base pointer. */
    private int ep;

    /** Holds the environment, top-of-stack pointer. */
    private int esp;

    /** Used to record whether the machine is in structure read or write mode. */
    private boolean writeMode;

    /** Holds the heap cell tag from the most recent dereference. */
    private byte derefTag;

    /** Holds the heap call value from the most recent dereference. */
    private int derefVal;

    /** Creates a unifying virtual machine for L2 with default heap sizes. */
    public L2ResolvingJavaMachine()
    {
        // Reset the machine to its initial state.
        reset();
    }

    /** {@inheritDoc} */
    public void emmitCode(L2CompiledClause clause) throws LinkageException
    {
        // Keep track of the offset into which the code was loaded.
        int entryPoint = loadPoint;
        int length = (int) clause.sizeof();

        // If the code is for a program clause, store the programs entry point in the call table.
        L2CallPoint callPoint;

        if (!clause.isQuery())
        {
            callPoint = setCodeAddress(clause.getHead().getName(), entryPoint, loadPoint - entryPoint);
        }
        else
        {
            callPoint = new L2CallPoint(loadPoint, length, -1);
        }

        // Emmit code for the clause into this machine.
        clause.emmitCode(loadPoint, code, this, callPoint);
        loadPoint += length;
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

        System.arraycopy(code, callPoint.entryPoint, result, 0, callPoint.length);

        return result;
    }

    /**
     * Resets the machine, to its initial state. This clears any programs from the machine, and clears all of its stacks
     * and heaps.
     */
    public void reset()
    {
        // Create fresh heaps, code areas and stacks.
        data = new int[TOP];
        code = new byte[CODE_SIZE];

        // Registers are on the top of the data area, the heap comes next.
        hp = REG_SIZE;
        sp = REG_SIZE;

        // The stack comes after the heap.
        ep = STACK_BASE;
        esp = ep;

        // The unification stack is a push down stack at the end of the data area.
        up = TOP;

        // Turn off write mode.
        writeMode = false;

        // Reset the instruction pointer to that start of the code area, ready for fresh code to be loaded there.
        ip = 0;
        loadPoint = 0;

        // Could probably not bother resetting these, but will do it anyway just to be sure.
        derefTag = 0;
        derefVal = 0;

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
     * Sets the maximum number of search steps that a search method may take. If it fails to find a solution before this
     * number of steps has been reached its search method should fail and return null. What exactly constitutes a single
     * step, and the granularity of the step size, is open to different interpretation by different search algorithms.
     * The guideline is that this is the maximum number of states on which the goal test should be performed.
     *
     * @param max The maximum number of states to goal test. If this is zero or less then the maximum number of steps
     *            will not be checked for.
     */
    public void setMaxSteps(int max)
    {
        throw new UnsupportedOperationException("L2ResolvingJavaMachine does not support max steps limit on search.");
    }

    /** {@inheritDoc} */
    protected int derefStack(int a)
    {
        /*log.fine("Stack deref from " + (a + ep + 3) + ", ep = " + ep);*/

        return deref(a + ep + 3);
    }

    /** {@inheritDoc} */
    protected boolean execute(L2CallPoint callPoint)
    {
        /*log.fine("protected boolean execute(L2CallPoint callPoint): called");*/

        ip = callPoint.entryPoint;
        uClear();

        boolean failed = false;

        // Holds the current continuation point.
        int cp = loadPoint;

        //while (!failed && (ip < code.length))
        while (!failed && (ip < loadPoint))
        {
            // Grab next instruction and switch on it.
            byte instruction = code[ip];

            switch (instruction)
            {
            // put_struc Xi, f/n:
            case PUT_STRUC:
            {
                // grab addr, f/n
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);
                int fn = ByteBufferUtils.getIntFromBytes(code, ip + 3);

                /*trace.fine(ip + ": PUT_STRUC " + printSlot(xi, mode) + ", " + fn);*/

                // heap[h] <- STR, h + 1
                data[hp] = (L2Instruction.STR << 24) | ((hp + 1) & 0xFFFFFF);

                data[hp + 1] = fn;

                // Xi <- heap[h]
                data[xi] = data[hp];

                // h <- h + 2
                hp += 2;

                // P <- instruction_size(P)
                ip += 7;

                break;
            }

            // set_var Xi:
            case SET_VAR:
            {
                // grab addr
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);

                /*trace.fine(ip + ": SET_VAR " + printSlot(xi, mode));*/

                // heap[h] <- REF, h
                data[hp] = (L2Instruction.REF << 24) | (hp & 0xFFFFFF);

                // Xi <- heap[h]
                data[xi] = data[hp];

                // h <- h + 1
                hp++;

                // P <- instruction_size(P)
                ip += 3;

                break;
            }

            // set_val Xi:
            case SET_VAL:
            {
                // grab addr
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);

                /*trace.fine(ip + ": SET_VAL " + printSlot(xi, mode));*/

                // heap[h] <- Xi
                data[hp] = data[xi];

                // h <- h + 1
                hp++;

                // P <- instruction_size(P)
                ip += 3;

                break;
            }

            // get_struc Xi,
            case GET_STRUC:
            {
                // grab addr, f/n
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);
                int fn = ByteBufferUtils.getIntFromBytes(code, ip + 3);

                /*trace.fine(ip + ": GET_STRUC " + printSlot(xi, mode) + ", " + fn);*/

                // addr <- deref(Xi);
                int addr = deref(xi);

                // switch STORE[addr]
                // int tmp = heap[addr];
                // byte tag = (byte)((tmp & 0xFF000000) >> 24);
                // int a = tmp & 0x00FFFFFF;
                byte tag = derefTag;
                int a = derefVal;

                switch (tag)
                {
                // case REF:
                case REF:
                {
                    // heap[h] <- STR, h + 1
                    data[hp] = (L2Instruction.STR << 24) | ((hp + 1) & 0xFFFFFF);

                    // heap[h+1] <- f/n
                    data[hp + 1] = fn;

                    // bind(addr, h)
                    data[addr] = (L2Instruction.REF << 24) | (hp & 0xFFFFFF);

                    // h <- h + 2
                    hp += 2;

                    // mode <- write
                    writeMode = true;

                    break;
                }

                // case STR, a:
                case STR:
                {
                    // if heap[a] = f/n
                    if (data[a] == fn)
                    {
                        // s <- a + 1
                        sp = a + 1;

                        // mode <- read
                        writeMode = false;
                    }
                    else
                    {
                        // fail
                        failed = true;
                    }

                    break;
                }

                default:
                {
                    throw new RuntimeException("Unkown tag type.");
                }
                }

                // P <- instruction_size(P)
                ip += 7;

                break;
            }

            // unify_var Xi:
            case UNIFY_VAR:
            {
                // grab addr
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);

                /*trace.fine(ip + ": UNIFY_VAR " + printSlot(xi, mode));*/

                // switch mode
                if (!writeMode)
                {
                    // case read:
                    // Xi <- heap[s]
                    data[xi] = data[sp];

                }
                else
                {
                    // case write:
                    // heap[h] <- REF, h
                    data[hp] = (L2Instruction.REF << 24) | (hp & 0xFFFFFF);

                    // Xi <- heap[h]
                    data[xi] = data[hp];

                    // h <- h + 1
                    hp++;
                }

                // s <- s + 1
                sp++;

                // P <- P + instruction_size(P)
                ip += 3;

                break;
            }

            // unify_val Xi:
            case UNIFY_VAL:
            {
                // grab addr
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);

                /*trace.fine(ip + ": UNIFY_VAL " + printSlot(xi, mode));*/

                // switch mode
                if (!writeMode)
                {
                    // case read:
                    // unify (Xi, s)
                    failed = !unify(xi, sp);
                }
                else
                {
                    // case write:
                    // heap[h] <- Xi
                    data[hp] = data[xi];

                    // h <- h + 1
                    hp++;
                }

                // s <- s + 1
                sp++;

                // P <- P + instruction_size(P)
                ip += 3;

                break;
            }

            // put_var Xn, Ai:
            case PUT_VAR:
            {
                // grab addr, Ai
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);
                byte ai = code[ip + 3];

                /*trace.fine(ip + ": PUT_VAR " + printSlot(xi, mode) + ", A" + ai);*/

                // heap[h] <- REF, H
                data[hp] = (L2Instruction.REF << 24) | (hp & 0xFFFFFF);

                // Xn <- heap[h]
                data[xi] = data[hp];

                // Ai <- heap[h]
                data[ai] = data[hp];

                // h <- h + 1
                hp++;

                // P <- P + instruction_size(P)
                ip += 4;

                break;
            }

            // put_val Xn, Ai:
            case PUT_VAL:
            {
                // grab addr, Ai
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);
                byte ai = code[ip + 3];

                /*trace.fine(ip + ": PUT_VAL " + printSlot(xi, mode) + ", A" + ai);*/

                // Ai <- Xn
                data[ai] = data[xi];

                // P <- P + instruction_size(P)
                ip += 4;

                break;
            }

            // get var Xn, Ai:
            case GET_VAR:
            {
                // grab addr, Ai
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);
                byte ai = code[ip + 3];

                /*trace.fine(ip + ": GET_VAR " + printSlot(xi, mode) + ", A" + ai);*/

                // Xn <- Ai
                data[xi] = data[ai];

                // P <- P + instruction_size(P)
                ip += 4;

                break;
            }

            // get_val Xn, Ai:
            case GET_VAL:
            {
                // grab addr, Ai
                byte mode = code[ip + 1];
                int xi = (int) code[ip + 2] + ((mode == STACK_ADDR) ? (ep + 3) : 0);
                byte ai = code[ip + 3];

                /*trace.fine(ip + ": GET_VAL " + printSlot(xi, mode) + ", A" + ai);*/

                // unify (Xn, Ai)
                failed = !unify(xi, ai);

                // P <- P + instruction_size(P)
                ip += 4;

                break;
            }

            // call @(p/n):
            case CALL:
            {
                // grab @(p/n)
                int pn = ByteBufferUtils.getIntFromBytes(code, ip + 1);

                /*trace.fine(ip + ": CALL " + pn);*/

                // Ensure that the predicate to call is known and linked int, otherwise fail.
                if (pn == -1)
                {
                    failed = true;

                    break;
                }

                // CP <- P + instruction_size(P)
                cp = ip + 5;

                // P <- @(p/n)
                ip = pn;

                break;
            }

            // proceed:
            case PROCEED:
            {
                /*trace.fine(ip + ": PROCEED");*/

                // P <- CP
                ip = cp;

                break;
            }

            // allocate N:
            case ALLOCATE:
            {
                // grab N
                int n = (int) code[ip + 1];

                // STACK[newE] <- E
                data[esp] = ep;

                // STACK[E + 1] <- CP
                data[esp + 1] = cp;

                // STACK[E + 2] <- N
                data[esp + 2] = n;

                // E <- newE
                // newE <- E + n + 3
                ep = esp;
                esp = esp + n + 3;

                /*trace.fine(ip + ": ALLOCATE " + n + " [ep=" + ep + ",esp=" + esp + "]");*/

                // P <- P + instruction_size(P)
                ip += 2;

                break;
            }

            // deallocate:
            case DEALLOCATE:
            {
                // E <- STACK[E]
                esp = ep;
                ep = data[ep];

                /*trace.fine(ip + ": DEALLOCATE" + " [ep=" + ep + ",esp=" + esp + "]");*/

                // P <- STACK[E + 1]
                ip = data[ep + 1];

                break;
            }

            // Unknown instruction encountered, something went very wrong...
            default:
            {
                failed = true;
                break;
            }
            }
        }

        return !failed;
    }

    /** {@inheritDoc} */
    protected int deref(int a)
    {
        // tag, value <- STORE[a]
        int addr = a;
        int tmp = data[a];
        derefTag = (byte) ((tmp & 0xFF000000) >> 24);
        derefVal = tmp & 0x00FFFFFF;

        // while tag = REF and value != a
        while ((derefTag == L2Instruction.REF))
        {
            // tag, value <- STORE[a]
            addr = derefVal;
            tmp = data[derefVal];
            derefTag = (byte) ((tmp & 0xFF000000) >> 24);
            tmp = tmp & 0x00FFFFFF;

            // Break on free var.
            if (derefVal == tmp)
            {
                break;
            }

            derefVal = tmp;
        }

        return addr;
    }

    /**
     * Gets the heap cell tag for the most recent dereference operation.
     *
     * @return The heap cell tag for the most recent dereference operation.
     */
    protected byte getDerefTag()
    {
        return derefTag;
    }

    /**
     * Gets the heap cell value for the most recent dereference operation.
     *
     * @return The heap cell value for the most recent dereference operation.
     */
    protected int getDerefVal()
    {
        return derefVal;
    }

    /**
     * Gets the value of the heap cell at the specified location.
     *
     * @param  addr The address to fetch from the heap.
     *
     * @return The heap cell at the specified location.
     */
    protected int getHeap(int addr)
    {
        return data[addr];
    }

    /**
     * Attempts to unify structures or references on the heap, given two references to them. Structures are matched
     * element by element, free references become bound.
     *
     * @param  a1 The address of the first structure or reference.
     * @param  a2 The address of the second structure or reference.
     *
     * @return <tt>true</tt> if the two structures unify, <tt>false</tt> otherwise.
     */
    private boolean unify(int a1, int a2)
    {
        // pdl.push(a1)
        // pdl.push(a2)
        uPush(a1);
        uPush(a2);

        // fail <- false
        boolean fail = false;

        // while !empty(PDL) and not failed
        while (!uEmpty() && !fail)
        {
            // d1 <- deref(pdl.pop())
            // d2 <- deref(pdl.pop())
            // t1, v1 <- STORE[d1]
            // t2, v2 <- STORE[d2]
            int d1 = deref(uPop());
            int t1 = derefTag;
            int v1 = derefVal;

            int d2 = deref(uPop());
            int t2 = derefTag;
            int v2 = derefVal;

            // if (d1 != d2)
            if (d1 != d2)
            {
                // if (t1 = REF or t2 = REF)
                // bind(d1, d2)
                if ((t1 == L2Instruction.REF))
                {
                    data[d1] = (L2Instruction.REF << 24) | (d2 & 0xFFFFFF);
                }
                else if (t2 == L2Instruction.REF)
                {
                    data[d2] = (L2Instruction.REF << 24) | (d1 & 0xFFFFFF);
                }
                else
                {
                    // f1/n1 <- STORE[v1]
                    // f2/n2 <- STORE[v2]
                    int fn1 = data[v1];
                    int fn2 = data[v2];
                    byte n1 = (byte) (fn1 & 0xFF);

                    // if f1 = f2 and n1 = n2
                    if (fn1 == fn2)
                    {
                        // for i <- 1 to n1
                        for (int i = 1; i <= n1; i++)
                        {
                            // pdl.push(v1 + i)
                            // pdl.push(v2 + i)
                            uPush(v1 + i);
                            uPush(v2 + i);
                        }
                    }
                    else
                    {
                        // fail <- true
                        fail = true;
                    }
                }
            }
        }

        return !fail;
    }

    /**
     * Pushes a value onto the unification stack.
     *
     * @param val The value to push onto the stack.
     */
    private void uPush(int val)
    {
        data[--up] = val;
    }

    /**
     * Pops a value from the unification stack.
     *
     * @return The top value from the unification stack.
     */
    private int uPop()
    {
        return data[up++];
    }

    /** Clears the unification stack. */
    private void uClear()
    {
        up = TOP;
    }

    /**
     * Checks if the unification stack is empty.
     *
     * @return <tt>true</tt> if the unification stack is empty, <tt>false</tt> otherwise.
     */
    private boolean uEmpty()
    {
        return up >= TOP;
    }

    /**
     * Pretty prints a variable allocation slot for tracing purposes.
     *
     * @param  xi   The allocation slot to print.
     * @param  mode The addressing mode, stack or register.
     *
     * @return The pretty printed slot.
     */
    private String printSlot(int xi, int mode)
    {
        return ((mode == STACK_ADDR) ? "Y" : "X") + ((mode == STACK_ADDR) ? (xi - ep - 3) : xi);
    }
}
