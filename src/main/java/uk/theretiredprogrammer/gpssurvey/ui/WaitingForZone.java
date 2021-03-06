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
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.HybridArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelPosition;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.TextZone;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.VariableBarWidget;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.Zone;

/**
 * A Zone showing message and progress bar.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
