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

import javax.swing.text.PlainDocument;

import com.thesett.common.util.Source;
import com.thesett.text.model.Row;
import com.thesett.text.model.Text;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextImpl extends PlainDocument implements Text
{
    public void caretAt(int row, int column)
    {
    }

    public void insert(char character)
    {
    }

    public void backspace()
    {
    }

    public void newline()
    {
    }

    public Source<Row> updates()
    {
        return null;
    }

    public Source<Row> full()
    {
        return null;
    }
}
