package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
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
