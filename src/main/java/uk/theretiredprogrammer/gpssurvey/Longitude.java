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
 * The Longitude class.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
