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
package uk.theretiredprogrammer.gpssurvey.input;

import java.io.IOException;
import java.util.TimerTask;
import uk.theretiredprogrammer.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction.Button;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService.Exitcode;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDThread;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;

/**
 * The keyboard listening thread - looking for key presses.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
