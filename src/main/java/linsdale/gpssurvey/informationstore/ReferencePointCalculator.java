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
package linsdale.gpssurvey.informationstore;

import linsdale.gpssurvey.Altitude;
import linsdale.gpssurvey.Depth;
import linsdale.gpssurvey.HDOP;
import linsdale.gpssurvey.Latitude;
import linsdale.gpssurvey.Longitude;
import linsdale.gpssurvey.Position;
import linsdale.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import linsdale.gpssurvey.informationstore.LocationData.Location;

/**
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

    public ReferencePointCalculator() {
        collectingreferencelocation = false;
    }

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

    public Location depthDataPoint(Depth depthdata) {
        if (collectingreferencelocation && depthrefcounter > 0) {
            depth += depthdata.get();
            depthrefcounter--;
            return isDataCollectionComplete();
        }
        return null;
    }

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
