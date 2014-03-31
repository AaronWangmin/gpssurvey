package linsdale.gpssurvey.gpsreader;

import java.io.IOException;
import linsdale.rpi.threadlib.MDTService;
import linsdale.rpi.threadlib.MDTService.Exitcode;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.VTGSentence;

/**
 *
 * @author richard
 */
public class GPSSentenceListener implements SentenceListener {

    private final GPSMessageConsolidator messageconsolidator = new GPSMessageConsolidator();

    @Override
    public void sentenceRead(SentenceEvent event) {
        try {
            Sentence s = event.getSentence();
            if (s instanceof GGASentence) {
                messageconsolidator.insertGGA((GGASentence) s);
            } else if (s instanceof GSASentence) {
                messageconsolidator.insertGSA((GSASentence) s);
            } else if (s instanceof VTGSentence) {
                messageconsolidator.insertVTG((VTGSentence) s);
            } else if (s instanceof RMCSentence) {
                messageconsolidator.insertRMC((RMCSentence) s);
            }
        } catch (IOException ex) {
            MDTService.reportExceptionAndExit(ex, Exitcode.EXIT_PROGFAIL);
        }
    }

    @Override
    public void readingPaused() {
    }

    @Override
    public void readingStarted() {
    }

    @Override
    public void readingStopped() {
    }
}
