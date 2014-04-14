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
package com.thesett.text.api;

import com.thesett.text.model.Text;

/**
 * Notifier defines a set of callback functions for updates on the status of the text UI.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Notifier
{
    Notifier resize(int columns, int rows);

    Notifier caret(int column, int row);

    Notifier update(Text mode);
}
