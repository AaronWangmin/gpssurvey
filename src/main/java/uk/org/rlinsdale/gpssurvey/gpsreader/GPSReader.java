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
package uk.org.rlinsdale.gpssurvey.gpsreader;

import java.io.InputStream;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDThread;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.Reporting;
import net.sf.marineapi.nmea.io.SentenceReader;

/**
 * The GPS reader class.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSReader extends MDThread<Command> {

    private final SentenceReader reader;
    private boolean running = false;

    /**
     * Create the GPS reader thread and start it.
     * 
     * @param gpsIn the input stream for reading GPS sentences
     */
    public static void createAndStart(InputStream gpsIn) {
        GPSReader thread = new GPSReader(gpsIn);
        thread.start();
        thread.sendMessage(Command.START);
    }

    private GPSReader(InputStream gpsIn) {
        super("GPS Reader", Command.CLOSE);
        reader = new SentenceReader(gpsIn);
        reader.addSentenceListener(new GPSSentenceListener());
        Reporting.registerControl("GPS Reader", 'g');
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) {
        Reporting.report("GPS Reader", 3, "Processing command %s", command);
        switch (command) {
            case START:
                if (!running) {
                    Reporting.report("GPS Reader", 1, "Started");
                    reader.start();
                    running = true;
                }
                break;
            case STOP:
                if (running) {
                    Reporting.report("GPS Reader", 1, "Stopped");
                    reader.stop();
                    running = false;
                }
                break;
            case CLOSE:
                if (running) {
                    Reporting.report("GPS Reader", 1, "Stopped");
                    reader.stop();
                }
                break;
        }
    }
}
