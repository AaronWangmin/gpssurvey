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
package uk.org.rlinsdale.gpssurvey.informationstore;

import uk.org.rlinsdale.gpssurvey.Depth;
import uk.org.rlinsdale.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData.Location;

/**
 * The Calculator for Location Data.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class LocationCalculator {

    /**
     * Constructor
     */
    public LocationCalculator() {
    }

    /**
     * Update depth data in location data.
     *
     * @param ld the location data
     * @param depthdata the depth data to use for updating
     * @return true if location data is updated
     */
    public boolean depthDataPoint(LocationData ld, Depth depthdata) {
        Location l = ld.getLocation();
        if (l != null) {
            Depth previousdepth = l.getDepth();
            if (!depthdata.equals(previousdepth)) {
                l.setDepth(depthdata);
                return true;
            }
        }
        return false;
    }

    /**
     * Update GPS data in location data.
     *
     * @param ld the location data
     * @param gpsdata the gps data to be used for updating
     * @return true if location data is updated
     */
    public boolean gpsDataPoint(LocationData ld, ConsolidatedGPSData gpsdata) {
        Location l = ld.getLocation();
        if (l != null) {
            l = l.clone();
        }
        ld.setLocation(gpsdata);
        if (l == null) {
            return true; // if first time location then obvious this is update
        }
        Location lnew = ld.getLocation();
        return !(l.getPosition().equals(lnew.getPosition()) && l.getAltitude().equals(lnew.getAltitude()));
    }
}
