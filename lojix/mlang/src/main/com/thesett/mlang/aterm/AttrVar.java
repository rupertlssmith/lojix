package com.thesett.mlang.aterm;

import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class AttrVar extends Variable
{
    public AttrVar(int name, Term substitution, boolean anonymous)
    {
        super(name, substitution, anonymous);
    }
}
