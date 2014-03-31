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
 * @author richard
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
