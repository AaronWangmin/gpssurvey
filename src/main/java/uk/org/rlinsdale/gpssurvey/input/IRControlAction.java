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
package uk.org.rlinsdale.gpssurvey.input;

import java.io.IOException;

/**
 * Actions interface for IR Controller Buttons.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
