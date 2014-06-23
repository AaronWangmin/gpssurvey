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

import uk.org.rlinsdale.gpssurvey.Altitude;
import uk.org.rlinsdale.gpssurvey.Depth;
import uk.org.rlinsdale.gpssurvey.HDOP;
import uk.org.rlinsdale.gpssurvey.Latitude;
import uk.org.rlinsdale.gpssurvey.Longitude;
import uk.org.rlinsdale.gpssurvey.Position;
import uk.org.rlinsdale.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData.Location;

/**
 * Reference Point Calculator. Used to calculate a reference point, using a
 * average of a number of successive locations.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
