package linsdale.gpssurvey.depthfinder;

import java.io.IOException;
import java.io.InputStream;
import linsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import linsdale.rpi.threadlib.MDThread;
import linsdale.rpi.threadlib.Reporting;
import net.sf.marineapi.nmea.io.SentenceReader;

/**
 *
 * @author richard
 */
public class DepthFinderReader extends MDThread<Command> {

    private final SentenceReader reader;
    private boolean running = false;

    public static DepthFinderReader createAndStart(InputStream depthfinderIn) throws IOException {
        DepthFinderReader thread = new DepthFinderReader(depthfinderIn);
        thread.start();
        thread.sendMessage(Command.START);
        return thread;
    }

    public DepthFinderReader(InputStream depthfinderIn) {
        super("Depth Finder Reader", Command.CLOSE);
        reader = new SentenceReader(depthfinderIn);
        reader.addSentenceListener(new DepthFinderSentenceListener());
        Reporting.registerControl("DepthFinderReader", 'd');
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) {
        Reporting.report("Depth Finder Reader", 3, "Processing command %s", command);
        switch (command) {
            case START:
                if (!running) {
                    Reporting.report("Depth Finder Reader", 1, "Started");
                    reader.start();
                    running = true;
                }
                break;
            case STOP:
                if (running) {
                    Reporting.report("Depth Finder Reader", 1, "Stopped");
                    reader.stop();
                    running = false;
                }
                break;
            case CLOSE:
                if (running) {
                    Reporting.report("Depth Finder Reader", 1, "Stopped");
                    reader.stop();
                }
                break;
        }
    }
}
