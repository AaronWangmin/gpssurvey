package linsdale.gpssurvey.uiscreens;

import java.io.IOException;
import linsdale.gpssurvey.Position;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.informationstore.LocationData;
import linsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import linsdale.rpi.screenlib.HybridArea;
import linsdale.rpi.screenlib.SerialTFTDisplay;
import linsdale.rpi.screenlib.PixelPosition;
import linsdale.rpi.screenlib.Screen;
import linsdale.rpi.screenlib.SerialTFTDisplay.CharSet;
import linsdale.rpi.screenlib.SerialTFTDisplay.Colour;
import linsdale.rpi.screenlib.SerialTFTDisplay.Font;
import linsdale.rpi.screenlib.TextZone;

/**
 *
 * @author richard
 */
public class PointDisplay2Screen extends Screen implements ScreenDataChangeProcessor {

    private final static HybridArea fullwidthx3 = new HybridArea(160, 3);
    private final static HybridArea fullwidthx6 = new HybridArea(160, 6);
    //
    private final TextZone header;
    private final static PixelPosition headerpos = new PixelPosition(0, 2);
    private final TextZone pointinformation;
    private final static PixelPosition pointinformationpos = new PixelPosition(0, 44);

    public PointDisplay2Screen(SerialTFTDisplay display) throws IOException {
        super("position2", display);
        addZone(header = new TextZone(display, headerpos, fullwidthx3, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setCentre().setForeground(Colour.GREEN));
        header.insert("Course and Distance to Previous Recorded Pt");
        //
        addZone(pointinformation = new TextZone(display, pointinformationpos, fullwidthx6, Font.SIZE_7x14, CharSet.ISO_8859_1));
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        dataChanged(Controller.getLocationData());
    }

    @Override
    public void dataChanged(LocationData ld) throws IOException {
        int pointcount = ld.getPointsSize();
        int linecount = 6;
        StringBuffer sb = new StringBuffer();
        if (pointcount > 0) {
            pointcount--;
            addPolarDisplay(sb,ld.getCurrentFromPoint(pointcount));
            linecount--;
        }
        while (linecount > 0 && pointcount > 0 ){
            pointcount--;
            addPolarDisplay(sb,ld.getPointFromPreviousPoint(pointcount+1));
            linecount--;
        }
        pointinformation.insert(sb.toString());
        paint();
    }
    
    private void addPolarDisplay(StringBuffer sb, Position p){
        sb.append(String.format("%s %s\n", p.distance.toStringWithUnits(), p.course.toStringWithUnits()));
    }
}
