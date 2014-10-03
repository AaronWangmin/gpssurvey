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

import java.io.InputStream;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDThread;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.Reporting;
import net.sf.marineapi.nmea.io.SentenceReader;

/**
 * The DepthFinder reader class.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
