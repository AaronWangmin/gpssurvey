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
    /**
     * Standard command set - used as commmand in inter-Thread communications.
     */
    public static enum Command {

        /**
         * Close down the thread
         */
        CLOSE,
        /**
         * Start the thread activity
         */
        START,
        /**
         * Stop the thread activity
         */
        STOP,
        /**
         * GPS Information as parameters
         */
        GPSINFO,
        /**
         * Depth Information as parameters
         */
        DEPTHINFO,
        /**
         * Increment the Depth by 0.25m (manual depth tracking)
         */
        INCDEPTH,
        /**
         * Decrement the Depth by 0.25m (manual depth tracking)
         */
        DECDEPTH,
        /**
         * Button Information as parameter
         */
        BUTTON
    }

    /**
     * The Application Main method.
     *
     * Command line arguments are:
     *
     * 1) Define display options
     *
     * --display (or --show) "comma separated list of options"
     *
     * Options are:
     *
     * altitude or a - display altitude information (* - alternatives)
     *
     * depth or d - display depth information (* - alternatives)
     *
     * knots or k - display speed in knots (# - alternatives)
     *
     * mps or m - display speed in metre/second (# - alternatives)
     *
     * longlat or degres or ll - display positions using longitude and latitude
     * (~ - alternatives)
     *
     * coordinates or metres or xy - display positions using metre based
     * coordinates (~ - alternatives)
     *
     * 2) Define the GPS device (defaults is /dev/ttyAMA0)
     *
     * --gps (or -g) "gps device"
     *
     * 3) Define the Depthfinder device(default is /dev/ttyUSB1)
     *
     * --depthfinder (or -d) "depthfinder device"
     *
     * 4) Define the Screen device (default is /dev/ttyUSB0)
     *
     * --screen (or -s) "screen device"
     *
     * 5) Use keyboard input (default is IR receiver)
     *
     * --keyboardinput (or -i)
     *
     * 6) Set the reporting levels for the various sections
     *
     * --report (or -r) "reportingswitches"
     *
     * 7) Provide help and then exit --help (or -h)
     *
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
                                knots = false;
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
