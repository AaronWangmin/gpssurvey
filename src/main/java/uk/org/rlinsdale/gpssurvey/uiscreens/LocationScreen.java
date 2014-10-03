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
import uk.org.rlinsdale.gpssurvey.HDOP;
import uk.org.rlinsdale.gpssurvey.informationstore.Controller;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData.Location;
import uk.org.rlinsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.CharacterArea;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.HybridArea;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.PixelArea;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.PixelPosition;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.Screen;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.TextZone;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.VariableLogBarWidget;

/**
 * Screen to display current location.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class LocationScreen extends Screen implements ScreenDataChangeProcessor {

    private final static int LABELWIDTH = 70;
    private final static HybridArea halfwidthx1 = new HybridArea(80, 1);
    private final static HybridArea fullwidthx1 = new HybridArea(160, 1);
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
    private final static PixelPosition eastPos = new PixelPosition(LABELWIDTH, 42);
    private final TextZone depthlabel;
    private final static PixelPosition depthlabelpos = new PixelPosition(0, 60);
    private final TextZone depth;
    private final static PixelPosition depthpos = new PixelPosition(LABELWIDTH, 60);
    private final TextZone hDOP;
    private final static PixelPosition hDOPpos = new PixelPosition(8, 101);
    private final static CharacterArea hDOParea = new CharacterArea(6, 1);
    private final VariableLogBarWidget hDOPbar;
    private final static PixelPosition hDOPbarpos = new PixelPosition(50, 100);
    private final static PixelArea hDOPbararea = new PixelArea(101, 10);
    private final TextZone time;
    private final static PixelPosition timepos = new PixelPosition(80, 114);
    private final TextZone date;
    private final static PixelPosition datepos = new PixelPosition(0, 114);
    //
    private final boolean displaycoordinates;
    private final boolean displaydepth;

    /**
     * Constructor.
     *
     * @param display the display device
     * @param displaycoordinates if true then display position as relative
     * coordinates, other display as latitude/longitude
     * @param displaydepth if true display depth otherwise display altitude
     * @throws IOException if problems
     */
    public LocationScreen(SerialTFTDisplay display, boolean displaycoordinates, boolean displaydepth) throws IOException {
        super("location", display);
        this.displaycoordinates = displaycoordinates;
        this.displaydepth = displaydepth;
        //
        addZone(header = new TextZone(display, headerpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1, 0, 4)
                .setCentre().setForeground(Colour.GREEN));
        header.insert("Current Location");
        addZone(northlabel = new TextZone(display, northlabelpos, labelwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        northlabel.insert("North: ");
        addZone(north = new TextZone(display, northpos, fieldwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(eastlabel = new TextZone(display, eastlabelpos, labelwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        eastlabel.insert("East: ");
        addZone(east = new TextZone(display, eastPos, fieldwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(depthlabel = new TextZone(display, depthlabelpos, labelwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        depthlabel.insert(displaydepth ? "Depth: " : "Altitude: ");
        addZone(depth = new TextZone(display, depthpos, fieldwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(hDOP = new TextZone(display, hDOPpos, hDOParea, Font.SIZE_6x12, CharSet.ISO_8859_1));
        addZone(hDOPbar = new VariableLogBarWidget(display, hDOPbarpos, hDOPbararea,
                1.0, 6.0, Colour.GREEN, Colour.YELLOW, true));
        addZone(date = new TextZone(display, datepos, halfwidthx1, Font.SIZE_6x12, CharSet.ISO_8859_1)
                .setLeft().setForeground(Colour.GREEN));
        addZone(time = new TextZone(display, timepos, halfwidthx1, Font.SIZE_6x12, CharSet.ISO_8859_1)
                .setRight().setForeground(Colour.GREEN));
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        dataChanged(Controller.getLocationData());
    }

    /**
     * Location Data change handler.
     *
     * @param ld the updated location data.
     * @throws IOException if problems
     */
    @Override
    public void dataChanged(LocationData ld) throws IOException {
        Location l = ld.getLocation();
        date.insert(ld.getDatedisplay());
        time.insert(ld.getTimedisplay());
        if (l != null) {
            HDOP hdop = l.getHDOP();
            hDOP.insert(hdop.toString());
            hDOPbar.setValue(hdop.get());
            east.insert(displaycoordinates
                    ? l.getPosition().x.toStringWithUnits()
                    : l.getPosition().longitude.toStringWithUnits());
            north.insert(displaycoordinates
                    ? l.getPosition().y.toStringWithUnits()
                    : l.getPosition().latitude.toStringWithUnits());
            depth.insert(displaydepth
                    ? l.getDepth().toStringWithUnits()
                    : l.getAltitude().toStringWithUnits());
        }
        paint();
    }
}
