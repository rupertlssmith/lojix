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
package com.thesett.aima.logic.fol.jpc.salt.jpl;

import static java.util.Arrays.asList;

import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.aima.logic.fol.builder.TermBuilder;

import org.jpc.salt.JpcTermWriter;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.FloatTerm;
import org.jpc.term.IntegerTerm;
import org.jpc.term.Term;
import org.jpc.term.Var;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * LojixTransformationTest checks that the term translators can map equivalent terms in Lojix and JPC into each other.
 *
 * <p/>The Prolog term to be translated is:
 *
 * <pre>
 * id(name2(atom1, -10, 10.5, A, _A))
 * </pre>
 */
public class LojixTransformationTest
{
    /** An interner used to intern variable and functor names. */
    VariableAndFunctorInterner interner = new VariableAndFunctorInternerImpl("vars", "functors");

    /** A term builder. */
    TermBuilder tb = new TermBuilder(interner);

    /** The term in Lojix. */
    com.thesett.aima.logic.fol.Term lojixTerm =
        tb.functor("id",
            tb.functor("name2", tb.atom("atom1"), tb.integer(-10), tb.real(10.5f), tb.var("A"), tb.var("_A")));

    /** The term in JPC. */
    Term jpcTerm =
        new Compound("id",
            asList(
                new Compound("name2",
                    asList(new Atom("atom1"), new IntegerTerm(-10), new FloatTerm(10.5), new Var("A"),
                        new Var("_A")))));

    @Test
    public void testLojixToLojix()
    {
        LojixTermWriter termWriter = new LojixTermWriter(interner);
        new LojixTermReader(lojixTerm, termWriter, interner).read();
        assertEquals(lojixTerm, termWriter.getFirst());
    }

    @Test
    public void testLojixToJpc()
    {
        JpcTermWriter jpcTermWriter = new JpcTermWriter();
        new LojixTermReader(lojixTerm, jpcTermWriter, interner).read();
        assertEquals(jpcTerm, jpcTermWriter.getFirst());
    }

    @Test
    public void testJpcToLojix()
    {
        LojixTermWriter jplTermWriter = new LojixTermWriter(interner);
        jpcTerm.read(jplTermWriter);
        assertEquals(lojixTerm, jplTermWriter.getFirst());
    }
}
