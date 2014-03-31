package linsdale.gpssurvey.uiscreens;

import java.io.IOException;
import linsdale.gpssurvey.HDOP;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.informationstore.LocationData;
import linsdale.gpssurvey.informationstore.LocationData.Location;
import linsdale.gpssurvey.input.IRControlAction;
import linsdale.gpssurvey.input.IRControlAction.Button;
import linsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.CharacterArea;
import linsdale.rpi.screenlib.HybridArea;
import linsdale.rpi.screenlib.PixelArea;
import linsdale.rpi.screenlib.SerialTFTDisplay;
import linsdale.rpi.screenlib.PixelPosition;
import linsdale.rpi.screenlib.Screen;
import linsdale.rpi.screenlib.SerialTFTDisplay.CharSet;
import linsdale.rpi.screenlib.SerialTFTDisplay.Colour;
import linsdale.rpi.screenlib.SerialTFTDisplay.Font;
import linsdale.rpi.screenlib.TextZone;
import linsdale.rpi.screenlib.VariableLogBarWidget;
import linsdale.rpi.threadlib.Reporting;

/**
 *
 * @author richard
 */
public class ConfirmAtReferenceLocationScreen extends Screen
        implements IRControlAction, ScreenDataChangeProcessor {

    private final static int LABELWIDTH = 70;
    private final static HybridArea fullwidthx1 = new HybridArea(160, 1);
    private final static HybridArea fullwidthx2 = new HybridArea(160, 2);
    private final static HybridArea labelwidthx1 = new HybridArea(LABELWIDTH, 1);
    private final static HybridArea fieldwidthx1 = new HybridArea(90, 1);
    //
    private final TextZone header;
    private final static PixelPosition headerpos = new PixelPosition(0, 0);
    private final TextZone northlabel;
    private final static PixelPosition northlabelpos = new PixelPosition(0, 24);
    private final TextZone north;
    private final static PixelPosition northpos = new PixelPosition(LABELWIDTH, 24);
    private final TextZone eastlabel;
    private final static PixelPosition eastlabelpos = new PixelPosition(0, 42);
    private final TextZone east;
    private final static PixelPosition eastpos = new PixelPosition(LABELWIDTH, 42);
    private final TextZone depthlabel;
    private final static PixelPosition depthlabelpos = new PixelPosition(0, 60);
    private final TextZone depth;
    private final static PixelPosition depthpos = new PixelPosition(LABELWIDTH, 60);
    private final TextZone hDOP;
    private final static PixelPosition hDOPpos = new PixelPosition(8, 79);
    private final static CharacterArea hDOParea = new CharacterArea(6, 1);
    private final VariableLogBarWidget hDOPbar;
    private final static PixelPosition hDOPbarpos = new PixelPosition(50, 78);
    private final static PixelArea hDOPbararea = new PixelArea(101, 10);
    private final TextZone confirm;
    private final static PixelPosition confirmpos = new PixelPosition(0, 95);
    //
    private final boolean displaydepth;

    public ConfirmAtReferenceLocationScreen(SerialTFTDisplay display, boolean displaydepth) throws IOException {
        super("confirm at reference location", display);
        this.displaydepth = displaydepth;
        //
        addZone(header = new TextZone(display, headerpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setForeground(Colour.GREEN).setCentre());
        header.insert("Reference Location");
        addZone(confirm = new TextZone(display, confirmpos, fullwidthx2, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setForeground(Colour.RED).setCentre());
        confirm.insert("Press Select to confirm");
        addZone(northlabel = new TextZone(display, northlabelpos, labelwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        northlabel.insert("North: ");
        addZone(north = new TextZone(display, northpos, fieldwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(eastlabel = new TextZone(display, eastlabelpos, labelwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        eastlabel.insert("East: ");
        addZone(east = new TextZone(display, eastpos, fieldwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(depthlabel = new TextZone(display, depthlabelpos, labelwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        depthlabel.insert(displaydepth?"Depth: ":"Altitude: ");
        addZone(depth = new TextZone(display, depthpos, fieldwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(hDOP = new TextZone(display, hDOPpos, hDOParea, Font.SIZE_6x12, CharSet.ISO_8859_1));
        addZone(hDOPbar = new VariableLogBarWidget(display, hDOPbarpos, hDOPbararea,
                1.0, 6.0, Colour.GREEN, Colour.YELLOW, true));
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        dataChanged(Controller.getLocationData());
    }

    @Override
    public boolean actionOnButton(Button button) throws IOException {
        Reporting.report("Screen", 3, "ConfirmAtReferenceLocation processing command %s", button);
        if (button == Button.SELECT) {
            View.displayNextScreen();
            return true;
        }
        return false;
    }

    @Override
    public void dataChanged(LocationData ld) throws IOException {
        Location l = ld.getLocation();
        HDOP hdop = l.getHDOP();
        hDOP.insert(hdop.toString());
        hDOPbar.setValue(hdop.get());
        north.insert(l.getPosition().latitude.toStringWithUnits());
        east.insert(l.getPosition().longitude.toStringWithUnits());
        depth.insert(displaydepth
                ? l.getDepth().toStringWithUnits()
                : l.getAltitude().toStringWithUnits());
        paint();
    }
}
