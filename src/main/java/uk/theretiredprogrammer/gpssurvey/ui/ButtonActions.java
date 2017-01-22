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
package uk.theretiredprogrammer.gpssurvey.ui;

import java.io.IOException;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction;
import uk.theretiredprogrammer.gpssurvey.input.IRControlAction.Button;
import uk.theretiredprogrammer.rpiembeddedlibrary.thread.Reporting;

/**
 * The Button Actions class - allows definition of Actions associated with
 * buttons.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ButtonActions implements IRControlAction {

    private static final int MAXLINES = 7;
    private final String title;
    private final String[] lines = new String[MAXLINES];
    private final Button[] buttons = new Button[MAXLINES];
    private final Action[] actions = new Action[MAXLINES];
    private int linecount = 0;
    private final String defaultline;
    private final Action defaultaction;

    /**
     * Constructor
     *
     * @param title the title to be displayed in any menu associated with these
     * button actions.
     * @param defaultline the text to be displayed for the default action
     * @param defaultaction the default action
     */
    public ButtonActions(String title, String defaultline, Action defaultaction) {
        this.title = title;
        this.defaultline = defaultline;
        this.defaultaction = defaultaction;
    }

    /**
     * Constructor - no default action defined.
     *
     * @param title the title to be displayed in any menu associated with these
     * button actions.
     */
    public ButtonActions(String title) {
        this(title, null, null);
    }

    /**
     * Constructor - no default action defined.
     */
    public ButtonActions() {
        this("", null, null);
    }

    /**
     * Add a button (without an action) and its associated menu text.
     *
     * @param button the button
     * @param text the menu text
     * @return this object
     * @throws IOException if problems
     */
    public ButtonActions addLine(Button button, String text) throws IOException {
        return addLine(button, text, null);
    }

    /**
     * Add a button with an action and no associated menu text.
     *
     * @param button the button
     * @param action the action for the button
     * @return this object
     * @throws IOException if problems
     */
    public ButtonActions addLine(Button button, Action action) throws IOException {
        return addLine(button, "", action);
    }

    /**
     * Add a button with an action and its associated menu text.
     *
     * @param button the button
     * @param text the menu text
     * @param action the action for the button
     * @return this object
     * @throws IOException if problems
     */
    public ButtonActions addLine(Button button, String text, Action action) throws IOException {
        if (linecount >= MAXLINES) {
            throw new IOException("Too many menu lines added");
        }
        lines[linecount] = text;
        buttons[linecount] = button;
        actions[linecount++] = action;
        return this;
    }

    /**
     * Get the formated menu for this set of button actions.
     *
     * @return the menu text
     */
    public String text() {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n\n");
        for (int i = 0; i < linecount; i++) {
            sb.append(buttons[i].name());
            sb.append(" = ");
            sb.append(lines[i]);
            sb.append("\n");
        }
        if (defaultline != null) {
            sb.append("Other buttons = ");
            sb.append(defaultline);
        }
        return sb.toString();
    }

    /**
     * Act on button. Undertake th explicit action if define for the button or
     * the default action if defined.
     *
     * @param button the button
     * @return true if action taken (either explicit or default)
     * @throws IOException if problems
     */
    @Override
    public boolean actionOnButton(Button button) throws IOException {
        Reporting.report("Screen", 3, "ButtonMenu processing command %s", button);
        for (int i = 0; i < linecount; i++) {
            if (button == buttons[i] && actions[i] != null) {
                actions[i].execute();
                return true;
            }
        }
        if (defaultaction != null) {
            defaultaction.execute();
        }
        return false;
    }
}
