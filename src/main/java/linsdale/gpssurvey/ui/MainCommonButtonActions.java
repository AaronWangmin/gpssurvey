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
package linsdale.gpssurvey.ui;

import java.io.IOException;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.input.IRControlAction.Button;

/**
 * Definitions of the main button actions used in the application.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class MainCommonButtonActions {

    /**
     * Get the Button actions definition.
     *
     * Defines common actions for buttons A, B, C, Left and Right.
     *
     * @return the button actions
     * @throws IOException if problems.
     */
    public static ButtonActions getActions() throws IOException {
        return new ButtonActions()
                .addLine(Button.A, new RecordPositionAction())
                .addLine(Button.B, new ButtonBHandler())
                .addLine(Button.C, new ButtonCHandler())
                .addLine(Button.LEFT, new ButtonLeftHandler())
                .addLine(Button.RIGHT, new ButtonRightHandler());
    }

    /**
     * Record this point. The Action associated with button A.
     */
    public static class RecordPositionAction implements Action {

        /**
         * Action - Record this point.
         *
         * @throws IOException if problems
         */
        @Override
        public void execute() throws IOException {
            Controller.recordPoint();
        }
    }

    /**
     * Start / Stop record (track) recording. The Action associated with button
     * B.
     */
    public static class ButtonBHandler implements Action {

        /**
         * Action - Start / Stop record (track) recording
         *
         * @throws IOException if problems
         */
        @Override
        public void execute() throws IOException {
            Controller.startStopRecording();
        }
    }

    /**
     * Cancel record (track) recording. The Action associated with button C.
     */
    public static class ButtonCHandler implements Action {

        /**
         * Action - Cancel record (track) recording
         *
         * @throws IOException if problems
         */
        @Override
        public void execute() throws IOException {
            Controller.cancelRecording();
        }
    }

    /**
     * Move to previous Screen. The Action associated with Left button.
     */
    public static class ButtonLeftHandler implements Action {

        /**
         * Action - Move to previous Screen
         *
         * @throws IOException if problem
         */
        @Override
        public void execute() throws IOException {
            View.displayPreviousScreen();
        }
    }

    /**
     * Move to next Screen. The Action associated with the Right button.
     */
    public static class ButtonRightHandler implements Action {

        /**
         * Action - Move to next Screen
         *
         * @throws IOException if problem
         */
        @Override
        public void execute() throws IOException {
            View.displayNextScreen();
        }
    }
}
