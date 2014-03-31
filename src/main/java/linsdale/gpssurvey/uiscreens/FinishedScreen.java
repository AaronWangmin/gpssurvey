package linsdale.gpssurvey.uiscreens;

import java.io.IOException;
import linsdale.gpssurvey.ui.GPSLogoZone;
import linsdale.rpi.screenlib.Screen;
import linsdale.rpi.screenlib.SerialTFTDisplay;

/**
 *
 * @author richard
 */
public class FinishedScreen extends Screen {

     private final GPSLogoZone logo;

    public FinishedScreen(SerialTFTDisplay display) throws IOException {
        super("finished", display);
        addZone(logo = new GPSLogoZone(display, ""));
    }

    public void setMessage(String message) throws IOException {
        logo.setMessage(message);
    }
}
