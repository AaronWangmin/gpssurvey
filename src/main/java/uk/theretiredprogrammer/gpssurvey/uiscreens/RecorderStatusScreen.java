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
package uk.theretiredprogrammer.gpssurvey.uiscreens;

import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.Position;
import uk.theretiredprogrammer.gpssurvey.informationstore.Controller;
import uk.theretiredprogrammer.gpssurvey.informationstore.LocationData;
import uk.theretiredprogrammer.gpssurvey.ui.ScreenDataChangeProcessor;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.HybridArea;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.PixelPosition;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.Screen;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.CharSet;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Colour;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.SerialTFTDisplay.Font;
import uk.theretiredprogrammer.rpiembeddedlibrary.screen.TextZone;

/**
 * Screen displaying information about track recording.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class RecorderStatusScreen extends Screen implements ScreenDataChangeProcessor {

    private final static int HALFWIDTH = 80;
    private final static HybridArea fullwidthx1 = new HybridArea(160, 1);
    private final static HybridArea halfwidthx1 = new HybridArea(80, 1);
    //
    private final TextZone header;
    private final static PixelPosition headerpos = new PixelPosition(0, 2);
    private final TextZone pointcount;
    private final static PixelPosition pointcountpos = new PixelPosition(0, 20);
    private final TextZone recordedccount;
    private final static PixelPosition recordedccountpos = new PixelPosition(0, 38);
    //
    private final TextZone reference;
    private final static PixelPosition referencepos = new PixelPosition(0, 58);
    private final TextZone referencecourse;
    private final static PixelPosition referencecoursepos = new PixelPosition(0, 76);
    private final TextZone referencedistance;
    private final static PixelPosition referencedistancepos = new PixelPosition(HALFWIDTH, 76);
    //
    private final TextZone recording;
    private final static PixelPosition recordingpos = new PixelPosition(0, 96);
    private final TextZone recordingcourse;
    private final static PixelPosition recordingcoursepos = new PixelPosition(0, 114);
    private final TextZone recordingdistance;
    private final static PixelPosition recordingdistancepos = new PixelPosition(HALFWIDTH, 114);

    /**
     * Constructor.
     *
     * @param display the display device
     * @throws IOException if problems
     */
    public RecorderStatusScreen(SerialTFTDisplay display) throws IOException {
        super("recorderstatus", display);
        addZone(header = new TextZone(display, headerpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setCentre().setForeground(Colour.GREEN));
        header.insert("Recorder Status");
        //
        addZone(pointcount = new TextZone(display, pointcountpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1));
        addZone(recordedccount = new TextZone(display, recordedccountpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1));
        addZone(reference = new TextZone(display, referencepos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1));
        addZone(referencecourse = new TextZone(display, referencecoursepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(referencedistance = new TextZone(display, referencedistancepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(recording = new TextZone(display, recordingpos, fullwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1));
        addZone(recordingcourse = new TextZone(display, recordingcoursepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
        addZone(recordingdistance = new TextZone(display, recordingdistancepos, halfwidthx1, Font.SIZE_7x14, CharSet.ISO_8859_1)
                .setRight());
    }

    @Override
    protected void hasGainedFocus() throws IOException {
        dataChanged(Controller.getLocationData());
    }

    /**
     * Location Data change handler.
     *
     * @param ld new location data
     * @throws IOException if problems
     */
    @Override
    public void dataChanged(LocationData ld) throws IOException {
        pointcount.insert(String.format("Recorded %3d points", ld.getPointsSize()));
        recordedccount.insert(String.format("Recorded %3d tracks", ld.getStartEndPointsSize()));
        double refcourse = 0;
        if (ld.hasNoStartEndPoints()) {
            reference.insert("No Reference Track");
            referencecourse.insert("");
            referencedistance.insert("");
        } else {
            reference.insert("Reference Track");
            Position ref = ld.getFirstStartEnd();
            refcourse = ref.course.get();
            double inverse = refcourse >= 180 ? refcourse - 180 : refcourse + 180;
            referencecourse.insert(String.format("%3.0f° (%3.0f°)", refcourse, inverse));
            referencedistance.insert(ref.distance.toStringWithUnits());
        }
        if (ld.isRecording()) {
            recording.insert("Recording ...");
            Position current = ld.getPosition();
            recordingcourse.insert(current.course.toStringWithUnits());
            recordingdistance.insert(current.distance.toStringWithUnits());
        } else {
            if (ld.hasNoStartEndPoints()) {
                recording.insert("Not Recording");
                recordingcourse.insert("");
                recordingdistance.insert("");
            } else {
                Position offsetfromend = ld.getCurrentFromEnd();
                double offset = Math.abs(offsetfromend.distance.get()
                        * Math.sin(Math.toRadians(offsetfromend.course.get() - refcourse)));
                recording.insert("Not Recording");
                recordingcourse.insert("Offset: ");
                recordingdistance.insert(String.format("%8.1f m", offset));
            }
        }
        paint();
    }
}
