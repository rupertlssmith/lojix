package com.thesett.aima.state.restriction;

/**
 * Describes a regular expression on allowable values of a string.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Represent allowable strings matching a regular expression. </td></tr>
 * </table></pre>
 */
public class RegexRestriction implements TypeRestriction {
    private final String pattern;

    public RegexRestriction(String pattern) {
        this.pattern = pattern;
    }

    /** {@inheritDoc} */
    public String getName() {
        return "Regex";
    }

    public String getPattern() {
        return pattern;
    }
}
