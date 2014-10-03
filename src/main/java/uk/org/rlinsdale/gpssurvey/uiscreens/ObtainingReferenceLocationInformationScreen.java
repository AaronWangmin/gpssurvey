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
import uk.org.rlinsdale.gpssurvey.ui.View;
import uk.org.rlinsdale.gpssurvey.ui.WaitingForZone;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.ScreenWithTick;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay;

/**
 * Screen to be displayed will waiting for reference location data to be
 * collected.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class ObtainingReferenceLocationInformationScreen extends ScreenWithTick
        implements ScreenDataChangeProcessor {

    private final WaitingForZone zone;

    /**
     * Constructor.
     *
     * @param display the display device
     * @throws IOException if problems
     */
    public ObtainingReferenceLocationInformationScreen(SerialTFTDisplay display) throws IOException {
        super("obtaining reference location information", display, 1);
        addZone(zone = new WaitingForZone(display, "Obtaining Reference Location"));
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        Controller.obtainReferenceLocation();
    }

    @Override
    public void tick(Ticker t) throws IOException {
        zone.onTick();
        View.displayRepaint();
    }

    /**
     * Location Data changed handler.
     *
     * @param ld updated location data.
     * @throws IOException if problems
     */
    @Override
    public void dataChanged(LocationData ld) throws IOException {
        if (ld.getReferenceLocation() != null) {
            View.displayNewScreenSet("main");
        }
    }
}
