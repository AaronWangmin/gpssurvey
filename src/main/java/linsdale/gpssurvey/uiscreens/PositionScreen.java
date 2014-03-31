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
