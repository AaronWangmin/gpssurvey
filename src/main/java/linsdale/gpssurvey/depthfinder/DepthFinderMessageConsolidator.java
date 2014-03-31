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
package linsdale.gpssurvey.depthfinder;

import java.io.IOException;
import linsdale.gpssurvey.Depth;
import linsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import linsdale.rpi.threadlib.MDTService;
import linsdale.rpi.threadlib.Reporting;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.VHWSentence;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class DepthFinderMessageConsolidator {

    private ConsolidatedDepthFinderData depthfinderdata = null;

    public void insertVHW(VHWSentence s) {
        Reporting.report("Depth Finder Reader", 4, "VHW sentence presented for processing");
        Reporting.report("Depth Finder Reader", 6, "%s", s);
        // ignore at present
    }

    public void insertMTW(MTWSentence s) throws IOException {
        Reporting.report("Depth Finder Reader", 4, "MTW sentence presented for processing");
        Reporting.report("Depth Finder Reader", 6, "%s", s);
        // ignore at present
    }

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

    public static class ConsolidatedDepthFinderData {
        public int setup = 0;
        public static final int VHWSETUP = 1;
        public static final int MTWSETUP = 2;
        public static final int DPTSETUP = 4;
        public static final int ALLSETUP = VHWSETUP | MTWSETUP | DPTSETUP;
        //
        public Depth depth;
    }
}
