/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
import java.util.Date;

/**
 * Value object used for passing credit card details.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Represent credit card information
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CCBillingDetails implements Serializable
{
    /** The card holders name. */
    private String holdersName;

    /** The credit card number. */
    private String number;

    /** The credit cart security number. */
    private String securityNumber;

    /** The credit card type. */
    private String type;

    /** The credit card expiry date. */
    private Date expiryDate;

    /** The credit card start date. */
    private Date startDate;

    /** The credit card issue number. */
    private String issueNumber;

    /**
     * Gets the card holders name.
     *
     * @return The card holders name.
     */
    public String getHoldersName()
    {
        return holdersName;
    }

    /**
     * Gets the card number.
     *
     * @return The card number.
     */
    public String getNumber()
    {
        return number;
    }

    /**
     * Gets the card security number.
     *
     * @return The card security number.
     */
    public String getSecurityNumber()
    {
        return securityNumber;
    }

    /**
     * Gets the card type.
     *
     * @return The card type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Gets the card expiry date.
     *
     * @return The card expiry date.
     */
    public Date getExpiryDate()
    {
        return expiryDate;
    }

    /**
     * Gets the card start date.
     *
     * @return The card start date.
     */
    public Date getStartDate()
    {
        return startDate;
    }

    /**
     * Gets the card issue number.
     *
     * @return The card issue number.
     */
    public String getIssueNumber()
    {
        return issueNumber;
    }

    /**
     * Sets the card holders name.
     *
     * @param holdersName The card holders name.
     */
    public void setHoldersName(String holdersName)
    {
        this.holdersName = holdersName;
    }

    /**
     * Sets the card number.
     *
     * @param number The card number.
     */
    public void setNumber(String number)
    {
        this.number = number;
    }

    /**
     * Sets the card security number.
     *
     * @param securityNumber The card security number.
     */
    public void setSecurityNumber(String securityNumber)
    {
        this.securityNumber = securityNumber;
    }

    /**
     * Sets the card type.
     *
     * @param type The card type.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Sets the card expiry date.
     *
     * @param expiryDate The card expiry date.
     */
    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    /**
     * Sets the card start date.
     *
     * @param startDate The card start date.
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Sets the card issue number.
     *
     * @param issueNumber The card issue number.
     */
    public void setIssueNumber(String issueNumber)
    {
        this.issueNumber = issueNumber;
    }
}
