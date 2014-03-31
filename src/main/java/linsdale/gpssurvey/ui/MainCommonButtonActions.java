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
package linsdale.gpssurvey.ui;

import java.io.IOException;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.input.IRControlAction.Button;

/**
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class MainCommonButtonActions {

    public static ButtonActions getActions() throws IOException {
        return new ButtonActions()
                .addLine(Button.A, new RecordPositionAction())
                .addLine(Button.B, new ButtonBHandler())
                .addLine(Button.C, new ButtonCHandler())
                .addLine(Button.LEFT, new ButtonLeftHandler())
                .addLine(Button.RIGHT, new ButtonRightHandler());
    }

    public static class RecordPositionAction implements Action {

        @Override
        public void execute() throws IOException {
            Controller.recordPoint();
        }
    }

    public static class ButtonBHandler implements Action {

        @Override
        public void execute() throws IOException {
            Controller.startStopRecording();
        }
    }

    public static class ButtonCHandler implements Action {

        @Override
        public void execute() throws IOException {
            Controller.cancelRecording();
        }
    }
    
    public static class ButtonLeftHandler implements Action {

        @Override
        public void execute() throws IOException {
            View.displayPreviousScreen();
        }
    }
    
    public static class ButtonRightHandler implements Action {

        @Override
        public void execute() throws IOException {
            View.displayNextScreen();
        }
    }
}
