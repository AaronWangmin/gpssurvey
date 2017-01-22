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
import uk.theretiredprogrammer.gpssurvey.HDOP;
import uk.theretiredprogrammer.gpssurvey.informationstore.Controller;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData.Location;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction.Button;
import uk.theretiredprogrammer.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.theretiredprogrammer.gpssurvey.ui.View;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.CharacterArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.HybridArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelPosition;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.Screen;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.TextZone;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.VariableLogBarWidget;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;

/**
 * Screen - confirming acceptance of Reference point information.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
