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
package uk.theretiredprogrammer.gpssurvey.ui;

import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.HybridArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelPosition;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.Screen;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.TextZone;

/**
 * A Menu screen.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SelectUsingButtonScreen extends Screen implements IRControlAction {

    private final TextZone description;
    private final static PixelPosition dpos = new PixelPosition(0, 6);
    private final static HybridArea dsize = new HybridArea(160, 10);
    private final ButtonActions menu;

    /**
     * Constructor.
     *
     * @param screenId the screen name
     * @param display the display device
     * @param menu the menu definition (text/buttons/actions)
     * @throws IOException if problems
     */
    public SelectUsingButtonScreen(String screenId, SerialTFTDisplay display, ButtonActions menu) throws IOException {
        super(screenId, display);
        this.menu = menu;
        addZone(description = new TextZone(display, dpos, dsize, SerialTFTDisplay.Font.SIZE_6x12, SerialTFTDisplay.CharSet.ISO_8859_1, 0, 4));
        description.insert(menu.text());
    }

    /**
     * Called on button press.
     *
     * @param button the button
     * @return true if action taken (explicit or default)
     * @throws IOException if problems
     */
    @Override
    public boolean actionOnButton(Button button) throws IOException {
        return menu.actionOnButton(button);
    }
}
