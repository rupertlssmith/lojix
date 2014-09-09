package com.thesett.aima.state.restriction;

/**
 * TypeRestrictions denotes a set of restrictions that can be used to further constrain the values that instances of a
 * type can take.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Denote an extra restriction on the instances of a type. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TypeRestriction {
    /** Provides the name of the restriction. */
    String getName();
}
