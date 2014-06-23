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

import uk.org.rlinsdale.gpssurvey.ui.WaitingForZone;
import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.informationstore.Controller;
import uk.org.rlinsdale.gpssurvey.ui.View;
import uk.org.rlinsdale.rpi.screenlib.ScreenWithTick;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay;

/**
 * Screen to display while waiting for GPS device to lock on.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class WaitingForGPSScreen extends ScreenWithTick {

    private final WaitingForZone zone;

    /**
     * Constructor.
     *
     * @param display the display device
     * @throws IOException if problems
     */
    public WaitingForGPSScreen(SerialTFTDisplay display) throws IOException {
        super("waiting for GPS", display, 1);
        addZone(zone = new WaitingForZone(display,
                "Waiting for GPS lock"));
    }

    @Override
    public void tick(Ticker t) throws IOException {
        if (Controller.getLocationData().getLocation() != null) {
            View.displayNextScreen();
        } else {
            zone.onTick();
            View.displayRepaint();
        }
    }
}
