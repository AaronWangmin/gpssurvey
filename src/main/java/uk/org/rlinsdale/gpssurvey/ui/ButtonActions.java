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

import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction.Button;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.Reporting;

/**
 * The Button Actions class - allows definition of Actions associated with
 * buttons.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
