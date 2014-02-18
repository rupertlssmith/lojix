package com.thesett.mlang.aterm;

import com.thesett.aima.logic.fol.BaseTerm;
import com.thesett.aima.logic.fol.Term;

import java.util.LinkedList;
import java.util.List;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Program extends BaseTerm implements Term
{
    private List<ProgramBody> programBody = new LinkedList<ProgramBody>();

    /** {@inheritDoc} */
    public Term getValue()
    {
        return this;
    }

    /** {@inheritDoc} */
    public void free()
    {
    }

    public List<ProgramBody> getProgramBody()
    {
        return programBody;
    }
}
