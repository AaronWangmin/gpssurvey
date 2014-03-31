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
package linsdale.gpssurvey.input;

import java.io.IOException;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public interface IRControlAction {
    
    public static enum Button {

        POWERDOWN, // the controller most likely will handle his one (std action - so will not be seen by screen)
        POWER,      // ... ditto
        A,
        B,
        C,
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SELECT
    }
    
    public boolean actionOnButton(Button button) throws IOException;
}
