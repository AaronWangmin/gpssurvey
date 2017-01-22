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
 * Screen to display current location (as distance and course) - relative to
 * reference point and last recorded point.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PositionScreen extends Screen implements ScreenDataChangeProcessor {

    private final static int HALFWIDTH = 80;
    private final static HybridArea fullwidthx1 = new HybridArea(160, 1);
    private final static HybridArea halfwidthx1 = new HybridArea(80, 1);
    //
    private final TextZone header;
    private final static PixelPosition headerpos = new PixelPosition(0, 2);
    private final TextZone refheader;
    private final static PixelPosition refheaderpos = new PixelPosition(0, 30);
    private final TextZone refcourse;
    private final static PixelPosition refcoursepos = new PixelPosition(0, 52);
    private final TextZone refdistance;
    private final static PixelPosition refdistancepos = new PixelPosition(HALFWIDTH, 52);
    private final TextZone ptheader;
    private final static PixelPosition ptheaderpos = new PixelPosition(0, 74);
    private final TextZone ptcourse;
    private final static PixelPosition ptcoursepos = new PixelPosition(0, 96);
    private final TextZone ptdistance;
    private final static PixelPosition ptdistancepos = new PixelPosition(HALFWIDTH, 96);

    /**
     * Constructor.
     *
     * @param display the display device
     * @throws IOException if problems
     */
    public PositionScreen(SerialTFTDisplay display) throws IOException {
        super("position", display);
        addZone(header = new TextZone(display, headerpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setCentre().setForeground(Colour.GREEN));
        header.insert("Current Location");
        //
        addZone(refheader = new TextZone(display, refheaderpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setForeground(Colour.GREEN));
        refheader.insert("From Reference Location");
        addZone(refcourse = new TextZone(display, refcoursepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(refdistance = new TextZone(display, refdistancepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        //
        addZone(ptheader = new TextZone(display, ptheaderpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setForeground(Colour.GREEN));
        ptheader.insert("From Last Recorded Pt.");
        addZone(ptcourse = new TextZone(display, ptcoursepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(ptdistance = new TextZone(display, ptdistancepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
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
        Position l = ld.getPosition();
        refcourse.insert(l.course.toStringWithUnits());
        refdistance.insert(l.distance.toStringWithUnits());
        if (ld.hasNoPoints()) {
            ptcourse.insert("");
            ptdistance.insert("");
        } else {
            Position frompoint = ld.getCurrentFromLastPoint();
            ptcourse.insert(frompoint.course.toStringWithUnits());
            ptdistance.insert(frompoint.distance.toStringWithUnits());
        }
        paint();
    }
}
