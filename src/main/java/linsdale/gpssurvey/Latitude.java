package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
 */
public class Latitude {

    private final double value;

    public Latitude(double value) {
        this.value = value;
    }

    public Latitude(double value, char direction) {
        this.value = (direction == 'N' ? +1 : -1) * value;
    }

    public double get() {
        return value;
    }

    public double getAbsolute() {
        return Math.abs(value);
    }

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
    
    public String toStringWithUnits() {
        return String.format("%11.6f°", value);
    }
    
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
