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
package com.thesett.aima.logic.fol.wam.debugger;

import java.util.concurrent.TimeUnit;

import com.thesett.aima.logic.fol.wam.debugger.controller.TopLevelStandaloneController;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactoryBuilder;

/**
 * Implements the main method for the debugger stand-alone application.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Attach the UI controller to the lifecycle for the application. </td>
 *     <td> {@link StartStopLifecycleBase}, {@link TopLevelStandaloneController} </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Main extends StartStopLifecycleBase
{
    /** The top-level UI controller for the application. */
    TopLevelStandaloneController controller =
        new TopLevelStandaloneController(ComponentFactoryBuilder.createComponentFactory(
                ComponentFactoryBuilder.SWING_FACTORY));

    /**
     * Launches the debugger as a stand-alone Swing application.
     *
     * @param args Command line parameters.
     */
    public static void main(String[] args)
    {
        try
        {
            final Main main = new Main();

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

    /**
     * {@inheritDoc}
     *
     * <p/>Starts the UI.
     */
    public void start()
    {
        controller.open();
        running();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Closes the UI.
     */
    public void shutdown()
    {
        controller.close();
        terminated();
    }
}
