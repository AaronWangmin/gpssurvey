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
package uk.theretiredprogrammer.gpssurvey.informationstore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.gpssurvey.informationstore.Recorder.RecordWriterCommand;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDThread;

/**
 * The Recorder for Record (track) files.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class RecorderRecordWriter extends MDThread<RecordWriterCommand> {

    /**
     * The filename to be used for the record (track) files
     */
    public String filename = null;

    /**
     * The list of list to be inserted into the file.
     */
    public List<String> lines;

    /**
     * Create and start the Record (track) Recorder thread.
     */
    public static void createAndStart() {
        RecorderRecordWriter thread = new RecorderRecordWriter();
        thread.start();
    }

    private RecorderRecordWriter() {
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
