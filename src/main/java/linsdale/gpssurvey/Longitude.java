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
public class Longitude {

    private final double value;

    public Longitude(double value) {
        this.value = value;
    }

    public Longitude(double value, char direction) {
        this.value = (direction == 'E' ? +1 : -1) * value;
    }

    public double get() {
        return value;
    }

    public double getAbsolute() {
        return Math.abs(value);
    }

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
    
    public String toStringWithUnits() {
        return String.format("%11.6f°", value);
    }
    
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
