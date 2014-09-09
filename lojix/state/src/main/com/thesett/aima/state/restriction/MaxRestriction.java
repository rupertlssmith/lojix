package com.thesett.aima.state.restriction;

/**
 * Describes a maximum allowed value of an integer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Represent a maximum allowed value. </td></tr>
 * </table></pre>
 */
public class MaxRestriction implements TypeRestriction {
    private final long max;

    public MaxRestriction(long max) {
        this.max = max;
    }

    /** {@inheritDoc} */
    public String getName() {
        return "Max";
    }

    public long getMax() {
        return max;
    }
}
