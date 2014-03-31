package linsdale.gpssurvey.depthfinder;

import java.io.IOException;
import linsdale.rpi.threadlib.MDTService;
import linsdale.rpi.threadlib.MDTService.Exitcode;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.VHWSentence;

/**
 *
 * @author richard
 */
public class DepthFinderSentenceListener implements SentenceListener {

    private final DepthFinderMessageConsolidator messageconsolidator = new DepthFinderMessageConsolidator();

    @Override
    public void sentenceRead(SentenceEvent event) {
        try {
            Sentence s = event.getSentence();
            if (s instanceof VHWSentence) {
                messageconsolidator.insertVHW((VHWSentence) s);
            } else if (s instanceof MTWSentence) {
                messageconsolidator.insertMTW((MTWSentence) s);
            } else if (s instanceof DPTSentence) {
                messageconsolidator.insertDPT((DPTSentence) s);
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
