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

import java.io.InputStream;
import uk.theretiredprogrammer.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDThread;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;
import net.sf.marineapi.nmea.io.SentenceReader;

/**
 * The DepthFinder reader class.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DepthFinderReader extends MDThread<Command> {

    private final SentenceReader reader;
    private boolean running = false;

    /**
     * Create the DepthFinder Reader Thread and start it.
     *
     * @param depthfinderIn the inputstream for reading the depthfinder
     * sentences
     */
    public static void createAndStart(InputStream depthfinderIn) {
        DepthFinderReader thread = new DepthFinderReader(depthfinderIn);
        thread.start();
        thread.sendMessage(Command.START);
    }

    private DepthFinderReader(InputStream depthfinderIn) {
        super("Depth Finder Reader", Command.CLOSE);
        reader = new SentenceReader(depthfinderIn);
        reader.addSentenceListener(new DepthFinderSentenceListener());
        Reporting.registerControl("DepthFinderReader", 'd');
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) {
        Reporting.report("Depth Finder Reader", 3, "Processing command %s", command);
        switch (command) {
            case START:
                if (!running) {
                    Reporting.report("Depth Finder Reader", 1, "Started");
                    reader.start();
                    running = true;
                }
                break;
            case STOP:
                if (running) {
                    Reporting.report("Depth Finder Reader", 1, "Stopped");
                    reader.stop();
                    running = false;
                }
                break;
            case CLOSE:
                if (running) {
                    Reporting.report("Depth Finder Reader", 1, "Stopped");
                    reader.stop();
                }
                break;
        }
    }
}
