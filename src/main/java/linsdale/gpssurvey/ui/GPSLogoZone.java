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
package linsdale.gpssurvey.ui;

import java.io.IOException;
import linsdale.rpi.screenlib.HybridArea;
import linsdale.rpi.screenlib.PixelPosition;
import linsdale.rpi.screenlib.SerialTFTDisplay;
import linsdale.rpi.screenlib.SerialTFTDisplay.CharSet;
import linsdale.rpi.screenlib.SerialTFTDisplay.Colour;
import linsdale.rpi.screenlib.SerialTFTDisplay.Font;
import linsdale.rpi.screenlib.TextZone;
import linsdale.rpi.screenlib.Zone;

/**
 * A Screen Zone which draws the application logo screen with optional appended
 * user text.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSLogoZone implements Zone {

    private final Colour LOGOCOLOUR = Colour.BLACK;
    private final Colour LOGOSTRAPCOLOUR = Colour.RED;
    private final Font LOGOSTRAPFONT = Font.SIZE_10x20;
    private final Font LOGOFONT = Font.SIZE_7x14;

    private final TextZone gps1;
    private final static PixelPosition gps1pos = new PixelPosition(0, 20);
    private final static HybridArea gps1size = new HybridArea(160, 4);
    private final TextZone strap1;
    private final static PixelPosition strappos1 = new PixelPosition(13, 22);
    private final static HybridArea strapsize1 = new HybridArea(147, 1);
    private final TextZone strap2;
    private final static PixelPosition strappos2 = new PixelPosition(0, 37);
    private final static HybridArea strapsize2 = new HybridArea(160, 1);
    private final TextZone strap3;
    private final static PixelPosition strappos3 = new PixelPosition(0, 52);
    private final static HybridArea strapsize3 = new HybridArea(147, 1);
    private final TextZone msg;
    private final static PixelPosition msgpos = new PixelPosition(0, 100);
    private final static HybridArea msgsize = new HybridArea(160, 1);

    /**
     * Constructor.
     * 
     * @param display the display device
     * @param message the user message to add to the logo
     * @throws IOException if problems
     */
    public GPSLogoZone(SerialTFTDisplay display, String message) throws IOException {
        gps1 = new TextZone(display, gps1pos, gps1size, LOGOFONT, CharSet.ISO_8859_1, 0, 0)
                .setCentre().setTransparent().setForeground(LOGOCOLOUR);
        gps1.insert("GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS GPS");
        strap1 = new TextZone(display, strappos1, strapsize1, LOGOSTRAPFONT, CharSet.ISO_8859_1, 1, 0)
                .setLeft().setTransparent().setForeground(LOGOSTRAPCOLOUR);
        strap1.insert("Sports");
        strap2 = new TextZone(display, strappos2, strapsize2, LOGOSTRAPFONT, CharSet.ISO_8859_1, 1, 0)
                .setCentre().setTransparent().setForeground(LOGOSTRAPCOLOUR);
        strap2.insert("Location");
        strap3 = new TextZone(display, strappos3, strapsize3, LOGOSTRAPFONT, CharSet.ISO_8859_1, 1, 0)
                .setRight().setTransparent().setForeground(LOGOSTRAPCOLOUR);
        strap3.insert("Recorder");
        msg = new TextZone(display, msgpos, msgsize, Font.SIZE_10x20, CharSet.ISO_8859_1)
                .setCentre().setForeground(SerialTFTDisplay.Colour.WHITE);
        msg.insert(message);
    }

    /**
     * Set the user message text.
     * 
     * @param message the user message to add to the logo
     * @throws IOException if problems
     */
    public void setMessage(String message) throws IOException {
        msg.insert(message);
    }

    @Override
    public void paint() throws IOException {
        paint(false);
    }

    @Override
    public void paint(boolean force) throws IOException {
        gps1.paint(force);
        strap1.paint(force);
        strap2.paint(force);
        strap3.paint(force);
        msg.paint(force);
    }
}
