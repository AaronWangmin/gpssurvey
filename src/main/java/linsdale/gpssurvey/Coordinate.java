package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
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
