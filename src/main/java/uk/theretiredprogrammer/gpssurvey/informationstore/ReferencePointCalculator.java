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

import uk.theretiredprogrammer.gpssurvey.Altitude;
import uk.theretiredprogrammer.gpssurvey.Depth;
import uk.theretiredprogrammer.gpssurvey.HDOP;
import uk.theretiredprogrammer.gpssurvey.Latitude;
import uk.theretiredprogrammer.gpssurvey.Longitude;
import uk.theretiredprogrammer.gpssurvey.Position;
import uk.theretiredprogrammer.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData.Location;

/**
 * Reference Point Calculator. Used to calculate a reference point, using a
 * average of a number of successive locations.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ReferencePointCalculator {

    private double latitude;
    private double longitude;
    private double altitude;
    private double depth;
    private double hDOP;
    private final static int REFSAMPLECOUNT = 5;
    private int gpsrefcounter;
    private int depthrefcounter;
    private boolean collectingreferencelocation;

    /**
     * Constructor
     */
    public ReferencePointCalculator() {
        collectingreferencelocation = false;
    }

    /**
     * Start to collect information to calculate the reference point
     */
    public final void start() {
        if (!collectingreferencelocation) {
            gpsrefcounter = REFSAMPLECOUNT;
            depthrefcounter = REFSAMPLECOUNT;
            collectingreferencelocation = true;
            depth = 0;
            latitude = 0;
            longitude = 0;
            altitude = 0;
            hDOP = 0;
        }
    }

    /**
     * Set a depth data point.
     *
     * @param depthdata the depth
     * @return the reference location if data calculation is complete else null
     */
    public Location depthDataPoint(Depth depthdata) {
        if (collectingreferencelocation && depthrefcounter > 0) {
            depth += depthdata.get();
            depthrefcounter--;
            return isDataCollectionComplete();
        }
        return null;
    }

    /**
     * Set a gps data point.
     *
     * @param gpsdata the gps data
     * @return the reference location if data calculation is complete else null
     */
    public Location gpsDataPoint(ConsolidatedGPSData gpsdata) {
        if (collectingreferencelocation && gpsrefcounter > 0) {
            latitude += gpsdata.position.latitude.get();
            longitude += gpsdata.position.longitude.get();
            altitude += gpsdata.altitude.get();
            hDOP += gpsdata.hDOP.get();
            gpsrefcounter--;
            return isDataCollectionComplete();
        }
        return null;
    }

    private Location isDataCollectionComplete() {
        if (gpsrefcounter == 0 && depthrefcounter == 0) {
            depth = depth / REFSAMPLECOUNT;
            latitude = latitude / REFSAMPLECOUNT;
            longitude = longitude / REFSAMPLECOUNT;
            altitude = altitude / REFSAMPLECOUNT;
            hDOP = hDOP / REFSAMPLECOUNT;
            collectingreferencelocation = false;
            return new Location(
                    new Position(new Latitude(latitude), new Longitude(longitude)),
                    new Altitude(altitude), new HDOP(hDOP), new Depth(depth));
        }
        return null;
    }
}
