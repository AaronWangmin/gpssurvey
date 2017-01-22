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

import java.io.IOException;
import java.io.PrintWriter;
import uk.theretiredprogrammer.gpssurvey.informationstore.Recorder.SessionWriterCommand;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDThread;

/**
 * The Recorder for Session files.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
