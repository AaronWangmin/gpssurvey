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
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.informationstore.LocationData;
import linsdale.gpssurvey.ui.ScreenDataChangeProcessor;
import linsdale.gpssurvey.ui.View;
import linsdale.gpssurvey.ui.WaitingForZone;
import linsdale.rpi.screenlib.ScreenWithTick;
import linsdale.rpi.screenlib.SerialTFTDisplay;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class ObtainingReferenceLocationInformationScreen extends ScreenWithTick
        implements ScreenDataChangeProcessor {

    private final WaitingForZone zone;

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

    @Override
    public void dataChanged(LocationData ld) throws IOException {
        if (ld.getReferenceLocation() != null) {
            View.displayNewScreenSet("main");
        }
    }
}
