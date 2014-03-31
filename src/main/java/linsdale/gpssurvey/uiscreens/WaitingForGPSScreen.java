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

import linsdale.gpssurvey.ui.WaitingForZone;
import java.io.IOException;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.ScreenWithTick;
import linsdale.rpi.screenlib.SerialTFTDisplay;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class WaitingForGPSScreen extends ScreenWithTick {

    private final WaitingForZone zone;

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
