package linsdale.gpssurvey.input;

import java.io.IOException;
import java.util.TimerTask;
import linsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import linsdale.gpssurvey.input.IRControlAction.Button;
import linsdale.rpi.threadlib.MDTService;
import linsdale.rpi.threadlib.MDTService.Exitcode;
import linsdale.rpi.threadlib.MDThread;
import linsdale.rpi.threadlib.Reporting;

/**
 *
 * @author richard
 */
public class KeyboardListener extends MDThread<Command> {
    
    private final static int POLLINTERVAL = 100;

    private final static String invalues = "q+/*-84562";
    private final static Button[] inbuttons = new Button[]{
        Button.POWERDOWN,
        Button.POWER,
        Button.A,
        Button.B,
        Button.C,
        Button.UP,
        Button.LEFT,
        Button.SELECT,
        Button.RIGHT,
        Button.DOWN
    };
    private Listener ticker;
    
    public static KeyboardListener createAndStart() throws IOException {
        KeyboardListener thread = new KeyboardListener();
        thread.start();
        thread.sendMessage(Command.START);
        return thread;
    }

    public KeyboardListener() {
        super("KB Listener", Command.CLOSE);
        Reporting.registerControl("KB Listener", 'k');
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) throws IOException {
        Reporting.report("KB Listener", 3, "Processing command %s", command);
        switch (command) {
            case START:
                ticker = new Listener();
                MDTService.timerSchedule(ticker, POLLINTERVAL, POLLINTERVAL);
                break;
            case CLOSE:
                ticker.cancel();
        }
    }

    private class Listener extends TimerTask {

        @Override
        public void run() {
            try {
                if (System.in.available() > 0) {
                    int inchar = System.in.read();
                    int pos = invalues.indexOf(inchar);
                    if (pos != -1) {
                        Button b = inbuttons[pos];
                        Reporting.report("KB Listener", 3, "Received KB message: " + b.toString());
                        MDTService.sendMessage("Controller", Command.BUTTON, b);
                    }
                }
            } catch (IOException ex) {
                MDTService.reportExceptionAndExit(ex, Exitcode.EXIT_PROGFAIL);
            }
        }
    }
}
