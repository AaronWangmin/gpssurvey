/*
 * Copyright (C) 2014 Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.org.rlinsdale.gpssurvey.uiscreens;

import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.Position;
import uk.org.rlinsdale.gpssurvey.informationstore.Controller;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData;
import uk.org.rlinsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.org.rlinsdale.rpi.screenlib.HybridArea;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay;
import uk.org.rlinsdale.rpi.screenlib.PixelPosition;
import uk.org.rlinsdale.rpi.screenlib.Screen;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.CharSet;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.Colour;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.Font;
import uk.org.rlinsdale.rpi.screenlib.TextZone;

/**
 * Screen to display course and distance from previously recorded point.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class PointDisplay2Screen extends Screen implements ScreenDataChangeProcessor {

    private final static HybridArea fullwidthx3 = new HybridArea(160, 3);
    private final static HybridArea fullwidthx6 = new HybridArea(160, 6);
    //
    private final TextZone header;
    private final static PixelPosition headerpos = new PixelPosition(0, 2);
    private final TextZone pointinformation;
    private final static PixelPosition pointinformationpos = new PixelPosition(0, 44);

    /**
     * Constructor.
     * 
     * @param display the display device
     * @throws IOException if problems
     */
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

    /**
     * Location Data change handler.
     * 
     * @param ld new location data
     * @throws IOException if problems
     */
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
