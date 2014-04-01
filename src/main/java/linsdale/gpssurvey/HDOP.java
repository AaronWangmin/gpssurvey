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
package linsdale.gpssurvey;

import java.util.Objects;

/**
 * The HDOP class. HDOP is an acronym for horizontal dilution of precision.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
