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
package com.thesett.text.model;

import com.thesett.common.util.Source;

/**
 * Text defines a model describing a buffer of text.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Text
{
    void caretAt(int row, int column);

    void insert(char character);

    void backspace();

    void newline();

    Source<Row> updates();

    Source<Row> full();
}
