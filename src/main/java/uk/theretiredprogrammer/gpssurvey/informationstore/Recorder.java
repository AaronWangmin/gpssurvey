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

import java.io.File;
import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.Position;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData.Location;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;

/**
 * The Recorder class. Provides the data management associated with the
 * recording of both session and track recordings.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Recorder {

    /**
     * Commands used to control the Session Writer
     */
    protected enum SessionWriterCommand {

        /**
         * Close Command
         */
        CLOSE,
        /**
         * Open File Command
         */
        OPENFILE,
        /**
         * Write Command
         */
        WRITE
    }

    /**
     * Commands used to control the Record (Track) Writer
     */
    protected enum RecordWriterCommand {

        /**
         * Close Command
         */
        CLOSE,
        /**
         * Open File Command
         */
        OPENFILE,
        /**
         * Write Command
         */
        WRITE,
        /**
         * Close File Command
         */
        CLOSEFILE,
        /**
         * Cancel File Command
         */
        CANCELFILE
    }

    private String filename;
    private boolean sessionfileopened = false;

    /**
     * Constructor
     */
    public Recorder() {
        Reporting.registerControl("Recorder", 'r');
    }

    /**
     * Set a new Location as a track location.
     *
     * @param ld the location
     * @throws IOException if problems
     */
    public void locationChanged(LocationData ld) throws IOException {
        Location l = ld.getLocation();
        if (l != null) {
            if (ld.isRecording()) {
                MDTService.sendMessage("RecordWriter", RecordWriterCommand.WRITE, createRecord("RECORD", ld, l));
            }
        }
    }

    /**
     * Start or Stop Recording a Record (Track).
     *
     * @param ld the location
     * @throws IOException if problems
     */
    public void startStopRecording(LocationData ld) throws IOException {
        if (ld.isRecording()) {
            // end and save
            MDTService.sendMessage("RecordWriter", RecordWriterCommand.CLOSEFILE);
            writeToSessionFile("ENDRECORDING", ld, filename);
            ld.setRecording(false);
            ld.addStartEndPoints();
        } else {
            // start
            filename = String.format("recording_%s_%s.csv", ld.getIsodatedisplay(), ld.getIsotimedisplay());
            if (new File(filename).exists()) {
                String msg = String.format("Cannot start recording - file name already exists ( %s)", filename);
                filename = null;
                throw new IOException(msg);
            }
            Location l = ld.getLocation();
            if (l != null) {
                MDTService.sendMessage("RecordWriter", RecordWriterCommand.OPENFILE, filename);
                ld.setRecording(true);
                ld.setStartPosition();
                writeToSessionFile("RECORDING", ld);
                MDTService.sendMessage("RecordWriter", RecordWriterCommand.WRITE, createRecord("RECORD", ld, l));
            }
        }
    }

    /**
     * Cancel Recording of a Record (Track).
     *
     * @param ld the location
     * @throws IOException if problems
     */
    public void cancelRecording(LocationData ld) throws IOException {
        if (ld.isRecording()) {
            writeToSessionFile("CANCELRECORDING", ld);
            ld.setRecording(false);
            ld.clearStartPosition();
            MDTService.sendMessage("RecordWriter", RecordWriterCommand.CANCELFILE);
        }
    }

    /**
     * Record the location as a point in the session file.
     *
     * @param ld the location
     * @return true if point recorded
     * @throws IOException if problems
     */
    public boolean recordPoint(LocationData ld) throws IOException {
        Location l = ld.getLocation();
        if (l != null) {
            writeToSessionFile("POSITION", ld, l);
            ld.addPoint();
            return true;
        }
        return false;
    }

    /**
     * Record the location as the reference location in the session file.
     *
     * @param ld the reference location
     * @return true if location is recorded
     * @throws IOException if problems
     */
    public boolean recordReferenceLocation(LocationData ld) throws IOException {
        Location l = ld.getReferenceLocation();
        if (l != null) {
            writeToSessionFile("REFERENCE", ld, l);
            return true;
        }
        return false;
    }

    private void writeToSessionFile(String type, LocationData ld, Location l) throws IOException {
        if (!sessionfileopened) {
            openSessionFile(ld);
        }
        MDTService.sendMessage("SessionWriter", SessionWriterCommand.WRITE, createRecord(type, ld, l));
    }

    private void writeToSessionFile(String type, LocationData ld, String parameter) throws IOException {
        if (!sessionfileopened) {
            openSessionFile(ld);
        }
        StringBuilder sb = buildRecordHeader(type, ld);
        sb.append(',');
        sb.append(parameter);
        MDTService.sendMessage("SessionWriter", SessionWriterCommand.WRITE, sb.toString());
    }

    private void writeToSessionFile(String type, LocationData ld) throws IOException {
        if (!sessionfileopened) {
            openSessionFile(ld);
        }
        MDTService.sendMessage("SessionWriter", SessionWriterCommand.WRITE, buildRecordHeader(type, ld).toString());
    }

    private void openSessionFile(LocationData ld) throws IOException {
        String fname = String.format("session_%s_%s.csv", ld.getIsodatedisplay(), ld.getIsotimedisplay());
        if (new File(fname).exists()) {
            throw new IOException(String.format("Cannot open session recording file - file name already exists ( %s)", fname));
        }
        MDTService.sendMessage("SessionWriter", SessionWriterCommand.OPENFILE, fname);
        sessionfileopened = true;
        writeToSessionFile("START", ld);
    }

    private StringBuilder buildRecordHeader(String type, LocationData ld) {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(',');
        sb.append(ld.getIsodatedisplay());
        sb.append(',');
        sb.append(ld.getIsotimedisplay());
        return sb;
    }

    private String createRecord(String type, LocationData ld, Location l) {
        StringBuilder sb = buildRecordHeader(type, ld);
        sb.append(',');
        sb.append(l.getHDOP());
        sb.append(',');
        Position p = l.getPosition();
        sb.append(p.latitude.toString());
        sb.append(',');
        sb.append(p.longitude.toString());
        sb.append(',');
        sb.append(p.x.toString());
        sb.append(',');
        sb.append(p.y.toString());
        sb.append(',');
        sb.append(l.getAltitude().toString());
        sb.append(',');
        sb.append(l.getDepth().toString());
        return sb.toString();
    }
}
