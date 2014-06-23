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
import uk.org.rlinsdale.rpi.screenlib.HybridArea;
import uk.org.rlinsdale.rpi.screenlib.PixelArea;
import uk.org.rlinsdale.rpi.screenlib.PixelPosition;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.CharSet;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.Colour;
import uk.org.rlinsdale.rpi.screenlib.SerialTFTDisplay.Font;
import uk.org.rlinsdale.rpi.screenlib.TextZone;
import uk.org.rlinsdale.rpi.screenlib.VariableBarWidget;
import uk.org.rlinsdale.rpi.screenlib.Zone;

/**
 * A Zone showing message and progress bar.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class WaitingForZone implements Zone {

    private final TextZone msg;
    private final static PixelPosition msgpos = new PixelPosition(0, 10);
    private final static HybridArea msgsize = new HybridArea(160, 4);
    private final VariableBarWidget progressbar;
    private final static PixelPosition progressbarpos = new PixelPosition(20, 111);
    private final static PixelArea progressbarsize = new PixelArea(120, 10);
    private static final int MINCOUNT = 0;
    private static final int MAXCOUNT = 10;
    private int count = MINCOUNT + 1;
    private boolean countup = true;
   
    /**
     * Constructor.
     * 
     * @param display the display device
     * @param message the message to display
     * @throws IOException if problems
     */
    public WaitingForZone(SerialTFTDisplay display, String message) throws IOException {
        msg = new TextZone(display, msgpos, msgsize, Font.SIZE_10x20, CharSet.ISO_8859_1)
                .setCentre().setForeground(Colour.WHITE);
        msg.insert(message);
        progressbar = new VariableBarWidget(display, progressbarpos, progressbarsize,
                0, 10, Colour.GREEN, display.getBackground());
    }

    /**
     * Update the progress bar display
     */
    public void onTick() {
        if (count == MINCOUNT) {
            countup = true;
            progressbar.invert();
        }
        if (count == MAXCOUNT) {
            countup = false;
            progressbar.invert();
        }
        if (countup) {
            count++;
        } else {
            count--;
        }
        progressbar.setValue(count);
    }
    
    @Override
    public void paint() throws IOException {
        paint(false);
    }

    @Override
    public void paint(boolean force) throws IOException {
        msg.paint(force);
        progressbar.paint(force);
    }
}
