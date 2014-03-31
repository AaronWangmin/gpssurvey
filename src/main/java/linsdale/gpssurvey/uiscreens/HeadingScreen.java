package linsdale.gpssurvey.uiscreens;

import java.io.IOException;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.informationstore.LocationData;
import linsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import linsdale.rpi.screenlib.HybridArea;
import linsdale.rpi.screenlib.PixelPosition;
import linsdale.rpi.screenlib.Screen;
import linsdale.rpi.screenlib.SerialTFTDisplay;
import linsdale.rpi.screenlib.SerialTFTDisplay.CharSet;
import linsdale.rpi.screenlib.SerialTFTDisplay.Colour;
import linsdale.rpi.screenlib.SerialTFTDisplay.Font;
import linsdale.rpi.screenlib.TextZone;

/**
 *
 * @author richard
 */
public class HeadingScreen extends Screen implements ScreenDataChangeProcessor {

    private final static HybridArea fullwidthx1 = new HybridArea(160, 1);
    //
    private final TextZone header1;
    private final static PixelPosition header1pos = new PixelPosition(0, 0);
    private final TextZone course;
    private final static PixelPosition coursepos = new PixelPosition(0, 27);
    private final TextZone header2;
    private final static PixelPosition header2pos = new PixelPosition(0, 64);
    private final TextZone speed;
    private final static PixelPosition speedpos = new PixelPosition(0, 91);
    //
    private final boolean displayknots;

    public HeadingScreen(SerialTFTDisplay display, boolean displayknots) throws IOException {
        super("position", display);
        this.displayknots = displayknots;
        addZone(header1 = new TextZone(display, header1pos, fullwidthx1, Font.SIZE_10x20, CharSet.ISO_8859_1)
                .setCentre().setForeground(Colour.GREEN));
        header1.insert("HEADING");
        //
        addZone(course = new TextZone(display, coursepos, fullwidthx1, Font.SIZE_15x30, CharSet.ISO_8859_1)
                .setCentre());
        //
        addZone(header2 = new TextZone(display, header2pos, fullwidthx1, Font.SIZE_10x20, CharSet.ISO_8859_1)
                .setCentre().setForeground(Colour.GREEN));
        header2.insert("SPEED " + (displayknots ? "(Kt)" : "(m/s)"));
        addZone(speed = new TextZone(display, speedpos, fullwidthx1, Font.SIZE_15x30, CharSet.ISO_8859_1)
                .setCentre());
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        dataChanged(Controller.getLocationData());
    }

    @Override
    public void dataChanged(LocationData ld) throws IOException {
        course.insert(String.format("%3.0f", ld.getHeading()).trim());
        speed.insert(
                (displayknots
                ? String.format("%4.1f", ld.getKnots())
                : String.format("%4.1f", ld.getSpeed())).trim()
        );
        paint();
    }
}
