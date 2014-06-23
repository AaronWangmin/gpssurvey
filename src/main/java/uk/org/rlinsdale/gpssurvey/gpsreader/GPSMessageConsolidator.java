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
import uk.org.rlinsdale.gpssurvey.Altitude;
import uk.org.rlinsdale.gpssurvey.GPSDate;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.gpssurvey.GPSTime;
import uk.org.rlinsdale.gpssurvey.HDOP;
import uk.org.rlinsdale.gpssurvey.Position;
import uk.org.rlinsdale.gpssurvey.Latitude;
import uk.org.rlinsdale.gpssurvey.Longitude;
import uk.org.rlinsdale.rpi.threadlib.MDTService;
import uk.org.rlinsdale.rpi.threadlib.Reporting;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.VTGSentence;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.GpsFixStatus;
import net.sf.marineapi.nmea.util.Time;

/**
 * The Consolidator of GPS information to create a gps message parameter set and
 * to send the message when all information has been consolidated.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSMessageConsolidator {

    private ConsolidatedGPSData gpsdata = null;
    private GPSTime oldtime = null;

    /**
     * Process a GGA sentence.
     *
     * @param s sentence
     */
    public void insertGGA(GGASentence s) {
        Reporting.report("GPS Reader", 4, "GGA sentence presented for processing");
        Reporting.report("GPS Reader", 6, "%s", s);
        if (s.getFixQuality() == GpsFixQuality.INVALID) {
            Reporting.report("GPS Reader", 5, "GGA fix quality INVALID");
            return;
        }
        Time t = s.getTime();
        GPSTime time = new GPSTime(t.getHour(), t.getMinutes(), (int) t.getSeconds());
        if (time.prior(oldtime)) {
            Reporting.report("GPS Reader", 5, "Ignoring historical GGA sentence at %s (previous time %s)\n%s",
                    time.toString(), oldtime.toString(), s);
            return; // skip over GGA sentances which are historical
        }
        if (gpsdata == null) {
            gpsdata = new ConsolidatedGPSData();
        }
        gpsdata.time = time;
        oldtime = time;
        net.sf.marineapi.nmea.util.Position p = s.getPosition();
        gpsdata.altitude = new Altitude(p.getAltitude());
        gpsdata.position = new Position(
                new Latitude(p.getLatitude(), p.getLatitudeHemisphere() == CompassPoint.SOUTH ? 'S' : 'N'),
                new Longitude(p.getLongitude(), p.getLongitudeHemisphere() == CompassPoint.WEST ? 'W' : 'E')
        );
        gpsdata.satelliteCount = s.getSatelliteCount();
        gpsdata.setup |= ConsolidatedGPSData.GGASETUP;
    }

    /**
     * Process a GSA sentence
     *
     * @param s sentence
     * @throws IOException if send message fails
     */
    public void insertGSA(GSASentence s) throws IOException {
        Reporting.report("GPS Reader", 4, "GSA sentence presented for processing");
        Reporting.report("GPS Reader", 6, "%s", s);
        GpsFixStatus gpsfixstatus = s.getFixStatus();
        if (gpsfixstatus != GpsFixStatus.GPS_3D) {
            Reporting.report("GPS Reader", 5, "GSA Fix status != GPS_3D");
            return; // ignore if no fix
        }
        if (gpsdata == null) {
            Reporting.report("GPS Reader", 5, "GSA sentence ignored - no GGA sentence preceeds");
            return; // ignore if no GGA processed
        }
        gpsdata.hDOP = new HDOP(s.getHorizontalDOP());
        gpsdata.setup |= ConsolidatedGPSData.GSASETUP;
        if (gpsdata.setup == ConsolidatedGPSData.ALLSETUP) {
            sendMessage();
        }
    }

    /**
     * Process VTG sentence.
     *
     * @param s sentence
     * @throws IOException if send message fails
     */
    public void insertVTG(VTGSentence s) throws IOException {
        Reporting.report("GPS Reader", 4, "VTG sentence presented for processing");
        Reporting.report("GPS Reader", 6, "%s", s);
        if (s.getMode() != FaaMode.AUTOMATIC) {
            Reporting.report("GPS Reader", 5, "VTG FaaMode is not AUTOMATIC");
            return; // ignore if no fix
        }
        if (gpsdata == null) {
            Reporting.report("GPS Reader", 5, "VTG sentence ignored - no GGA sentence preceeds");
            return; // ignore if no GGA processed
        }
        gpsdata.course = s.getTrueCourse();
        gpsdata.speed = s.getSpeedKmh() * 1000 / 3600; // m/s
        gpsdata.setup |= ConsolidatedGPSData.VTGSETUP;
        //System.out.println("VTG sentence processed");
        if (gpsdata.setup == ConsolidatedGPSData.ALLSETUP) {
            sendMessage();
        }
    }

    /**
     * Process RMC sentence.
     *
     * @param s sentence
     * @throws IOException if send message fails
     */
    public void insertRMC(RMCSentence s) throws IOException {
        Reporting.report("GPS Reader", 4, "RMC sentence presented for processing");
        Reporting.report("GPS Reader", 6, "%s", s);
        if (s.getStatus() != DataStatus.ACTIVE) {
            Reporting.report("GPS Reader", 5, "RMC Status is not ACTIVE");
            return;
        }
        if (gpsdata == null) {
            Reporting.report("GPS Reader", 5, "RMC sentence ignored - no GGA sentence preceeds");
            return; // ignore if no GGA processed
        }
        Date d = s.getDate();
        gpsdata.date = new GPSDate(d.getDay(), d.getMonth(), d.getYear());
        gpsdata.setup |= ConsolidatedGPSData.RMCSETUP;
        if (gpsdata.setup == ConsolidatedGPSData.ALLSETUP) {
            sendMessage();
        }
    }

    private void sendMessage() throws IOException {
        Reporting.report("GPS Reader", 4, "Sentence set processed - information send to Information Centre");
        MDTService.sendMessage("Controller", Command.GPSINFO, gpsdata);
        gpsdata = null;
    }

    /**
     *
     */
    public class ConsolidatedGPSData {

        /**
         * The status of the Consolidated Data - a set of bits indicating which
         * parts of the consolidation have been completed.
         */
        public int setup = 0;

        /**
         * flag indicating GGA data has been set
         */
        public static final int GGASETUP = 1;

        /**
         * flag indicating GSA data has been set
         */
        public static final int GSASETUP = 2;

        /**
         * flag indicating VTG data has been set
         */
        public static final int VTGSETUP = 4;

        /**
         * flag indicating RMC data has been set
         */
        public static final int RMCSETUP = 8;

        /**
         * the desired state of the setup flag indicating that consolidation is
         * complete and is ready for sending.
         */
        public static final int ALLSETUP = GGASETUP | GSASETUP | VTGSETUP | RMCSETUP;
        // from GGA

        /**
         * The GPS time
         */
        public GPSTime time;

        /**
         * The Altitude
         */
        public Altitude altitude;

        /**
         * The Position
         */
        public Position position;

        /**
         * The Satellite Count
         */
        public int satelliteCount;
        // from GSA

        /**
         * The HDOP
         */
        public HDOP hDOP;
        // from VTG

        /**
         * The Course
         */
        public double course;

        /**
         * The Speed (m/s)
         */
        public double speed; // m/s
        // from RMC

        /**
         * The GPS Date
         */
        public GPSDate date;
    }
}
