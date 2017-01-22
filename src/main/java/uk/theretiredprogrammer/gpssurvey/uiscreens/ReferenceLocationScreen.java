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
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData.Location;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.HybridArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelPosition;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.Screen;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.TextZone;

/**
 * Screen display reference location information.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ReferenceLocationScreen extends Screen {

    private final static int LABELWIDTH = 70;
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
    private final static PixelPosition eastpos = new PixelPosition(LABELWIDTH, 42);
    private final TextZone depthlabel;
    private final static PixelPosition depthlabelpos = new PixelPosition(0, 60);
    private final TextZone depth;
    private final static PixelPosition depthpos = new PixelPosition(LABELWIDTH, 60);
    //
    private final boolean displaydepth;

    /**
     * Constructor.
     *
     * @param display the display device
     * @param displaydepth if true display depth else display altitude
     * @throws IOException if problems
     */
    public ReferenceLocationScreen(SerialTFTDisplay display, boolean displaydepth) throws IOException {
        super("reference location", display);
        this.displaydepth = displaydepth;
        //
        addZone(header = new TextZone(display, headerpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setForeground(Colour.GREEN).setCentre());
        header.insert("Reference Location");
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
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        LocationData ld = Controller.getLocationData();
        Location l = ld.getReferenceLocation();
        if (l != null) {
            north.insert(l.getPosition().latitude.toStringWithUnits());
            east.insert(l.getPosition().longitude.toStringWithUnits());
            depth.insert(displaydepth
                    ? l.getDepth().toStringWithUnits()
                    : l.getAltitude().toStringWithUnits());
        } else {
            throw new IOException("Reference Location Screen gained focus, but reference location not available from InformationStore");
        }
    }
}
