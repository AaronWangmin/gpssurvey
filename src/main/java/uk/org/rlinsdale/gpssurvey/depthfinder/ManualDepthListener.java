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
package uk.org.rlinsdale.gpssurvey.depthfinder;

import java.io.IOException;
import java.util.TimerTask;
import uk.org.rlinsdale.gpssurvey.Depth;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.gpssurvey.depthfinder.DepthFinderMessageConsolidator.ConsolidatedDepthFinderData;
import uk.org.rlinsdale.rpi.threadlib.MDTService;
import uk.org.rlinsdale.rpi.threadlib.MDTService.Exitcode;
import uk.org.rlinsdale.rpi.threadlib.MDThread;
import uk.org.rlinsdale.rpi.threadlib.Reporting;

/**
 * The manual Depth Listener.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
