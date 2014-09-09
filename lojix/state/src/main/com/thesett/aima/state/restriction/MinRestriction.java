package com.thesett.aima.state.restriction;

/**
 * Describes a minimum allowed value of an integer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Represent a minimum allowed value. </td></tr>
 * </table></pre>
 */
public class MinRestriction implements TypeRestriction {
    private final long min;

    public MinRestriction(long min) {
        this.min = min;
    }

    /** {@inheritDoc} */
    public String getName() {
        return "Min";
    }

    public long getMin() {
        return min;
    }
}
