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
package linsdale.gpssurvey.gpsreader;

import java.io.IOException;
import java.io.InputStream;
import linsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import linsdale.rpi.threadlib.MDThread;
import linsdale.rpi.threadlib.Reporting;
import net.sf.marineapi.nmea.io.SentenceReader;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSReader extends MDThread<Command> {

    private final SentenceReader reader;
    private boolean running = false;

    public static GPSReader createAndStart(InputStream gpsIn) throws IOException {
        GPSReader thread = new GPSReader(gpsIn);
        thread.start();
        thread.sendMessage(Command.START);
        return thread;
    }

    public GPSReader(InputStream gpsIn) {
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
