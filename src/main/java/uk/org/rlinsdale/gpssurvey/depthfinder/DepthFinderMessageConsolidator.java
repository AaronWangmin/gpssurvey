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
import uk.org.rlinsdale.gpssurvey.Depth;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.Reporting;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.VHWSentence;

/**
 * The Consolidator of depth information to create a Depth message parameter set
 * and to send the message when all information has been consolidated.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class DepthFinderMessageConsolidator {

    private ConsolidatedDepthFinderData depthfinderdata = null;

    /**
     * Process a VHW sentence.
     *
     * @param s sentence
     */
    public void insertVHW(VHWSentence s) {
        Reporting.report("Depth Finder Reader", 4, "VHW sentence presented for processing");
        Reporting.report("Depth Finder Reader", 6, "%s", s);
        // ignore at present
    }

    /**
     * Process a MTW sentence.
     *
     * @param s sentence
     */
    public void insertMTW(MTWSentence s) {
        Reporting.report("Depth Finder Reader", 4, "MTW sentence presented for processing");
        Reporting.report("Depth Finder Reader", 6, "%s", s);
        // ignore at present
    }

    /**
     * Process a DPT sentence.
     *
     * @param s sentence
     * @throws IOException if send message fails
     */
    public void insertDPT(DPTSentence s) throws IOException {
        Reporting.report("Depth Finder Reader", 4, "DPT sentence presented for processing");
        Reporting.report("Depth Finder Reader", 6, "%s", s);
        //
        depthfinderdata = new ConsolidatedDepthFinderData();
        depthfinderdata.depth = new Depth(s.getDepth());
        sendMessage();
    }

    private void sendMessage() throws IOException {
        Reporting.report("Depth Finder Reader", 4, "Sentence set processed - information send to Information Centre");
        MDTService.sendMessage("Controller", Command.DEPTHINFO, depthfinderdata);
        depthfinderdata = null;
    }

    /**
     * Data Structure to store and transfer the consolidated depth information.
     */
    public static class ConsolidatedDepthFinderData {

        /**
         * The depth of water
         */
        public Depth depth;
    }
}
