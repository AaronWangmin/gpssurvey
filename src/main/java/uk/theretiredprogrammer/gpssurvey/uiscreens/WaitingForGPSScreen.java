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

import uk.theretiredprogrammer.gpssurvey.ui.WaitingForZone;
import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.informationstore.Controller;
import uk.theretiredprogrammer.gpssurvey.ui.View;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.ScreenWithTick;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;

/**
 * Screen to display while waiting for GPS device to lock on.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
