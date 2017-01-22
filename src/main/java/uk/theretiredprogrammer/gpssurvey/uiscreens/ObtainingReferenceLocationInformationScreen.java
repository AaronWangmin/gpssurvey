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
import uk.theretiredprogrammer.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.theretiredprogrammer.gpssurvey.ui.View;
import uk.theretiredprogrammer.gpssurvey.ui.WaitingForZone;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.ScreenWithTick;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;

/**
 * Screen to be displayed will waiting for reference location data to be
 * collected.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
