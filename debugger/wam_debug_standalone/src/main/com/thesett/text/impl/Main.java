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

import java.util.concurrent.TimeUnit;

import com.thesett.text.api.Controller;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Main extends StartStopLifecycleBase
{
    Controller controller = new ControllerImpl();

    public static void main(String[] args)
    {
        try
        {
            Main main = new Main();
            main.start();

            Runtime.getRuntime().addShutdownHook(main.getShutdownHook());

            main.awaitTermination(1, TimeUnit.DAYS);
        }
        catch (InterruptedException e)
        {
            e = null;
            Thread.currentThread().interrupt();
        }
    }

    public void start()
    {
        controller.open();
        running();
    }

    public void shutdown()
    {
        controller.close();
        terminated();
    }
}
