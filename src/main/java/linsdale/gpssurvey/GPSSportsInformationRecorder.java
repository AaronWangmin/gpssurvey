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
package linsdale.gpssurvey;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.rpi.threadlib.MDTService.Exitcode;
import linsdale.rpi.threadlib.Reporting;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class GPSSportsInformationRecorder {

    // standard command set being used in this application
    public static enum Command {

        CLOSE,
        START,
        STOP,
        GPSINFO,
        DEPTHINFO,
        INCDEPTH,
        DECDEPTH,
        BUTTON
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // process args
        String gpsString = "/dev/ttyAMA0";
        String screenString = "/dev/ttyUSB0";
        String depthFinderString = "/dev/ttyUSB1";
        String reportingString = "";
        boolean displayDepth = false;
        boolean useKeyboardInput = false; // default is to use IrInput
        boolean knots = false;
        boolean coordinates = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--display":
                case "--show":
                    i++;
                    for (String p : args[i].split(",")) {
                        switch (p.trim()) {
                            case "ll":
                            case "longlat":
                            case "degrees":
                                coordinates = false;
                                break;
                            case "xy":
                            case "coordinate":
                            case "metres":
                                coordinates = true;
                                break;
                            case "k":
                            case "kt":
                                knots = true;
                                break;
                            case "m":
                            case "mps":
                                knots = true;
                                break;
                            case "a":
                            case "altitude":
                                displayDepth = false;
                                break;
                            case "d":
                            case "depth":
                                displayDepth = true;
                                break;
                        }
                    }
                    break;
                case "-g":
                case "--gps":
                    i++;
                    gpsString = args[i];
                    break;
                case "-d":
                case "--depthfinder":
                    i++;
                    gpsString = args[i];
                    break;
                case "-s":
                case "--screen":
                    i++;
                    screenString = args[i];
                    break;
                case "-i":
                case "--keyboardinput":
                    useKeyboardInput = true;
                    break;
                case "-r":
                case "--report":
                    i++;
                    reportingString = args[i];
                    break;
                case "-h":
                case "--help":
                    System.out.println("gps [[-g|--gps] file]");
                    System.out.println("    [[-d|--depthfinder] file]");
                    System.out.println("    [[-s|--screen] file]");
                    System.out.println("    [[-i|--keyboardinput]");
                    System.out.println("    [[--display|--show] [[ll|longlat|degrees|xy|coordinate|metres|k|kt|m|mps|a|altitude|d|depth],]* ]");
                    System.out.println("    [[-r|--report] reportingswitches]");
                    System.out.println("    [-h|--help]");
                    System.exit(Exitcode.EXIT_OK.rc);
                    break;
                default:
                    System.err.println(String.format("Unknown switch: %s", args[i]));
                    System.exit(Exitcode.EXIT_PROGFAIL.rc);
            }
        }

        @SuppressWarnings("UnusedAssignment")
        FileInputStream gpsIn = null;
        try {
            gpsIn = new FileInputStream(gpsString);
        } catch (FileNotFoundException ex) {
            System.err.println(String.format("GPS file does not exist: %s", gpsString));
            System.exit(Exitcode.EXIT_PROGFAIL.rc);
        }
        @SuppressWarnings("UnusedAssignment")
        FileOutputStream screenOut = null;
        try {
            screenOut = new FileOutputStream(screenString);
        } catch (FileNotFoundException ex) {
            System.err.println(String.format("Screen Output file does not exist: %s", screenString));
            System.exit(Exitcode.EXIT_PROGFAIL.rc);
        }
        @SuppressWarnings("UnusedAssignment")
        FileInputStream screenIn = null;
        try {
            screenIn = new FileInputStream(screenString);
        } catch (FileNotFoundException ex) {
            System.err.println(String.format("Screen Input file does not exist: %s", screenString));
            System.exit(Exitcode.EXIT_PROGFAIL.rc);
        }
        FileInputStream depthFinderIn;
        try {
            depthFinderIn = new FileInputStream(depthFinderString);
        } catch (FileNotFoundException ex) {
            depthFinderIn = null;
        }
        //
        Reporting.setReportingLevels(reportingString);
        //
        new Controller(gpsIn, screenOut, screenIn, depthFinderIn,
                useKeyboardInput, coordinates, displayDepth, knots).start();
    }
}
