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
package uk.theretiredprogrammer.gpssurvey;

import java.util.Objects;

/**
 * The Latitude class.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
