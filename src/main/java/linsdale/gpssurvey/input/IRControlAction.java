package linsdale.gpssurvey.input;

import java.io.IOException;

/**
 *
 * @author richard
 */
public interface IRControlAction {
    
    public static enum Button {

        POWERDOWN, // the controller most likely will handle his one (std action - so will not be seen by screen)
        POWER,      // ... ditto
        A,
        B,
        C,
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SELECT
    }
    
    public boolean actionOnButton(Button button) throws IOException;
}
