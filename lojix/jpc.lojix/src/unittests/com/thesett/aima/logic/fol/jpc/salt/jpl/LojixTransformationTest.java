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
    VariableAndFunctorInterner interner = new VariableAndFunctorInternerImpl("vars", "functors");

    com.thesett.aima.logic.fol.Term t1Jpl =
        new com.thesett.aima.logic.fol.Functor(interner.internFunctorName("id", 1),
            new com.thesett.aima.logic.fol.Term[]
            {
                new com.thesett.aima.logic.fol.Functor(interner.internFunctorName("name2", 5),
                    new com.thesett.aima.logic.fol.Term[]
                    {
                        new com.thesett.aima.logic.fol.Functor(interner.internFunctorName("atom1", 0), null),
                        new com.thesett.aima.logic.fol.IntLiteral(-10),
                        new com.thesett.aima.logic.fol.FloatLiteral(10.5f),
                        new com.thesett.aima.logic.fol.Variable(interner.internVariableName("A"), null, false),
                        new com.thesett.aima.logic.fol.Variable(interner.internVariableName("_A"), null, true)
                    })
            });

    Term t1Jpc =
        new Compound("id",
            asList(
                new Compound("name2",
                    asList(new Atom("atom1"), new IntegerTerm(-10), new FloatTerm(10.5), new Var("A"),
                        new Var("_A")))));

    @Test
    public void testJplToJpl()
    {
        LojixTermWriter termWriter = new LojixTermWriter();
        new LojixTermReader(t1Jpl, termWriter).read();
        assertEquals(t1Jpl, termWriter.getFirst());
    }

    @Test
    public void testJplToJpc()
    {
        JpcTermWriter jpcTermWriter = new JpcTermWriter();
        new LojixTermReader(t1Jpl, jpcTermWriter).read();
        assertEquals(t1Jpc, jpcTermWriter.getFirst());
    }

    @Test
    public void testJpcToJpl()
    {
        LojixTermWriter jplTermWriter = new LojixTermWriter();
        t1Jpc.read(jplTermWriter);
        assertEquals(t1Jpl, jplTermWriter.getFirst());
    }
}
