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
import linsdale.gpssurvey.input.IRControlAction;
import linsdale.gpssurvey.input.IRControlAction.Button;
import linsdale.rpi.threadlib.Reporting;

/**
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

    public ButtonActions(String title, String defaultline, Action defaultaction) {
        this.title = title;
        this.defaultline = defaultline;
        this.defaultaction = defaultaction;
    }

    public ButtonActions(String title) {
        this(title, null, null);
    }

    public ButtonActions() {
        this("", null, null);
    }

    public ButtonActions addLine(Button button, String text) throws IOException {
        return addLine(button, text, null);
    }

    public ButtonActions addLine(Button button, Action action) throws IOException {
        return addLine(button, "", action);
    }

    public ButtonActions addLine(Button button, String text, Action action) throws IOException {
        if (linecount >= MAXLINES) {
            throw new IOException("Too many menu lines added");
        }
        lines[linecount] = text;
        buttons[linecount] = button;
        actions[linecount++] = action;
        return this;
    }

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
