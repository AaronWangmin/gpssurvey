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
 * @author richard
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
