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
package uk.theretiredprogrammer.gpssurvey.depthfinder;

import java.io.IOException;
import java.util.TimerTask;
import uk.theretiredprogrammer.gpssurvey.Depth;
import uk.theretiredprogrammer.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.theretiredprogrammer.gpssurvey.depthfinder.DepthFinderMessageConsolidator.ConsolidatedDepthFinderData;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService.Exitcode;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDThread;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;

/**
 * The manual Depth Listener.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ManualDepthListener extends MDThread<Command> {

    private final static double DEPTHSTEPSIZE = 0.25;
    private double depth = 0;
    private Ticker ticker;
    private boolean operational = false;
    private static final int TICKINTERVAL = 1000;

    /**
     * Create the manual DepthListener Thread and start it.
     */
    public static void createAndStart()  {
        ManualDepthListener thread = new ManualDepthListener();
        thread.start();
        thread.sendMessage(Command.START);
    }
    
    private ManualDepthListener() {
        super("Manual Depth Listener", Command.CLOSE);
        Reporting.registerControl("Manual Depth Listener", 'm');
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) throws IOException {
        Reporting.report("Manual Depth Listener", 3, "Processing command %s", command);
        switch (command) {
            case START:
                operational = true;
                ticker = new Ticker();
                MDTService.timerSchedule(ticker, TICKINTERVAL, TICKINTERVAL);
                break;
            case STOP:
                if (operational) {
                    ticker.cancel();
                }
                break;
            case CLOSE:
                if (operational) {
                    ticker.cancel();
                    operational = false;
                }
                break;
            case INCDEPTH:
                if (operational) {
                    depth += DEPTHSTEPSIZE;
                    sendDepthMessage();
                }
                break;
            case DECDEPTH:
                if (operational) {
                    depth -= DEPTHSTEPSIZE;
                    if (depth < 0.0) {
                        depth = 0.0;
                    }
                    sendDepthMessage();
                }
        }
    }

    private void sendDepthMessage() throws IOException {
        Reporting.report("Manual Depth Listener", 4, "Sending Depth Message to Information Store");
        ConsolidatedDepthFinderData msg = new ConsolidatedDepthFinderData();
        msg.depth = new Depth(depth);
        MDTService.sendMessage("Controller", Command.DEPTHINFO, msg);
    }

    private class Ticker extends TimerTask {

        @Override
        public void run() {
            try {
                ManualDepthListener.this.sendDepthMessage();
            } catch (IOException ex) {
                MDTService.reportExceptionAndExit(ex, Exitcode.EXIT_PROGFAIL);
            }
        }
    }
}
