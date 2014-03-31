package linsdale.gpssurvey.informationstore;

import java.io.IOException;
import java.io.PrintWriter;
import linsdale.gpssurvey.informationstore.Recorder.SessionWriterCommand;
import linsdale.rpi.threadlib.MDThread;

/**
 *
 * @author richard
 */
public class RecorderSessionWriter extends MDThread<SessionWriterCommand> {

    private PrintWriter sessionout = null;

    public static RecorderSessionWriter createAndStart() {
        RecorderSessionWriter thread = new RecorderSessionWriter();
        thread.start();
        return thread;
    }

    public RecorderSessionWriter() {
        super("SessionWriter", SessionWriterCommand.CLOSE, 50);
    }

    @Override
    protected void processMessage(SessionWriterCommand command, Object commandParameters) throws IOException {
        switch (command) {
            case CLOSE:
                if (sessionout != null ) {
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
