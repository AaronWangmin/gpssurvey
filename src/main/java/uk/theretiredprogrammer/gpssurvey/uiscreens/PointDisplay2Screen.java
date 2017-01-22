/*
 * Copyright 2014-2017 Richard Linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.gpssurvey.uiscreens;

import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.Position;
import uk.theretiredprogrammer.gpssurvey.informationstore.Controller;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData;
import uk.theretiredprogrammer.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.HybridArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelPosition;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.Screen;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.TextZone;

/**
 * Screen to display course and distance from previously recorded point.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
