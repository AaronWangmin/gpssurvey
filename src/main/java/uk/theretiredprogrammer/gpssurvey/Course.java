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
 * The Course Class - represents a heading (0-359 degrees)
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
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
