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
 * A Coordinate class.  Represents a distances.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Coordinate {

    private final double value;

    /**
     * Constructor.
     * 
     * @param value the coordinate value (in metres)
     */
    public Coordinate(double value) {
        this.value = value;
    }

    /**
     * Get the coordinate value.
     * 
     * @return teh coordinate value (in metres)
     */
    public double get() {
        return value;
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Coordinate clone() {
        return new Coordinate(value);
    }

    @Override
    public String toString() {
        return String.format("%9.1f", value);
    }
    
    /**
     * Format the coordinate value as a String. The string representation includes the
     * units symbol.
     *
     * @return the formated string
     */
    public String toStringWithUnits() {
        return String.format("%9.1f m", value);
    }
    
    // equals working to the accuracy of the formatted string (ie to 1 dp in this case)
    @Override
    public boolean equals(Object other) {
        if (other instanceof Coordinate) {
            Coordinate otherC = (Coordinate) other;
            return this.toString().equals(otherC.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value)<<6;
    }
}
