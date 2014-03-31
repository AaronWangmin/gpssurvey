package linsdale.gpssurvey.informationstore;

import linsdale.gpssurvey.Depth;
import linsdale.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import linsdale.gpssurvey.informationstore.LocationData.Location;

/**
 *
 * @author richard
 */
public class LocationCalculator {


    public LocationCalculator() {
    }
    
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
    
    public boolean gpsDataPoint(LocationData ld, ConsolidatedGPSData gpsdata) {
        Location l = ld.getLocation();
        if (l != null) {
            l = l.clone();
        }
        ld.setLocation(gpsdata);
        if (l == null ) {
            return true; // if first time location then obvious this is update
        } 
        Location lnew = ld.getLocation();
        return !(l.getPosition().equals(lnew.getPosition()) && l.getAltitude().equals(lnew.getAltitude()));
    }

}
