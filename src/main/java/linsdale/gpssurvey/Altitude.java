package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
 */
public class Altitude {

    private final double value;

    public Altitude(double value) {
        this.value = value;
    }

    public double get() {
        return value;
    }

    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Altitude clone() {
        return new Altitude(value);
    }
    
    @Override
    public String toString() {
        return String.format("%8.1f", value);
    }
    
    public String toStringWithUnits() {
        return String.format("%8.1f m", value);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Altitude) {
            Altitude otherAlt = (Altitude) other;
            return this.toString().equals(otherAlt.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value)<<6;
    }
}
