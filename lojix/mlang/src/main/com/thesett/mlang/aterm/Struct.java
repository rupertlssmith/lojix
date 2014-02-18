package com.thesett.mlang.aterm;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Term;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Struct extends Functor
{
    public Struct(int name, Term[] arguments)
    {
        super(name, arguments);
    }
}
