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
package com.thesett.aima.logic.fol.vam;

/**
 * Defines the byte encoded instruction codes for all VAM two pointer instruction sets. The instruction codes have been
 * selected as marks on a Golomb ruler, to ensure that the sums of pairwise combinations of them are always unique.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Contain instruction codes.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMInstructionCodes
{
    /** Integer or Atom. */
    public static final short CONST = 0;

    /** Integer. */
    public static final short INT = 5;

    /** Atom. */
    public static final short ATOM = 7;

    /** Empty list. */
    public static final short NIL = 17;

    /** List followed by its arguments. */
    public static final short LIST = 52;

    /** Structure followed by its arguments. */
    public static final short STRUCT = 56;

    /** Void variable. */
    public static final short VOID = 67;

    /** First occurrence of a temporary variable. */
    public static final short FIRST_TEMP = 80;

    /** Subsequent occurrence of a temporary variable. */
    public static final short NEXT_TEMP = 81;

    /** First occurrence of a local variable. */
    public static final short FIRST_VAR = 100;

    /** Subsequent occurrence of a local variable. */
    public static final short NEXT_VAR = 122;

    /** Subgoal followed by arguments and a call or lastcall. */
    public static final short GOAL = 138;

    /** Termination of a fact. */
    public static final short NOGOAL = 159;

    /** Cut operator. */
    public static final short CUT = 165;

    /** Built in predicate followed by its arguments. */
    public static final short BUILTIN = 168;

    /** Termination of a goal. */
    public static final short CALL = 191;

    /** Termination of a last goal. */
    public static final short LASTCALL = 199;
}
