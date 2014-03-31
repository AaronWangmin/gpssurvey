package linsdale.gpssurvey.uiscreens;

import java.io.IOException;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.informationstore.LocationData;
import linsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import linsdale.gpssurvey.ui.View;
import linsdale.gpssurvey.ui.WaitingForZone;
import linsdale.rpi.screenlib.ScreenWithTick;
import linsdale.rpi.screenlib.SerialTFTDisplay;

/**
 *
 * @author richard
 */
public class ObtainingReferenceLocationInformationScreen extends ScreenWithTick
        implements ScreenDataChangeProcessor {

    private final WaitingForZone zone;

    public ObtainingReferenceLocationInformationScreen(SerialTFTDisplay display) throws IOException {
        super("obtaining reference location information", display, 1);
        addZone(zone = new WaitingForZone(display, "Obtaining Reference Location"));
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        Controller.obtainReferenceLocation();
    }

    @Override
    public void tick(Ticker t) throws IOException {
        zone.onTick();
        View.displayRepaint();
    }

    @Override
    public void dataChanged(LocationData ld) throws IOException {
        if (ld.getReferenceLocation() != null) {
            View.displayNewScreenSet("main");
        }
    }
}
