/*
 * Copyright 2014-2017 Richard Linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.gpssurvey.uiscreens;

import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.ui.Action;
import uk.theretiredprogrammer.gpssurvey.ui.ButtonActions;
import uk.theretiredprogrammer.gpssurvey.ui.SelectUsingButtonScreen;
import uk.theretiredprogrammer.gpssurvey.ui.View;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService.Exitcode;

/**
 * The Power down options screen - allows choice of exitcode or cancel.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SelectPowerDownOptionScreen extends SelectUsingButtonScreen {

    /**
     * Constructor.
     *
     * @param display the display device
     * @throws IOException if problems
     */
    public SelectPowerDownOptionScreen(SerialTFTDisplay display) throws IOException {
        super("SelectPowerDownOption", display,
                new ButtonActions("SYSTEM EXIT REQUEST", "Cancel", new CancelAction())
                .addLine(Button.A, "Power Off", new PowerOffAction())
                .addLine(Button.B, "Reboot", new RebootAction())
                .addLine(Button.C, "Restart GPS", new RestartAction()));
    }

    /**
     * The action class for cancel
     */
    public static class CancelAction implements Action {

        /**
         * Action - cancel
         *
         * @throws IOException if problems
         */
        @Override
        public void execute() throws IOException {
            View.displayPreviousScreenSet();
        }
    }

    /**
     * The action class for power off exit
     */
    public static class PowerOffAction implements Action {

        /**
         * Action - power off exit
         *
         * @throws IOException if problems
         */
        @Override
        public void execute() throws IOException {
            View.displayNextScreenAndExit(Exitcode.EXIT_POWEROFF, "Powering down...");
        }
    }

    /**
     * The action class for reboot exit
     */
    public static class RebootAction implements Action {

        /**
         * Action - reboot exit
         *
         * @throws IOException if problems
         */
        @Override
        public void execute() throws IOException {
            View.displayNextScreenAndExit(Exitcode.EXIT_REBOOT, "Rebooting...");
        }
    }

    /**
     * The action class for program restart exit
     */
    public static class RestartAction implements Action {

        /**
         * Action - program restart
         *
         * @throws IOException if problems
         */
        @Override
        public void execute() throws IOException {
            View.displayNextScreenAndExit(Exitcode.EXIT_OK, "Restarting...");
        }
    }
}
