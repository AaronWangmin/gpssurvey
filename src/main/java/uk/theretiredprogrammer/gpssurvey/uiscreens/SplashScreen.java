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

import uk.theretiredprogrammer.gpssurvey.ui.GPSLogoZone;
import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.ui.View;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.ScreenWithTick;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;

/**
 * The opening splash screen.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
