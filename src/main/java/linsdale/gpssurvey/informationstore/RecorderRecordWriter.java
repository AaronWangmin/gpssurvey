package linsdale.gpssurvey.informationstore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import linsdale.gpssurvey.informationstore.Recorder.RecordWriterCommand;
import linsdale.rpi.threadlib.MDThread;

/**
 *
 * @author richard
 */
public class RecorderRecordWriter extends MDThread<RecordWriterCommand> {

    public String filename = null;
    public List<String> lines;

    public static RecorderRecordWriter createAndStart() {
        RecorderRecordWriter thread = new RecorderRecordWriter();
        thread.start();
        return thread;
    }

    public RecorderRecordWriter() {
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
