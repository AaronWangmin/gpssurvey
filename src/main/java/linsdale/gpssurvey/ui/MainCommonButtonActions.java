package linsdale.gpssurvey.ui;

import java.io.IOException;
import linsdale.gpssurvey.informationstore.Controller;
import linsdale.gpssurvey.input.IRControlAction.Button;

/**
 *
 * @author richard
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
