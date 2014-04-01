/*
 * Copyright (C) 2014 Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package linsdale.gpssurvey.uiscreens;

import java.io.IOException;
import linsdale.gpssurvey.ui.Action;
import linsdale.gpssurvey.ui.ButtonActions;
import linsdale.gpssurvey.ui.SelectUsingButtonScreen;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.SerialTFTDisplay;
import linsdale.rpi.threadlib.MDTService.Exitcode;

/**
 * The Power down options screen - allows choice of exitcode or cancel.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
