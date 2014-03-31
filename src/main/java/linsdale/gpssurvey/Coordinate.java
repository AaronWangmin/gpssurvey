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
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class Coordinate {

    private final double value;

    public Coordinate(double value) {
        this.value = value;
    }

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
