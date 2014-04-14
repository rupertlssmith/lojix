/*
 * © Copyright Rupert Smith, 2005 to 2013.
 *
 * ALL RIGHTS RESERVED. Any unauthorized reproduction or use of this
 * material is prohibited. No part of this work may be reproduced or
 * transmitted in any form or by any means, electronic or mechanical,
 * including photocopying, recording, or by any information storage
 * and retrieval system without express written permission from the
 * author.
 */
package com.thesett.text.impl.model;

import com.thesett.common.util.Source;
import com.thesett.text.model.Cell;
import com.thesett.text.model.Row;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class RowImpl implements Row
{
    int row;

    public Source<Cell> updates()
    {
        return null;
    }

    public int getRow()
    {
        return row;
    }
}
