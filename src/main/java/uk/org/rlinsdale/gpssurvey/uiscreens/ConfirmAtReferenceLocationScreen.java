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
import uk.org.rlinsdale.gpssurvey.input.IRControlAction;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction.Button;
import uk.org.rlinsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.org.rlinsdale.gpssurvey.ui.View;
import uk.org.rlinsdale.rpi.screenlib.CharacterArea;
import uk.org.rlinsdale.rpi.screenlib.HybridArea;
import uk.org.rlinsdale.rpi.screenlib.PixelArea;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay;
import uk.org.rlinsdale.rpi.screenlib.PixelPosition;
import uk.org.rlinsdale.rpi.screenlib.Screen;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.CharSet;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.Colour;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.Font;
import uk.org.rlinsdale.rpi.screenlib.TextZone;
import uk.org.rlinsdale.rpi.screenlib.VariableLogBarWidget;
import uk.org.rlinsdale.rpi.threadlib.Reporting;

/**
 * Screen - confirming acceptance of Reference point information.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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

    /**
     * Constructor.
     *
     * @param display the display device
     * @param displaydepth true if depth is displayed else altitude will be
     * displayed
     * @throws IOException if problems
     */
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
        depthlabel.insert(displaydepth ? "Depth: " : "Altitude: ");
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

    /**
     * Action Handler for buttons.
     *
     * Select confirms acceptance.
     *
     * @param button the button
     * @return true if button actioned
     * @throws IOException if problems
     */
    @Override
    public boolean actionOnButton(Button button) throws IOException {
        Reporting.report("Screen", 3, "ConfirmAtReferenceLocation processing command %s", button);
        if (button == Button.SELECT) {
            View.displayNextScreen();
            return true;
        }
        return false;
    }

    /**
     * Location Data changed handler.
     *
     * @param ld new location data
     * @throws IOException if problems
     */
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
