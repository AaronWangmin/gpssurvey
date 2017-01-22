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
import uk.theretiredprogrammer.gpssurvey.Depth;
import uk.theretiredprogrammer.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.MDTService;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.VHWSentence;

/**
 * The Consolidator of depth information to create a Depth message parameter set
 * and to send the message when all information has been consolidated.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
