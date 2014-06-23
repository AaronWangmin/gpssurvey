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
package uk.org.rlinsdale.gpssurvey;

import java.util.Objects;

/**
 * The Altitude Class.
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class Altitude {

    private final double value;

    /**
     * Constructor
     *
     * @param value the altitude (in metres)
     */
    public Altitude(double value) {
        this.value = value;
    }

    /**
     * Get the altitude value.
     *
     * @return the altitude value (in metres)
     */
    public double get() {
        return value;
    }

    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Altitude clone() {
        return new Altitude(value);
    }

    @Override
    public String toString() {
        return String.format("%8.1f", value);
    }

    /**
     * Format the altitude value as a String. The string representation includes the
     * units symbol.
     *
     * @return the formated string
     */
    public String toStringWithUnits() {
        return String.format("%8.1f m", value);
    }

    // equals working to the accuracy of the formatted string (ie to 1 dp in this case)
    @Override
    public boolean equals(Object other) {
        if (other instanceof Altitude) {
            Altitude otherAlt = (Altitude) other;
            return this.toString().equals(otherAlt.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value) << 6;
    }
}
