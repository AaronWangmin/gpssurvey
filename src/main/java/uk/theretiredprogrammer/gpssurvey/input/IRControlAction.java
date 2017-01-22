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
package uk.theretiredprogrammer.gpssurvey.input;

import java.io.IOException;

/**
 * Actions interface for IR Controller Buttons.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public interface IRControlAction {

    /**
     * Button Definitions
     */
    public static enum Button {

        /**
         * The Power-down button on the IR plate
         */
        POWERDOWN, // the controller most likely will handle his one (std action - so will not be seen by screen)

        /**
         * Button Power on IR controller
         */
        POWER, // ... ditto

        /**
         * Button A on IR controller
         */
        A,
        /**
         * Button B on IR controller
         */
        B,
        /**
         * Button C on IR controller
         */
        C,
        /**
         * Button Up on IR controller
         */
        UP,
        /**
         * Button Down on IR controller
         */
        DOWN,
        /**
         * Button Left on IR controller
         */
        LEFT,
        /**
         * Button Right on IR controller
         */
        RIGHT,
        /**
         * Button Select on IR controller
         */
        SELECT
    }

    /**
     * Button Action Handler.
     *
     * @param button the button pressed
     * @return true if button action processed
     * @throws IOException if problems
     */
    public boolean actionOnButton(Button button) throws IOException;
}
