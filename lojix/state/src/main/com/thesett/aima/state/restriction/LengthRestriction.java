package com.thesett.aima.state.restriction;

/**
 * Describes a maximum length restriction.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Represent a maximum allowed length of a value. </td></tr>
 * </table></pre>
 */
public class LengthRestriction implements TypeRestriction {
    private final int length;

    public LengthRestriction(int length) {
        this.length = length;
    }

    /** {@inheritDoc} */
    public String getName() {
        return "Length";
    }

    public int getLength() {
        return length;
    }
}
