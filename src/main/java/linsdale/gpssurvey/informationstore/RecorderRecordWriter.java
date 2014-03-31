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
package linsdale.gpssurvey.informationstore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import linsdale.gpssurvey.informationstore.Recorder.RecordWriterCommand;
import linsdale.rpi.threadlib.MDThread;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class RecorderRecordWriter extends MDThread<RecordWriterCommand> {

    public String filename = null;
    public List<String> lines;

    public static RecorderRecordWriter createAndStart() {
        RecorderRecordWriter thread = new RecorderRecordWriter();
        thread.start();
        return thread;
    }

    public RecorderRecordWriter() {
        super("RecordWriter", RecordWriterCommand.CLOSE, 50);
    }

    @Override
    protected void processMessage(RecordWriterCommand command, Object commandParameters) throws IOException {
        switch (command) {
            case CLOSE:
                if (filename != null) {
                    filetostore();
                }
                break;
            case OPENFILE:
                filename = (String) commandParameters;
                lines = new ArrayList<>();
                break;
            case CLOSEFILE:
                filetostore();
                break;
            case CANCELFILE:
                filename = null;
                break;
            case WRITE:
                lines.add((String) commandParameters);
        }
    }

    private void filetostore() throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(filename)) {
            lines.stream().forEach((line) -> {
                out.println(line);
            });
        }
        filename = null;
    }
}
