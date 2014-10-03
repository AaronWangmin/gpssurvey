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
package uk.org.rlinsdale.gpssurvey.ui;

import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.HybridArea;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.PixelPosition;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.Screen;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.TextZone;

/**
 * A Menu screen.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
