package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
 */
public class Distance {

    private final double value;

    public Distance(double value) {
        this.value = value;
    }

    public double get() {
        return value;
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Distance clone() {
        return new Distance(value);
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
        if (other instanceof Distance) {
            Distance otherD = (Distance) other;
            return this.toString().equals(otherD.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
