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
public class Depth {

    private final double value;

    public Depth(double value) {
        this.value = value;
    }
    
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
    
    public String toStringWithUnits() {
        return String.format("%8.2f m", value);
    }
    
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
