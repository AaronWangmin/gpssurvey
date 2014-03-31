package linsdale.gpssurvey.uiscreens;

import linsdale.gpssurvey.ui.WaitingForZone;
import java.io.IOException;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.ScreenWithTick;
import linsdale.rpi.screenlib.SerialTFTDisplay;

/**
 *
 * @author richard
 */
public class WaitingForGPSScreen extends ScreenWithTick {

    private final WaitingForZone zone;

    public WaitingForGPSScreen(SerialTFTDisplay display) throws IOException {
        super("waiting for GPS", display, 1);
        addZone(zone = new WaitingForZone(display,
                "Waiting for GPS lock"));
    }

    @Override
    public void tick(Ticker t) throws IOException {
        if (Controller.getLocationData().getLocation() != null) {
            View.displayNextScreen();
        } else {
            zone.onTick();
            View.displayRepaint();
        }
    }
}
