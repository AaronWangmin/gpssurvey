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
import uk.org.rlinsdale.gpssurvey.informationstore.Controller;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData;
import uk.org.rlinsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.HybridArea;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.PixelPosition;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.Screen;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.TextZone;

/**
 * Screen to display Heading and Speed.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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