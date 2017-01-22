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
package uk.theretiredprogrammer.gpssurvey.informationstore;

import uk.theretiredprogrammer.gpssurvey.Depth;
import uk.theretiredprogrammer.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData.Location;

/**
 * The Calculator for Location Data.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
