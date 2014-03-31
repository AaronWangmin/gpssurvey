package linsdale.gpssurvey;

import java.util.Objects;

/**
 *
 * @author richard
 */
public class HDOP {

    private final double value;

    public HDOP(double value) {
        this.value = value;
    }

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
