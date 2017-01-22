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
package uk.theretiredprogrammer.gpssurvey.ui;

import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.informationstore.Controller;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction.Button;

/**
 * Definitions of the main button actions used in the application.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
