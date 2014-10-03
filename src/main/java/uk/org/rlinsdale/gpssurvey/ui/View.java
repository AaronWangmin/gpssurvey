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
package uk.org.rlinsdale.gpssurvey.ui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.gpssurvey.informationstore.Controller;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction.Button;
import uk.org.rlinsdale.gpssurvey.uiscreens.ConfirmAtReferenceLocationScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.FinishedScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.HeadingScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.LocationScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.ObtainingReferenceLocationInformationScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.PointDisplay2Screen;
import uk.org.rlinsdale.gpssurvey.uiscreens.PointDisplayScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.PositionScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.RecorderStatusScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.ReferenceLocationScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.SelectPowerDownOptionScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.SplashScreen;
import uk.org.rlinsdale.gpssurvey.uiscreens.WaitingForGPSScreen;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.Screen;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.ScreenManager;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.ScreenSet;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import static uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.FULLBRIGHTNESS;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplay.Orientation;
import uk.org.rlinsdale.rpiembeddedlibrary.screen.SerialTFTDisplayUsingOutputThread;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService.Exitcode;

/**
 * The View Manager.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class View {

    private FinishedScreen finishedscreen;

    /**
     * Constructor. Creates all this screens, screenset and other artifacts
     * needed for the views displayed.
     *
     * @param screenOut the display device output stream
     * @param screenIn the display device input stream
     * @param displaycoordinates if true display position as x,y coordinates
     * otherwise display as latitude/longtitude
     * @param displaydepth if true display depth else display altitude
     * @param displayknots if true display speed in knots otherwise display as
     * metres/sec
     */
    public View(FileOutputStream screenOut, FileInputStream screenIn,
            boolean displaycoordinates, boolean displaydepth, boolean displayknots) {
        try {
            // setup the screen sets
            SerialTFTDisplay display = new SerialTFTDisplayUsingOutputThread(screenOut, screenIn, Orientation.LANDSCAPE, Colour.BLUE, FULLBRIGHTNESS);
            //
            ScreenManager.add(new ScreenSet<>("setup", new SplashScreen(display)));
            ScreenManager.selectScreenSet("setup").paint(); // draw first screen immediately
            ScreenManager.getScreenSet().add(
                    new Screen[]{
                        new WaitingForGPSScreen(display),
                        new ConfirmAtReferenceLocationScreen(display, displaydepth),
                        new ObtainingReferenceLocationInformationScreen(display)
                    });
            ScreenManager.add(new ScreenSet<>(
                    "main", MainCommonButtonActions.getActions(),
                    new Screen[]{
                        new LocationScreen(display, displaycoordinates, displaydepth),
                        new ReferenceLocationScreen(display, displaydepth),
                        new PositionScreen(display),
                        new HeadingScreen(display, displayknots),
                        new RecorderStatusScreen(display),
                        new PointDisplayScreen(display),
                        new PointDisplay2Screen(display)
                    }));
            ScreenManager.add(new ScreenSet<>(
                    "finish", new Screen[]{
                        new SelectPowerDownOptionScreen(display),
                        finishedscreen = new FinishedScreen(display)
                    }));
        } catch (IOException e) {
            MDTService.reportExceptionAndExit(e, Exitcode.EXIT_PROGFAIL);
        }
    }

    /**
     * Display the power down screen.
     *
     * @throws IOException if problems
     */
    public synchronized void displayPowerDown() throws IOException {
        ScreenManager.switchScreenSet("finish", 1); // get the finished screen
        finishedscreen.setMessage("powering down...");
        finishedscreen.paint();
    }

    /**
     * Display the power down selection screen.
     *
     * @throws IOException if problems
     */
    public synchronized void displayPowerDownSelection() throws IOException {
        if (!"finish".equals(ScreenManager.getCurrentScreenSetId())) {
            ScreenManager.switchScreenSet("finish");
        }
    }

    /**
     * Process a button based on current screen context.
     *
     * The processing order is:
     *
     * The screen may have it's own button actions defined
     *
     * The screen set may have it's own button actions defined
     *
     * if manual depth setting is being used, then up and down button are
     * handled to increment or decrement the depth.
     *
     * @param button the button
     * @param useManualDepth true if manual depth setting is being used
     * @throws IOException if problems
     */
    public synchronized void processButtonInScreenContext(Button button, boolean useManualDepth) throws IOException {
        // let the screen handle any buttons it knows about
        Screen currentScreen = ScreenManager.getScreen();
        if (currentScreen instanceof IRControlAction) {
            if (((IRControlAction) currentScreen).actionOnButton(button)) {
                return;
            }

        }
        // now let the common actions for a screen set be processed
        ScreenSet currentScreenSet = ScreenManager.getScreenSet();
        ButtonActions actions = (ButtonActions) currentScreenSet.getActions();
        if (actions != null) {
            if (actions.actionOnButton(button)) {
                return;
            }
        }
        // finally handle up / down if manual depth handling
        if (useManualDepth) {
            // catch unprocessed up / down - pass to manual depth
            switch (button) {
                case UP:
                    MDTService.sendMessage("Manual Depth Listener", Command.INCDEPTH);
                    break;
                case DOWN:
                    MDTService.sendMessage("Manual Depth Listener", Command.DECDEPTH);
            }
        }
    }

    /**
     * Process a change in location data.
     *
     * If the current screen displays location data, then it will be updated.
     *
     * @throws IOException if problems
     */
    public synchronized void dataChanged() throws IOException {
        Screen currentScreen = ScreenManager.getScreen();
        if (currentScreen instanceof ScreenDataChangeProcessor) {
            ((ScreenDataChangeProcessor) currentScreen).dataChanged(Controller.getLocationData());
        }
    }

    /**
     * Display the next screen in the set and then shut down the application.
     *
     * @param exitcode the desired exitcode
     * @param message the message to be passed to the next screen.
     * @throws IOException if problems
     */
    public synchronized static void displayNextScreenAndExit(Exitcode exitcode, String message) throws IOException {
        Screen nextScreen = ScreenManager.getScreenSet().next();
        if (nextScreen instanceof FinishedScreen) {
            ((FinishedScreen) nextScreen).setMessage(message);
            nextScreen.paint();
        }
        MDTService.fireShutdownHandler(exitcode);
    }

    /**
     * Display the next screen in the screen set.
     *
     * @throws IOException if problems
     */
    public synchronized static void displayNextScreen() throws IOException {
        ScreenManager.getScreenSet().next();
    }

    /**
     * Display the previous screen in the screen set.
     *
     * @throws IOException if problems
     */
    public synchronized static void displayPreviousScreen() throws IOException {
        ScreenManager.getScreenSet().previous();
    }

    /**
     * Display the previous screen set.
     *
     * @throws IOException if problems
     */
    public synchronized static void displayPreviousScreenSet() throws IOException {
        ScreenManager.popScreenSet();
    }

    /**
     * Display a new Screenset.
     *
     * @param name the screen set name
     * @throws IOException if problems
     */
    public synchronized static void displayNewScreenSet(String name) throws IOException {
        ScreenManager.switchScreenSet(name);
    }

    /**
     * Repaint screen.
     *
     * @throws IOException if problems
     */
    public synchronized static void displayRepaint() throws IOException {
        ScreenManager.getScreen().paint();
    }
}
