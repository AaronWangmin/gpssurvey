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
 * The Course Class - represents a heading (0-359 degrees)
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class Course {

    private final double value;

    /**
     *Constructor
     *
     * @param value the course (in degrees)
     */
    public Course(double value) {
        this.value = value;
    }

    /**
     * Get the course value.
     * @return the course value (in degrees)
     */
    public double get() {
        return value;
    }

    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Course clone() {
        return new Course(value);
    }
    
    @Override
    public String toString() {
        return String.format("%6.0f", value);
    }
    
    /**
     * Format the course value as a String. The string representation includes the
     * units symbol.
     *
     * @return the formated string
     */
    public String toStringWithUnits() {
        return String.format("%6.0f°", value);
    }
    
    // equals working to the accuracy of the formatted string (ie to 0 dp in this case)
    @Override
    public boolean equals(Object other) {
        if (other instanceof Course) {
            Course otherC = (Course) other;
            return this.toString().equals(otherC.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
