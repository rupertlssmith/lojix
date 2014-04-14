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
 * Controller defines a set of functions for controlling the text UI.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Controller
{
    Controller open();

    Controller close();

    Controller update(Text model);
}
