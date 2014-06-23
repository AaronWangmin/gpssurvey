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
 * The Longitude class.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class Longitude {

    private final double value;

    /**
     * Constructor
     *
     * @param value the longitude (signed number between -180 and 180; +ve is east)
     */
    public Longitude(double value) {
        this.value = value;
    }

    /**
     * Constructor.
     * 
     * @param value the longitude (positive number between 0 and 180)
     * @param direction the direction ('E' or 'W')
     */
    public Longitude(double value, char direction) {
        this.value = (direction == 'E' ? +1 : -1) * value;
    }

    /**
     * Get the longitude value.
     *
     * @return the longitude (signed number; +ve is east)
     */
    public double get() {
        return value;
    }

    /**
     * Get the absolute longitude value.
     * 
     * @return the absolute longitude value (between 0 and 180)
     */
    public double getAbsolute() {
        return Math.abs(value);
    }

    /**
     * Get the longitude direction.
     * 
     * @return the direction ('E' or 'W')
     */
    public char getDirection() {
        return value >= 0 ? 'E' : 'W';
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Longitude clone() {
        return new Longitude(value);
    }
    
    @Override
    public String toString() {
        return String.format("%11.6f", value);
    }
    
    /**
     * Format the longitude value as a String. The string representation includes the
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
        if (other instanceof Longitude) {
            Longitude otherLong = (Longitude) other;
            return this.toString().equals(otherLong.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
