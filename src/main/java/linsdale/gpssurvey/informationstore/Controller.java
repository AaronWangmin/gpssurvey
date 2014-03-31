package linsdale.gpssurvey.informationstore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import linsdale.gpssurvey.Depth;
import linsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import linsdale.gpssurvey.depthfinder.DepthFinderMessageConsolidator.ConsolidatedDepthFinderData;
import linsdale.gpssurvey.depthfinder.DepthFinderReader;
import linsdale.gpssurvey.depthfinder.ManualDepthListener;
import linsdale.gpssurvey.gpsreader.GPSMessageConsolidator.ConsolidatedGPSData;
import linsdale.gpssurvey.gpsreader.GPSReader;
import linsdale.gpssurvey.informationstore.LocationData.Location;
import linsdale.gpssurvey.input.IRControlAction.Button;
import linsdale.gpssurvey.input.IrListener;
import linsdale.gpssurvey.input.KeyboardListener;
import linsdale.gpssurvey.ui.View;
import linsdale.rpi.screenlib.SerialTFTDisplayOutputThread;
import linsdale.rpi.threadlib.MDTService;
import linsdale.rpi.threadlib.MDTService.Exitcode;
import linsdale.rpi.threadlib.MDThread;
import linsdale.rpi.threadlib.Reporting;

/**
 *
 * @author richard
 */
public class Controller extends MDThread<Command> {

    private final static LocationData information = new LocationData();
    private final LocationCalculator locCalc = new LocationCalculator();
    private final static ReferencePointCalculator refCalc = new ReferencePointCalculator();
    private static Recorder recorder;
    private static View view;
    private final boolean useManualDepth;

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

    public static final LocationData getLocationData() {
        return information;
    }

    public static final void obtainReferenceLocation() {
        refCalc.start();
    }

    public static final void recordPoint() throws IOException {
        recorder.recordPoint(information);
        view.dataChanged();
    }

    public static final void startStopRecording() throws IOException {
        recorder.startStopRecording(information);
        view.dataChanged();
    }

    public static final void cancelRecording() throws IOException {
        recorder.cancelRecording(information);
        view.dataChanged();
    }
}
