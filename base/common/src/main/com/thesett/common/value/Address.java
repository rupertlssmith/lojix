/*
 * Copyright The Sett Ltd, 2005 to 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.common.value;

import java.io.Serializable;

/**
 * Represents an address as a value object.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Represent a postal address
 * <tr><td>Represent an email address
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Address implements Serializable
{
    /** The first line of the address. */
    private String firstLine;

    /** The second line of the address. */
    private String secondLine;

    /** The town. */
    private String town;

    /** The country. */
    private String country;

    /** The post code. */
    private String postcode;

    /** The email address. */
    private String email;

    /** Creates an empty address. */
    public Address()
    {
    }

    /**
     * Builds an address.
     *
     * @param first    The first line of the address.
     * @param second   The second line of the address.
     * @param town     The town.
     * @param country  The country.
     * @param postcode The post code.
     * @param email    The email address.
     */
    public Address(String first, String second, String town, String country, String postcode, String email)
    {
        this.firstLine = first;
        this.secondLine = second;
        this.town = town;
        this.country = country;
        this.postcode = postcode;
        this.email = email;
    }

    /**
     * Gets the first line of the address.
     *
     * @return The first line of the address.
     */
    public String getFirstLine()
    {
        return firstLine;
    }

    /**
     * Gets the second line of the address.
     *
     * @return The second line of the address.
     */
    public String getSecondLine()
    {
        return secondLine;
    }

    /**
     * Gets the town.
     *
     * @return The town.
     */
    public String getTown()
    {
        return town;
    }

    /**
     * Gets the country.
     *
     * @return The country.
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * Gets the post code.
     *
     * @return The post code.
     */
    public String getPostcode()
    {
        return postcode;
    }

    /**
     * Gets the email address.
     *
     * @return The email address.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the first line of the address.
     *
     * @param firstLine The first line of the address.
     */
    public void setFirstLine(String firstLine)
    {
        this.firstLine = firstLine;
    }

    /**
     * Sets the seconds line of the address.
     *
     * @param secondLine The seconds line of the address.
     */
    public void setSecondLine(String secondLine)
    {
        this.secondLine = secondLine;
    }

    /**
     * Sets the town.
     *
     * @param town The town.
     */
    public void setTown(String town)
    {
        this.town = town;
    }

    /**
     * Sets the country of the address.
     *
     * @param country The country of the address.
     */
    public void setCountry(String country)
    {
        this.country = country;
    }

    /**
     * Sets the postcode of the address.
     *
     * @param postcode The postcode of the address.
     */
    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    /**
     * Sets the email of the address.
     *
     * @param email The email of the address.
     */
    public void setEmail(String email)
    {
        this.email = email;
    }
}
