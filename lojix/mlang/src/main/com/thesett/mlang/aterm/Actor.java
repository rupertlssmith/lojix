package com.thesett.mlang.aterm;

import com.thesett.aima.logic.fol.BaseTerm;
import com.thesett.aima.logic.fol.Term;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Actor extends BaseTerm implements Term
{
    /** {@inheritDoc} */
    public Term getValue()
    {
        return this;
    }

    /** {@inheritDoc} */
    public void free()
    {
    }
}
