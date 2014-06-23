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
package uk.org.rlinsdale.gpssurvey.input;

import java.io.IOException;
import java.util.TimerTask;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction.Button;
import uk.org.rlinsdale.rpi.threadlib.MDTService;
import uk.org.rlinsdale.rpi.threadlib.MDTService.Exitcode;
import uk.org.rlinsdale.rpi.threadlib.MDThread;
import uk.org.rlinsdale.rpi.threadlib.Reporting;

/**
 * The keyboard listening thread - looking for key presses.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
    
    /**
     * Create and start the listening thread.
     */
    public static void createAndStart()  {
        KeyboardListener thread = new KeyboardListener();
        thread.start();
        thread.sendMessage(Command.START);
    }

    private KeyboardListener() {
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
