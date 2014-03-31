package linsdale.gpssurvey.uiscreens;

import linsdale.gpssurvey.ui.GPSLogoZone;
import java.io.IOException;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.ScreenWithTick;
import linsdale.rpi.screenlib.SerialTFTDisplay;

/**
 *
 * @author richard
 */
public class SplashScreen extends ScreenWithTick  {


    public SplashScreen(SerialTFTDisplay display) throws IOException {
        super("splash", display, 5);
        addZone(new GPSLogoZone(display, "Starting..."));
    }
    
    @Override
    public void tick(Ticker t) throws IOException {
        t.cancel();
        View.displayNextScreen();
    }
}
