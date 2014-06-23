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
 * The Latitude class.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class Latitude {

    private final double value;

    /**
     * Constructor
     *
     * @param value the latitude (signed number between -90 and 90; +ve is north)
     */
    public Latitude(double value) {
        this.value = value;
    }

    /**
     * Constructor.
     * 
     * @param value the latitude (positive number between 0 and 90)
     * @param direction the direction ('N' or 'S')
     */
    public Latitude(double value, char direction) {
        this.value = (direction == 'N' ? +1 : -1) * value;
    }

    /**
     * Get the latitude value.
     *
     * @return the latitude (signed number; +ve is north)
     */
    public double get() {
        return value;
    }

    /**
     * Get the absolute latitude value.
     * 
     * @return the absolute latitude value (between 0 and 90)
     */
    public double getAbsolute() {
        return Math.abs(value);
    }

    /**
     * Get the latitude direction.
     * 
     * @return the direction ('N' or 'S')
     */
    public char getDirection() {
        return value >= 0 ? 'N' : 'S';
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Latitude clone() {
        return new Latitude(value);
    }
    
    @Override
    public String toString() {
        return String.format("%11.6f", value);
    }
    
    /**
     * Format the latitude value as a String. The string representation includes the
     * units symbol.
     *
     * @return the formated string
     */
    public String toStringWithUnits() {
        return String.format("%11.6f°", value);
    }
    
    // equals working to the accuracy of the formatted string (ie to 6 dp in this case)
    @Override
    public boolean equals(Object other) {
        if (other instanceof Latitude) {
            Latitude otherLat = (Latitude) other;
            return this.toString().equals(otherLat.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value)<<6;
    }
}
