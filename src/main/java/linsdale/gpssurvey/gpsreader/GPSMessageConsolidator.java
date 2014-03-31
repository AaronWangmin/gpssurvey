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
package linsdale.gpssurvey.gpsreader;

import java.io.IOException;
import linsdale.gpssurvey.Altitude;
import linsdale.gpssurvey.GPSDate;
import linsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import linsdale.gpssurvey.GPSTime;
import linsdale.gpssurvey.HDOP;
import linsdale.gpssurvey.Position;
import linsdale.gpssurvey.Latitude;
import linsdale.gpssurvey.Longitude;
import linsdale.rpi.threadlib.MDTService;
import linsdale.rpi.threadlib.Reporting;
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
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSMessageConsolidator {

    private ConsolidatedGPSData gpsdata = null;
    private GPSTime oldtime = null;

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
        gpsdata.date = new GPSDate(d.getDay(),d.getMonth(),d.getYear());
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

    public class ConsolidatedGPSData {

        public int setup = 0;
        public static final int GGASETUP = 1;
        public static final int GSASETUP = 2;
        public static final int VTGSETUP = 4;
        public static final int RMCSETUP = 8;
        public static final int ALLSETUP = GGASETUP | GSASETUP | VTGSETUP | RMCSETUP;
        // from GGA
        public GPSTime time;
        public Altitude altitude;
        public Position position;
        public int satelliteCount;
        // from GSA
        public HDOP hDOP;
        // from VTG
        public double course;
        public double speed; // m/s
        // from RMC
        public GPSDate date;
    }
}
