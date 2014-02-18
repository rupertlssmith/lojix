package com.thesett.mlang.aterm;

import com.thesett.aima.logic.fol.Term;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DVar extends AttrVar
{
    public DVar(int name, Term substitution, boolean anonymous)
    {
        super(name, substitution, anonymous);
    }
}
