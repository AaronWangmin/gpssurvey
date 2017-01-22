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
package uk.theretiredprogrammer.gpssurvey.depthfinder;

import java.io.IOException;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService.Exitcode;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.VHWSentence;

/**
 * The Sentence Listener for the DepthFinder.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
