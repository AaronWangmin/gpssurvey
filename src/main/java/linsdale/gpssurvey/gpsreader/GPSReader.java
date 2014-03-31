package linsdale.gpssurvey.gpsreader;

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
public class GPSReader extends MDThread<Command> {

    private final SentenceReader reader;
    private boolean running = false;

    public static GPSReader createAndStart(InputStream gpsIn) throws IOException {
        GPSReader thread = new GPSReader(gpsIn);
        thread.start();
        thread.sendMessage(Command.START);
        return thread;
    }

    public GPSReader(InputStream gpsIn) {
        super("GPS Reader", Command.CLOSE);
        reader = new SentenceReader(gpsIn);
        reader.addSentenceListener(new GPSSentenceListener());
        Reporting.registerControl("GPS Reader", 'g');
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) {
        Reporting.report("GPS Reader", 3, "Processing command %s", command);
        switch (command) {
            case START:
                if (!running) {
                    Reporting.report("GPS Reader", 1, "Started");
                    reader.start();
                    running = true;
                }
                break;
            case STOP:
                if (running) {
                    Reporting.report("GPS Reader", 1, "Stopped");
                    reader.stop();
                    running = false;
                }
                break;
            case CLOSE:
                if (running) {
                    Reporting.report("GPS Reader", 1, "Stopped");
                    reader.stop();
                }
                break;
        }
    }
}
