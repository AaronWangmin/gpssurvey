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

import java.io.File;
import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.Position;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData.Location;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.Reporting;

/**
 * The Recorder class. Provides the data management associated with the
 * recording of both session and track recordings.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
