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

import linsdale.gpssurvey.ui.GPSLogoZone;
import java.io.IOException;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.ScreenWithTick;
import linsdale.rpi.screenlib.SerialTFTDisplay;

/**
 * The opening splash screen.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class SplashScreen extends ScreenWithTick {

    /**
     * Constructor.
     *
     * @param display the display device
     * @throws IOException if problems
     */
    public SplashScreen(SerialTFTDisplay display) throws IOException {
        super("splash", display, 5);
        addZone(new GPSLogoZone(display, "Starting..."));
    }

    @Override
    public void tick(Ticker t) throws IOException {
        t.cancel();
        View.displayNextScreen();
    }
}
