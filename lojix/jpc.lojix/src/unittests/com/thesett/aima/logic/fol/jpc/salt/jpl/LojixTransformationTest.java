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

import org.jpc.salt.JpcTermWriter;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.FloatTerm;
import org.jpc.term.IntegerTerm;
import org.jpc.term.Term;
import org.jpc.term.Var;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LojixTransformationTest
{
    jpl.Term t1Jpl =
        new jpl.Compound("id",
            new jpl.Term[]
            {
                new jpl.Compound("name2",
                    new jpl.Term[]
                    {
                        new jpl.Atom("atom1"), new jpl.Integer(-10), new jpl.Float(10.5), new jpl.Variable("A"),
                        new jpl.Variable("_A")
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
        //new LojixTermReader(t1Jpl, termWriter).read();
        assertEquals(t1Jpl, termWriter.getFirst());
    }

    @Test
    public void testJplToJpc()
    {
        JpcTermWriter jpcTermWriter = new JpcTermWriter();
        //new LojixTermReader(t1Jpl, jpcTermWriter).read();
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
