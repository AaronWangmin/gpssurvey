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
package uk.org.rlinsdale.gpssurvey.gpsreader;

import java.io.IOException;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService.Exitcode;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.VTGSentence;

/**
 * The Sentence Listener for the GPS.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSSentenceListener implements SentenceListener {

    private final GPSMessageConsolidator messageconsolidator = new GPSMessageConsolidator();

    /**
     * Listener is called on every sentence read.
     *
     * @param event the sentence event
     */
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