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
package uk.org.rlinsdale.gpssurvey.informationstore;

import java.io.IOException;
import java.io.PrintWriter;
import uk.org.rlinsdale.gpssurvey.informationstore.Recorder.SessionWriterCommand;
import uk.org.rlinsdale.rpi.threadlib.MDThread;

/**
 * The Recorder for Session files.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class RecorderSessionWriter extends MDThread<SessionWriterCommand> {

    private PrintWriter sessionout = null;

    /**
     * Create and start the Session Recorder thread.
     */
    public static void createAndStart() {
        RecorderSessionWriter thread = new RecorderSessionWriter();
        thread.start();
    }

    private RecorderSessionWriter() {
        super("SessionWriter", SessionWriterCommand.CLOSE, 50);
    }

    @Override
    protected void processMessage(SessionWriterCommand command, Object commandParameters) throws IOException {
        switch (command) {
            case CLOSE:
                if (sessionout != null) {
                    sessionout.println("END");
                    sessionout.close();
                }
                break;
            case OPENFILE:
                sessionout = new PrintWriter((String) commandParameters);
                break;
            case WRITE:
                sessionout.println((String) commandParameters);
        }
    }
}
