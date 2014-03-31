package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
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
