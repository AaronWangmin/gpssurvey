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
 * The Depth Class - represents a depth in metres.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Depth {

    private final double value;

    /**
     * Constructor
     *
     * @param value the depth (in metres)
     */
    public Depth(double value) {
        this.value = value;
    }
    
    /**
     * Get the depth value.
     *
     * @return the depth value (in metres)
     */
    public double get() {
        return value;
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Depth clone() {
        return new Depth(value);
    }

    @Override
    public String toString() {
        return String.format("%8.2f", value);
    }
    
    /**
     * Format the depth value as a String. The string representation includes the
     * units symbol.
     *
     * @return the formated string
     */
    public String toStringWithUnits() {
        return String.format("%8.2f m", value);
    }
    
    // equals working to the accuracy of the formatted string (ie to 2 dp in this case)
    @Override
    public boolean equals(Object other) {
        if (other instanceof Depth) {
            Depth otherDepth = (Depth) other;
            return this.toString().equals(otherDepth.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value)<<6;
    }
}
