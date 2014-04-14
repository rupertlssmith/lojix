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
package com.thesett.text.impl;

import javax.swing.text.Document;

import com.thesett.text.api.Controller;
import com.thesett.text.impl.model.TextImpl;
import com.thesett.text.model.Text;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ControllerImpl implements Controller
{
    UIFactory uiFactory = new UIFactory();
    Document document = new TextImpl();

    public Controller open()
    {
        uiFactory.createMainWindow();
        uiFactory.addTextPane(document);
        uiFactory.showConsole(document);
        uiFactory.showStatusBar(document);

        return this;
    }

    public Controller close()
    {
        return this;
    }

    public Controller update(Text model)
    {
        return this;
    }
}
