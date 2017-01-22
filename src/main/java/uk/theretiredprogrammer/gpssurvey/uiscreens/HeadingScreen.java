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
import uk.theretiredprogrammer.gpssurvey.informationstore.Controller;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData;
import uk.theretiredprogrammer.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.HybridArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelPosition;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.Screen;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.TextZone;

/**
 * Screen to display Heading and Speed.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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

    /**
     * Constructor.
     *
     * @param display the display device
     * @param displayknots true if speed to be displayed in knots otherwise
     * metres/sec
     * @throws IOException if problems
     */
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

    /**
     * Location data change handler
     *
     * @param ld the updated location data
     * @throws IOException if problems
     */
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
