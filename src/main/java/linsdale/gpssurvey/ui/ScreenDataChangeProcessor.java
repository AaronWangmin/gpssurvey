package linsdale.gpssurvey.ui;

import java.io.IOException;
import linsdale.gpssurvey.informationstore.LocationData;

/**
 *
 * @author richard
 */
public interface ScreenDataChangeProcessor {
    
    public void dataChanged(LocationData data) throws IOException;
}
