package linsdale.gpssurvey.ui;

import java.io.IOException;
import linsdale.gpssurvey.input.IRControlAction;
import linsdale.rpi.screenlib.HybridArea;
import linsdale.rpi.screenlib.PixelPosition;
import linsdale.rpi.screenlib.Screen;
import linsdale.rpi.screenlib.SerialTFTDisplay;
import linsdale.rpi.screenlib.TextZone;

/**
 *
 * @author richard
 */
public class SelectUsingButtonScreen extends Screen implements IRControlAction {

    private final TextZone description;
    private final static PixelPosition dpos = new PixelPosition(0, 6);
    private final static HybridArea dsize = new HybridArea(160, 10);
    private final ButtonActions menu;

    public SelectUsingButtonScreen(String screenId, SerialTFTDisplay display, ButtonActions menu) throws IOException {
        super(screenId, display);
        this.menu = menu;
        addZone(description = new TextZone(display, dpos, dsize, SerialTFTDisplay.Font.SIZE_6x12, SerialTFTDisplay.CharSet.ISO_8859_1,0, 4));
        description.insert(menu.text());
    }

    @Override
    public boolean actionOnButton(Button button) throws IOException {
        return menu.actionOnButton(button);
    }
}
