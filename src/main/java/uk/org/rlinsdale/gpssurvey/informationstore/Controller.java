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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.Depth;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.gpssurvey.depthfinder.DepthFinderMessageConsolidator.ConsolidatedDepthFinderData;
import uk.org.rlinsdale.gpssurvey.depthfinder.DepthFinderReader;
import uk.org.rlinsdale.gpssurvey.depthfinder.ManualDepthListener;
import uk.org.rlinsdale.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import uk.org.rlinsdale.gpssurvey.gpsreader.GPSReader;
import uk.org.rlinsdale.gpssurvey.informationstore.LocationData.Location;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction.Button;
import uk.org.rlinsdale.gpssurvey.input.IrListener;
import uk.org.rlinsdale.gpssurvey.input.KeyboardListener;
import uk.org.rlinsdale.gpssurvey.ui.View;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplayOutputThread;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService.Exitcode;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDThread;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.Reporting;

/**
 * The Controller class for the application.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class Controller extends MDThread<Command> {

    private final static LocationData information = new LocationData();
    private final LocationCalculator locCalc = new LocationCalculator();
    private final static ReferencePointCalculator refCalc = new ReferencePointCalculator();
    private static Recorder recorder;
    private static View view;
    private final boolean useManualDepth;

    /**
     * Constructor.
     *
     * @param gpsIn the GPS device inputstream
     * @param screenOut the display device outputstream
     * @param screenIn the display device inputstream
     * @param depthfinderIn the depthfinder device inputstream
     * @param useKeyboardInput true if keyboard input is to be used
     * @param coordinates true if positions are to be displayed as x,y
     * coordinates, other latitude/longitude format will be used.
     * @param displayDepth true is depth is to be displayed, other altitude will
     * be shown
     * @param knots true if speed is to be displayed in knots, other metres/sec
     * will be used
     */
    public Controller(FileInputStream gpsIn, FileOutputStream screenOut, FileInputStream screenIn, FileInputStream depthfinderIn,
            boolean useKeyboardInput, boolean coordinates, boolean displayDepth, boolean knots) {
        super("Controller", Command.CLOSE, 6);
        useManualDepth = depthfinderIn == null;
        MDTService.enableShutdownHandler();
        Reporting.registerControl("Controller", 'c');
        try {
            RecorderSessionWriter.createAndStart();
            RecorderRecordWriter.createAndStart();
            recorder = new Recorder();
            //
            SerialTFTDisplayOutputThread.createAndStart(screenOut, screenIn);
            view = new View(screenOut, screenIn, coordinates, displayDepth, knots);
            //
            GPSReader.createAndStart(gpsIn);
            //
            if (useManualDepth) {
                ManualDepthListener.createAndStart();
            } else {
                DepthFinderReader.createAndStart(depthfinderIn);
            }
            //
            if (useKeyboardInput) {
                KeyboardListener.createAndStart();
            } else {
                IrListener.createAndStart();
            }
        } catch (IOException e) {
            MDTService.reportExceptionAndExit(e, Exitcode.EXIT_PROGFAIL);
        }
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) throws IOException {
        Reporting.report("Controller", 3, "Processing command %s", command);
        Location ref;
        switch (command) {
            case CLOSE:
                break;
            case DEPTHINFO:
                Depth depthdata = ((ConsolidatedDepthFinderData) commandParameters).depth;
                if (locCalc.depthDataPoint(information, depthdata)) {
                    recorder.locationChanged(information);
                    view.dataChanged();
                }
                if ((ref = refCalc.depthDataPoint(depthdata)) != null) {
                    information.setReferenceLocation(ref);
                    recorder.recordReferenceLocation(information);
                    view.dataChanged();
                }
                break;
            case GPSINFO:
                ConsolidatedGPSData gpsdata = (ConsolidatedGPSData) commandParameters;
                if (locCalc.gpsDataPoint(information, gpsdata)) {
                    recorder.locationChanged(information);
                    view.dataChanged();
                }
                if ((ref = refCalc.gpsDataPoint(gpsdata)) != null) {
                    information.setReferenceLocation(ref);
                    recorder.recordReferenceLocation(information);
                    view.dataChanged();
                }
                break;
            case BUTTON:
                Button button = (Button) commandParameters;
                Reporting.report("Controller", 3, "Processing command %S BUTTON", button);
                switch (button) {
                    case POWERDOWN:
                        view.displayPowerDown();
                        MDTService.fireShutdownHandler(Exitcode.EXIT_POWEROFF);
                        break;
                    case POWER:
                        view.displayPowerDownSelection();
                        break;
                    default:
                        view.processButtonInScreenContext(button, useManualDepth);
                }
        }
    }

    /**
     * Get information about current location.
     *
     * @return the location information
     */
    public static final LocationData getLocationData() {
        return information;
    }

    /**
     * Start the Reference Location calculation.
     */
    public static final void obtainReferenceLocation() {
        refCalc.start();
    }

    /**
     * Record the current Position on storage.
     *
     * @throws IOException if problems
     */
    public static final void recordPoint() throws IOException {
        recorder.recordPoint(information);
        view.dataChanged();
    }

    /**
     * Start or Stop the current recording of a track, saving data if necessary.
     *
     * @throws IOException if problems
     */
    public static final void startStopRecording() throws IOException {
        recorder.startStopRecording(information);
        view.dataChanged();
    }

    /**
     * Cancel current recording of track and abandon data.
     *
     * @throws IOException if problems
     */
    public static final void cancelRecording() throws IOException {
        recorder.cancelRecording(information);
        view.dataChanged();
    }
}
