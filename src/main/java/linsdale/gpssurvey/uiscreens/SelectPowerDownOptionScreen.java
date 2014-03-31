package linsdale.gpssurvey.uiscreens;

import java.io.IOException;
import linsdale.gpssurvey.ui.Action;
import linsdale.gpssurvey.ui.ButtonActions;
import linsdale.gpssurvey.ui.SelectUsingButtonScreen;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.SerialTFTDisplay;
import linsdale.rpi.threadlib.MDTService.Exitcode;

/**
 *
 * @author richard
 */
public class SelectPowerDownOptionScreen extends SelectUsingButtonScreen {

    public SelectPowerDownOptionScreen(SerialTFTDisplay display) throws IOException {
        super("SelectPowerDownOption", display,
                new ButtonActions("SYSTEM EXIT REQUEST", "Cancel", new CancelAction())
                .addLine(Button.A, "Power Off", new PowerOffAction())
                .addLine(Button.B, "Reboot", new RebootAction())
                .addLine(Button.C, "Restart GPS", new RestartAction()));
    }

    public static class CancelAction implements Action {

        @Override
        public void execute() throws IOException {
            View.displayPreviousScreenSet();
        }
    }
    
    public static class PowerOffAction implements Action {

        @Override
        public void execute() throws IOException {
            View.displayNextScreenAndExit(Exitcode.EXIT_POWEROFF, "Powering down...");
        }
    }
    
    public static class RebootAction implements Action {

        @Override
        public void execute() throws IOException {
            View.displayNextScreenAndExit(Exitcode.EXIT_REBOOT, "Rebooting...");
        }
    }
    
    public static class RestartAction implements Action {

        @Override
        public void execute() throws IOException {
            View.displayNextScreenAndExit(Exitcode.EXIT_OK, "Restarting...");
        }
    }
}
