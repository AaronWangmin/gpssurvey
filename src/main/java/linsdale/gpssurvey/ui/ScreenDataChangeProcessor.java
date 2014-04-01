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
import linsdale.gpssurvey.informationstore.LocationData;

/**
 * Interface for use in screens with location data presentation (which is
 * dynamic).
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public interface ScreenDataChangeProcessor {

    /**
     * Called when location data has changed.
     *
     * @param data the location data
     * @throws IOException if problems
     */
    public void dataChanged(LocationData data) throws IOException;
}
