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
 * The HDOP class. HDOP is an acronym for horizontal dilution of precision.
 * 
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class HDOP {

    private final double value;

    /**
     * Constructor
     *
     * @param value the HDOP value
     */
    public HDOP(double value) {
        this.value = value;
    }

    /**
     * Get the HDOP value.
     *
     * @return the HDOP value (in metres)
     */
    public double get() {
        return value;
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public HDOP clone() {
        return new HDOP(value);
    }

    @Override
    public String toString() {
        return String.format("%6.2f", value);
    }
    
    // equals working to the accuracy of the formatted string (ie to 2 dp in this case)
    @Override
    public boolean equals(Object other) {
        if (other instanceof HDOP) {
            HDOP otherHdop= (HDOP) other;
            return this.toString().equals(otherHdop.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
