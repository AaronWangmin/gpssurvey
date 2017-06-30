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

import com.pi4j.io.i2c.I2CFactory;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.Depth;
import uk.theretiredprogrammer.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.theretiredprogrammer.gpssurvey.depthfinder.DepthFinderMessageConsolidator.ConsolidatedDepthFinderData;
import uk.theretiredprogrammer.gpssurvey.depthfinder.DepthFinderReader;
import uk.theretiredprogrammer.gpssurvey.depthfinder.ManualDepthListener;
import uk.theretiredprogrammer.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import uk.theretiredprogrammer.gpssurvey.gpsreader.GPSReader;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData.Location;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction.Button;
import uk.theretiredprogrammer.gpssurvey.input.IrListener;
import uk.theretiredprogrammer.gpssurvey.input.KeyboardListener;
import uk.theretiredprogrammer.gpssurvey.ui.View;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplayOutputThread;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService.Exitcode;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDThread;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;

/**
 * The Controller class for the application.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Controller extends MDThread<Command> {

    private final static LocationData INFORMATION = new LocationData();
    private final LocationCalculator locCalc = new LocationCalculator();
    private final static ReferencePointCalculator REFCALC = new ReferencePointCalculator();
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
        } catch (IOException | I2CFactory.UnsupportedBusNumberException e) {
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
                if (locCalc.depthDataPoint(INFORMATION, depthdata)) {
                    recorder.locationChanged(INFORMATION);
                    view.dataChanged();
                }
                if ((ref = REFCALC.depthDataPoint(depthdata)) != null) {
                    INFORMATION.setReferenceLocation(ref);
                    recorder.recordReferenceLocation(INFORMATION);
                    view.dataChanged();
                }
                break;
            case GPSINFO:
                ConsolidatedGPSData gpsdata = (ConsolidatedGPSData) commandParameters;
                if (locCalc.gpsDataPoint(INFORMATION, gpsdata)) {
                    recorder.locationChanged(INFORMATION);
                    view.dataChanged();
                }
                if ((ref = REFCALC.gpsDataPoint(gpsdata)) != null) {
                    INFORMATION.setReferenceLocation(ref);
                    recorder.recordReferenceLocation(INFORMATION);
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
        return INFORMATION;
    }

    /**
     * Start the Reference Location calculation.
     */
    public static final void obtainReferenceLocation() {
        REFCALC.start();
    }

    /**
     * Record the current Position on storage.
     *
     * @throws IOException if problems
     */
    public static final void recordPoint() throws IOException {
        recorder.recordPoint(INFORMATION);
        view.dataChanged();
    }

    /**
     * Start or Stop the current recording of a track, saving data if necessary.
     *
     * @throws IOException if problems
     */
    public static final void startStopRecording() throws IOException {
        recorder.startStopRecording(INFORMATION);
        view.dataChanged();
    }

    /**
     * Cancel current recording of track and abandon data.
     *
     * @throws IOException if problems
     */
    public static final void cancelRecording() throws IOException {
        recorder.cancelRecording(INFORMATION);
        view.dataChanged();
    }
}
