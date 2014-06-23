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
package uk.org.rlinsdale.gpssurvey.depthfinder;

import java.io.IOException;
import uk.org.rlinsdale.rpi.threadlib.MDTService;
import uk.org.rlinsdale.rpi.threadlib.MDTService.Exitcode;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.VHWSentence;

/**
 * The Sentence Listener for the DepthFinder.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class DepthFinderSentenceListener implements SentenceListener {

    private final DepthFinderMessageConsolidator messageconsolidator = new DepthFinderMessageConsolidator();

    /**
     * Listener is called on every sentence read.
     *
     * @param event the sentence event
     */
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

    /**
     * called when reader is paused
     */
    @Override
    public void readingPaused() {
    }

    /**
     * called when reader is started
     */
    @Override
    public void readingStarted() {
    }

    /**
     * called when reader is stopped
     */
    @Override
    public void readingStopped() {
    }
}
