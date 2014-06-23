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
 * Screen to display current location (as distance and course) - relative to
 * reference point and last recorded point.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
